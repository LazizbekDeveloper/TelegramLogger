package uz.lazizbekdev.telegramlogger.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.AnimalTamer;
import uz.lazizbekdev.telegramlogger.TelegramLogger;
import uz.lazizbekdev.telegramlogger.config.ConfigManager;
import uz.lazizbekdev.telegramlogger.managers.DataManager;
import uz.lazizbekdev.telegramlogger.telegram.TelegramAPI;
import uz.lazizbekdev.telegramlogger.utils.AntiFloodManager;
import uz.lazizbekdev.telegramlogger.utils.MessageUtils;

import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles all Minecraft server events and forwards them to Telegram.
 */
public class EventListener implements Listener {

    private final TelegramLogger plugin;

    public EventListener(TelegramLogger plugin) {
        this.plugin = plugin;
    }

    private static Object vaultChat = null;
    private static boolean vaultChecked = false;

    private ConfigManager cfg() { return plugin.getConfigManager(); }
    private TelegramAPI api() { return plugin.getTelegramAPI(); }
    private DataManager data() { return plugin.getDataManager(); }
    private AntiFloodManager flood() { return plugin.getAntiFloodManager(); }

    // ─── Join ───────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.isPluginActive() || !cfg().isEnableJoin()) return;
        if (!flood().tryAcquire("join")) return;

        Player player = event.getPlayer();
        Map<String, String> ph = basePlayerPlaceholders(player);

        // First join detection
        if (!player.hasPlayedBefore() && cfg().isEnableFirstJoin()) {
            String msg = MessageUtils.applyPlaceholders(cfg().getFirstJoinMessage(), ph);
            api().sendMessage(MessageUtils.stripColors(msg));
            data().incrementStat("first_join_messages");
        } else {
            String msg = MessageUtils.applyPlaceholders(cfg().getJoinMessage(), ph);
            api().sendMessage(MessageUtils.stripColors(msg));
            data().incrementStat("join_messages");
        }

        // Version check for admins
        if (player.isOp() || player.hasPermission("telegramlogger.admin")) {
            checkVersionAsync(player);
        }
    }

    // ─── Quit ───────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.isPluginActive() || !cfg().isEnableLeave()) return;
        if (!flood().tryAcquire("leave")) return;

        Player player = event.getPlayer();
        Map<String, String> ph = basePlayerPlaceholders(player);
        // Adjust online count since the player is leaving
        ph.put("%online%", String.valueOf(Math.max(0, Bukkit.getOnlinePlayers().size() - 1)));

        String msg = MessageUtils.applyPlaceholders(cfg().getLeaveMessage(), ph);
        api().sendMessage(MessageUtils.stripColors(msg));
        data().incrementStat("leave_messages");
    }

    // ─── Chat ───────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.isPluginActive() || !cfg().isEnableChat()) return;
        
        // Skip if message is empty
        if (event.getMessage() == null || event.getMessage().trim().isEmpty()) return;
        
        if (!flood().tryAcquire("chat")) return;

        Player player = event.getPlayer();

        // Chat filter
        if (cfg().isEnableChatFilter() && containsFilteredWord(event.getMessage())) {
            handleFilteredMessage(player);
            return;
        }

        Map<String, String> ph = basePlayerPlaceholders(player);
        // Clean chat component tags injected by other plugins (mentions, etc.)
        String cleanMessage = MessageUtils.cleanChatComponents(event.getMessage());
        cleanMessage = MessageUtils.stripColors(cleanMessage);
        ph.put("%message%", MessageUtils.escapeHtml(cleanMessage));

        String msg = MessageUtils.applyPlaceholders(cfg().getChatMessage(), ph);
        api().sendMessage(MessageUtils.stripColors(msg));
        data().incrementStat("chat_messages");
    }

    // ─── Command execute ────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.isPluginActive() || !cfg().isEnableSendCommandExecutes()) return;

        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();

        // Check ignored commands
        if (cfg().getIgnoredCommands().stream().anyMatch(command::startsWith)) {
            data().incrementIgnoredCommands();
            return;
        }

        if (!flood().tryAcquire("command")) return;

        Map<String, String> ph = basePlayerPlaceholders(player);
        ph.put("%command%", MessageUtils.escapeHtml(event.getMessage()));

        String msg = MessageUtils.applyPlaceholders(cfg().getCommandExecuteMessage(), ph);
        api().sendCommandExecute(MessageUtils.stripColors(msg));
        data().incrementStat("command_messages");
        data().incrementCommandTracking();
    }

    // ─── Advancement ────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        if (!plugin.isPluginActive() || !cfg().isEnableAdvancement()) return;

        // Skip recipe unlocks
        if (event.getAdvancement().getKey().getKey().contains("recipes/")) return;

        if (!flood().tryAcquire("advancement")) return;

        Player player = event.getPlayer();
        String advName = MessageUtils.formatAdvancement(event.getAdvancement().getKey().getKey());

        Map<String, String> ph = basePlayerPlaceholders(player);
        ph.put("%advancement%", advName);

        String msg = MessageUtils.applyPlaceholders(cfg().getAdvancementMessage(), ph);
        api().sendMessage(MessageUtils.stripColors(msg));
        data().incrementStat("advancement_messages");
    }

    // ─── Death ──────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.isPluginActive() || !cfg().isEnableDeath()) return;
        if (!flood().tryAcquire("death")) return;

        Player player = event.getEntity();
        String deathMsg = event.getDeathMessage() != null
                ? event.getDeathMessage() : player.getName() + " died";

        Map<String, String> ph = basePlayerPlaceholders(player);
        ph.put("%death_message%", MessageUtils.escapeHtml(deathMsg));

        String msg = MessageUtils.applyPlaceholders(cfg().getDeathMessage(), ph);
        api().sendMessage(MessageUtils.stripColors(msg));
        data().incrementStat("death_messages");
    }

    // ─── Pet death ──────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.isPluginActive() || !cfg().isEnablePetDeath()) return;
        if (!(event.getEntity().getKiller() instanceof Player)) return;
        if (!(event.getEntity() instanceof Tameable)) return;

        Tameable pet = (Tameable) event.getEntity();
        if (!pet.isTamed() || pet.getOwner() == null) return;

        Player killer = event.getEntity().getKiller();
        AnimalTamer owner = pet.getOwner();

        // Don't log if owner is the killer
        if (owner.getUniqueId().equals(killer.getUniqueId())) return;

        if (!flood().tryAcquire("death")) return;

        Map<String, String> ph = basePlayerPlaceholders(killer);
        ph.put("%owner%", owner.getName() != null ? owner.getName() : "Unknown");
        ph.put("%pet%", pet.getType().getName().toLowerCase().replace("_", " "));

        String msg = MessageUtils.applyPlaceholders(cfg().getPetDeathMessage(), ph);
        api().sendMessage(MessageUtils.stripColors(msg));
        data().incrementStat("pet_death_messages");
    }

    // ─── World change ───────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (!plugin.isPluginActive() || !cfg().isEnableWorldSwitch()) return;
        if (!flood().tryAcquire("world_switch")) return;

        Player player = event.getPlayer();

        Map<String, String> ph = basePlayerPlaceholders(player);
        ph.put("%from_world%", MessageUtils.formatWorldName(event.getFrom().getName()));
        ph.put("%to_world%", MessageUtils.formatWorldName(player.getWorld().getName()));

        String msg = MessageUtils.applyPlaceholders(cfg().getWorldSwitchMessage(), ph);
        api().sendMessage(MessageUtils.stripColors(msg));
        data().incrementStat("world_switch_messages");
    }

    // ─── Helpers ────────────────────────────────────────

    private Map<String, String> basePlayerPlaceholders(Player player) {
        Map<String, String> ph = new LinkedHashMap<>();
        ph.put("%player%", player.getName());

        String rawDisplay = MessageUtils.stripColors(player.getDisplayName());
        Map<String, String> replacements = cfg().getPrefixReplacements();
        String origDisplay = rawDisplay;
        rawDisplay = MessageUtils.applyPrefixReplacements(rawDisplay, replacements);
        ph.put("%displayname%", rawDisplay.equals(origDisplay) ? MessageUtils.escapeHtml(rawDisplay) : rawDisplay);

        // Try Vault first (works with LuckPerms, PEX, etc.), fall back to display name extraction
        String prefix = getVaultPrefix(player);
        String suffix = getVaultSuffix(player);
        if (prefix.isEmpty()) {
            prefix = MessageUtils.extractPrefix(player.getDisplayName(), player.getName());
        }
        if (suffix.isEmpty()) {
            suffix = MessageUtils.extractSuffix(player.getDisplayName(), player.getName());
        }

        // Apply prefix_replacements: if a replacement matched, the value is
        // trusted HTML from config (e.g. <tg-emoji>) so don't escape it.
        // If no replacement matched, escape for safety.
        String origPrefix = prefix;
        String origSuffix = suffix;
        prefix = MessageUtils.applyPrefixReplacements(prefix, replacements);
        suffix = MessageUtils.applyPrefixReplacements(suffix, replacements);
        ph.put("%prefix%", prefix.equals(origPrefix) ? MessageUtils.escapeHtml(prefix) : prefix);
        ph.put("%suffix%", suffix.equals(origSuffix) ? MessageUtils.escapeHtml(suffix) : suffix);

        ph.put("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        ph.put("%max%", String.valueOf(Bukkit.getMaxPlayers()));
        return ph;
    }

    private void initVaultChat() {
        if (vaultChecked) return;
        vaultChecked = true;
        try {
            Class<?> chatClass = Class.forName("net.milkbowl.vault.chat.Chat");
            RegisteredServiceProvider<?> rsp = Bukkit.getServer().getServicesManager().getRegistration(chatClass);
            if (rsp != null) {
                vaultChat = rsp.getProvider();
                plugin.getLogger().info("Vault Chat provider found: " + vaultChat.getClass().getSimpleName());
            }
        } catch (ClassNotFoundException ignored) {
            // Vault not installed
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to hook into Vault Chat: " + e.getMessage());
        }
    }

    private String getVaultPrefix(Player player) {
        initVaultChat();
        if (vaultChat == null) return "";
        try {
            Method m = vaultChat.getClass().getMethod("getPlayerPrefix", Player.class);
            String result = (String) m.invoke(vaultChat, player);
            return result != null ? MessageUtils.stripColors(result).trim() : "";
        } catch (NoSuchMethodException e) {
            try {
                Method m = vaultChat.getClass().getMethod("getPlayerPrefix", String.class, String.class);
                String result = (String) m.invoke(vaultChat, player.getWorld().getName(), player.getName());
                return result != null ? MessageUtils.stripColors(result).trim() : "";
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return "";
    }

    private String getVaultSuffix(Player player) {
        initVaultChat();
        if (vaultChat == null) return "";
        try {
            Method m = vaultChat.getClass().getMethod("getPlayerSuffix", Player.class);
            String result = (String) m.invoke(vaultChat, player);
            return result != null ? MessageUtils.stripColors(result).trim() : "";
        } catch (NoSuchMethodException e) {
            try {
                Method m = vaultChat.getClass().getMethod("getPlayerSuffix", String.class, String.class);
                String result = (String) m.invoke(vaultChat, player.getWorld().getName(), player.getName());
                return result != null ? MessageUtils.stripColors(result).trim() : "";
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return "";
    }

    private boolean containsFilteredWord(String message) {
        String lower = message.toLowerCase();
        return cfg().getFilteredWords().stream().anyMatch(lower::contains);
    }

    private void handleFilteredMessage(Player player) {
        Map<String, String> ph = basePlayerPlaceholders(player);
        String msg = MessageUtils.applyPlaceholders(cfg().getFilteredMessage(), ph);
        api().sendMessage(MessageUtils.stripColors(msg));
        data().incrementStat("filtered_messages");
    }

    private void checkVersionAsync(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            try {
                String latestVersion = getLatestVersion();
                if (latestVersion != null && !latestVersion.equals(TelegramLogger.PLUGIN_VERSION)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        try {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
                        } catch (Exception ignored) {}
                        String updateMsg = formatUpdateMessage(latestVersion);
                        for (String line : updateMsg.split("\n")) {
                            player.sendMessage(MessageUtils.colorize(line));
                        }
                    });
                }
            } catch (Exception ignored) {}
        }, 60L);
    }

    private String getLatestVersion() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/LazizbekDeveloper/TelegramLogger/refs/heads/main/src/main/resources/config.yml");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() != 200) return null;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("version:")) {
                        return line.split(":")[1].trim().replace("\"", "");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String formatUpdateMessage(String newVersion) {
        return "&d&l\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\n" +
               "&f&l      \uD83D\uDCE2 &6&lTelegramLogger &f&lUpdate!\n" +
               "&fCurrent: &c" + TelegramLogger.PLUGIN_VERSION + " &8\u25BA &fLatest: &a" + newVersion + "\n" +
               "&e\u2728 &7Download: &b&nhttps://spigotmc.org/resources/120590\n" +
               "&d&l\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0\u25B0";
    }
}
