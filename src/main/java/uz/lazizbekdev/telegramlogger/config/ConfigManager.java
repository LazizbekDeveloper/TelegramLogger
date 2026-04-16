package uz.lazizbekdev.telegramlogger.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uz.lazizbekdev.telegramlogger.TelegramLogger;
import uz.lazizbekdev.telegramlogger.utils.MessageUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Handles config loading, saving, validation, and automatic restore.
 * When the config file is corrupt or invalid, the old file is backed up
 * and a fresh config is generated with values migrated from the backup.
 */
public class ConfigManager {

    private final TelegramLogger plugin;
    private final Logger logger;
    private File configFile;

    // Bot settings
    private String botToken = "";
    private String chatId = "";
    private String threadId = "";
    private boolean sendToThread = false;
    private boolean sendTelegramMessagesToGame = false;
    private boolean debugMode = false;

    // Prefix & format
    private String pluginPrefix = "&6&lTelegramLogger&7 \u279C &r&a";
    private String telegramGameMessage = "&7[&9TG&7] &c%name% &8\u00BB &f%message%";

    // Server events
    private boolean enableServerStartStop = true;
    private String serverStartMessage = "<blockquote>\uD83D\uDFE2 <b>Server started!</b>\nVersion: %version% | Max: %max%</blockquote>";
    private String serverStopMessage = "<blockquote>\uD83D\uDD34 <b>Server stopped!</b></blockquote>";

    // Join/Leave
    private boolean enableJoin = true;
    private String joinMessage = "<blockquote>\u2795 <b>%player%</b> joined! (%online%/%max%)</blockquote>";
    private boolean enableLeave = true;
    private String leaveMessage = "<blockquote>\u2796 <b>%player%</b> left! (%online%/%max%)</blockquote>";

    // First join
    private boolean enableFirstJoin = true;
    private String firstJoinMessage = "<blockquote>\uD83C\uDF1F <b>%player%</b> joined the server for the first time! Welcome! (%online%/%max%)</blockquote>";

    // Chat
    private boolean enableChat = true;
    private String chatMessage = "\uD83D\uDCAC <b>%player%</b> \u27A5 %message%";

    // Advancement
    private boolean enableAdvancement = true;
    private String advancementMessage = "<blockquote>\uD83C\uDFC6 <b>%player%</b> earned <b>[%advancement%]</b> (%online%/%max%)</blockquote>";

    // Death
    private boolean enableDeath = true;
    private String deathMessage = "<blockquote>\uD83D\uDC80 %death_message% (%online%/%max%)</blockquote>";

    // World switch
    private boolean enableWorldSwitch = true;
    private String worldSwitchMessage = "<blockquote>\uD83C\uDF0D <b>%player%</b> moved: %from_world% \u2192 %to_world% (%online%/%max%)</blockquote>";

    // Pet death
    private boolean enablePetDeath = true;
    private String petDeathMessage = "<blockquote>\uD83D\uDC3E <b>%player%</b> killed <b>%owner%</b>'s %pet%! (%online%/%max%)</blockquote>";

    // Chat filter
    private boolean enableChatFilter = true;
    private List<String> filteredWords = new ArrayList<>();
    private String filteredMessage = "<blockquote>\uD83D\uDEAB <b>%player%</b> used a filtered word. (%online%/%max%)</blockquote>";

    // Command execution logging
    private boolean enableSendCommandExecutes = false;
    private String commandExecuteMessage = "<blockquote>\uD83D\uDCA0 <b>%player%</b> \u27A5 <code>%command%</code> (%online%/%max%)</blockquote>";
    private String commandExecutesChatId = "";
    private boolean sendCommandExecutesToThread = false;
    private String commandExecutesGroupThreadId = "";
    private List<String> ignoredCommands = new ArrayList<>();

    // Sudo
    private boolean enableSudoCommand = false;
    private boolean sudoShowOutput = true;
    private List<String> sudoBlacklist = new ArrayList<>();

    // Prefix replacements
    private Map<String, String> prefixReplacements = new LinkedHashMap<>();

    // Anti-flood
    private boolean antiFloodEnabled = true;
    private int antiFloodMaxMessages = 20;
    private long antiFloodWindowSeconds = 10;

    // Error messages
    private String errorNotAdmin = "<blockquote>\u274C You are not registered as an admin!</blockquote>";

    // Telegram to Game enhancements
    private boolean enableTelegramToTelegramRelay = true;
    private String telegramToTelegramRelayFormat = "[TG] %name%: %message%";
    private boolean enableMentionNotifications = true;
    private String mentionSound = "BLOCK_NOTE_BLOCK_BELL";
    private float mentionVolume = 1.0f;
    private float mentionPitch = 1.0f;
    private String mentionTitle = "&eYou were mentioned in chat!";
    private String mentionSubtitle = "&fBy &6%name%";
    private int mentionTitleFadeIn = 10;
    private int mentionTitleStay = 40;
    private int mentionTitleFadeOut = 10;
    private String mentionActionbar = "&e&l⭐ MENTIONED BY %name% ⭐";
    private int mentionActionbarDuration = 60;
    private String mentionBossBar = "&eYou were mentioned by &f%name%";
    private int mentionBossBarDuration = 80;
    private String mentionHighlightColor = "&e";


    // Version
    private String configVersion = TelegramLogger.PLUGIN_VERSION;

    public ConfigManager(TelegramLogger plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    /**
     * Load configuration. If the file is corrupt, triggers restore.
     */
    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        FileConfiguration config;
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            config = YamlConfiguration.loadConfiguration(reader);
        } catch (Exception e) {
            logger.severe("Config file is corrupt! Starting restore process...");
            restoreConfig();
            return;
        }

        applyValues(config);

        if (!isValid()) {
            logger.warning("Config has invalid values. Check your config.yml");
        }
    }

    /**
     * Reload config from disk.
     */
    public void reload() {
        load();
    }

    /**
     * Apply values from a loaded FileConfiguration.
     */
    private void applyValues(FileConfiguration cfg) {
        botToken = cfg.getString("bot_token", "BOT_TOKEN");
        chatId = cfg.getString("chat_id", "CHAT_ID");
        threadId = cfg.getString("thread_id", "THREAD_ID");
        sendToThread = cfg.getBoolean("send_to_thread", false);
        sendTelegramMessagesToGame = cfg.getBoolean("send_telegram_messages_to_game", false);
        debugMode = cfg.getBoolean("debug_mode", false);

        pluginPrefix = cfg.getString("plugin_prefix", pluginPrefix);
        telegramGameMessage = cfg.getString("telegram_game_message", telegramGameMessage);

        enableServerStartStop = cfg.getBoolean("enable_server_start_stop", true);
        serverStartMessage = cfg.getString("server_start_message", serverStartMessage);
        serverStopMessage = cfg.getString("server_stop_message", serverStopMessage);

        enableJoin = cfg.getBoolean("enable_join", true);
        joinMessage = cfg.getString("join_message", joinMessage);
        enableLeave = cfg.getBoolean("enable_leave", true);
        leaveMessage = cfg.getString("leave_message", leaveMessage);

        enableFirstJoin = cfg.getBoolean("enable_first_join", true);
        firstJoinMessage = cfg.getString("first_join_message", firstJoinMessage);

        enableChat = cfg.getBoolean("enable_chat", true);
        chatMessage = cfg.getString("chat_message", chatMessage);

        enableAdvancement = cfg.getBoolean("enable_advancement", true);
        advancementMessage = cfg.getString("advancement_message", advancementMessage);

        enableDeath = cfg.getBoolean("enable_death", true);
        deathMessage = cfg.getString("death_message", deathMessage);

        enableWorldSwitch = cfg.getBoolean("enable_world_switch", true);
        worldSwitchMessage = cfg.getString("world_switch_message", worldSwitchMessage);

        enablePetDeath = cfg.getBoolean("enable_pet_death", true);
        petDeathMessage = cfg.getString("pet_death_message", petDeathMessage);

        enableChatFilter = cfg.getBoolean("enable_chat_filter", true);
        filteredWords = cfg.getStringList("filtered_words").stream()
                .map(String::toLowerCase).collect(Collectors.toList());
        filteredMessage = cfg.getString("filtered_message", filteredMessage);

        enableSendCommandExecutes = cfg.getBoolean("enable_send_command_executes", false);
        commandExecuteMessage = cfg.getString("command_execute_message", commandExecuteMessage);
        commandExecutesChatId = cfg.getString("command_executes_chat_id", "");
        sendCommandExecutesToThread = cfg.getBoolean("send_command_executes_to_thread", false);
        commandExecutesGroupThreadId = cfg.getString("command_executes_group_thread_id", "");
        ignoredCommands = cfg.getStringList("ignored_commands").stream()
                .map(String::toLowerCase).collect(Collectors.toList());

        enableSudoCommand = cfg.getBoolean("enable_sudo_command", false);
        sudoShowOutput = cfg.getBoolean("sudo_show_output", true);
        sudoBlacklist = cfg.getStringList("sudo_blacklist").stream()
                .map(String::toLowerCase).collect(Collectors.toList());

        prefixReplacements = new LinkedHashMap<>();
        if (cfg.isConfigurationSection("prefix_replacements")) {
            for (String key : cfg.getConfigurationSection("prefix_replacements").getKeys(false)) {
                String value = cfg.getString("prefix_replacements." + key, "");
                prefixReplacements.put(key, value);
            }
        }

        antiFloodEnabled = cfg.getBoolean("anti_flood_enabled", true);
        antiFloodMaxMessages = cfg.getInt("anti_flood_max_messages", 20);
        antiFloodWindowSeconds = cfg.getLong("anti_flood_window_seconds", 10);

        errorNotAdmin = cfg.getString("error_not_admin", errorNotAdmin);

        enableTelegramToTelegramRelay = cfg.getBoolean("telegram_to_game_enhancements.enable_telegram_to_telegram_relay", true);
        telegramToTelegramRelayFormat = cfg.getString("telegram_to_game_enhancements.telegram_to_telegram_relay_format", "[TG] %name%: %message%");
        enableMentionNotifications = cfg.getBoolean("telegram_to_game_enhancements.enable_mention_notifications", true);
        mentionSound = cfg.getString("telegram_to_game_enhancements.mention_sound", "BLOCK_NOTE_BLOCK_BELL");
        mentionVolume = (float) cfg.getDouble("telegram_to_game_enhancements.mention_volume", 1.0);
        mentionPitch = (float) cfg.getDouble("telegram_to_game_enhancements.mention_pitch", 1.0);
        mentionTitle = cfg.getString("telegram_to_game_enhancements.mention_title", "&eYou were mentioned in chat!");
        mentionSubtitle = cfg.getString("telegram_to_game_enhancements.mention_subtitle", "&fBy &6%name%");
        mentionTitleFadeIn = cfg.getInt("telegram_to_game_enhancements.mention_title_fade_in", 10);
        mentionTitleStay = cfg.getInt("telegram_to_game_enhancements.mention_title_stay", 40);
        mentionTitleFadeOut = cfg.getInt("telegram_to_game_enhancements.mention_title_fade_out", 10);
        mentionActionbar = cfg.getString("telegram_to_game_enhancements.mention_actionbar", "&e&l⭐ MENTIONED BY %name% ⭐");
        mentionActionbarDuration = cfg.getInt("telegram_to_game_enhancements.mention_actionbar_duration", 60);
        mentionBossBar = cfg.getString("telegram_to_game_enhancements.mention_boss_bar", "&eYou were mentioned by &f%name%");
        mentionBossBarDuration = cfg.getInt("telegram_to_game_enhancements.mention_boss_bar_duration", 80);
        mentionHighlightColor = cfg.getString("telegram_to_game_enhancements.mention_highlight_color", "&e");

        configVersion = cfg.getString("version", TelegramLogger.PLUGIN_VERSION);
    }

    /**
     * Backup a corrupt config, create a fresh one, and migrate recoverable values.
     */
    private void restoreConfig() {
        String timestamp = MessageUtils.getDateTimestamp();
        File backup = new File(plugin.getDataFolder(), "config.yml.broken." + timestamp);

        if (configFile.renameTo(backup)) {
            logger.info("Backed up broken config to: " + backup.getName());
        } else {
            logger.severe("Could not backup broken config file!");
        }

        // Extract the default config from the jar
        plugin.saveResource("config.yml", false);

        // Attempt to recover values from the broken file line by line
        Map<String, String> recovered = recoverValues(backup);
        if (!recovered.isEmpty()) {
            logger.info("Recovered " + recovered.size() + " values from broken config.");
            mergeRecoveredValues(recovered);
        }

        // Load the new config
        load();
    }

    /**
     * Line-by-line recovery from a potentially corrupt YAML file.
     * Extracts simple key: value pairs.
     */
    private Map<String, String> recoverValues(File brokenFile) {
        Map<String, String> values = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(brokenFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                int colonIdx = line.indexOf(':');
                if (colonIdx <= 0) continue;

                String key = line.substring(0, colonIdx).trim();
                String value = line.substring(colonIdx + 1).trim();

                // Strip inline comments
                if (value.contains(" #")) {
                    value = value.substring(0, value.indexOf(" #")).trim();
                }
                // Strip surrounding quotes
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                // Skip default/placeholder values
                if (value.equals("BOT_TOKEN") || value.equals("CHAT_ID") || value.equals("THREAD_ID")) {
                    continue;
                }

                if (!value.isEmpty()) {
                    values.put(key, value);
                }
            }
        } catch (Exception e) {
            logger.warning("Could not recover values from broken config: " + e.getMessage());
        }
        return values;
    }

    /**
     * Merge recovered key-value pairs into the freshly created config file.
     */
    private void mergeRecoveredValues(Map<String, String> recovered) {
        try {
            FileConfiguration freshConfig;
            try (InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                freshConfig = YamlConfiguration.loadConfiguration(reader);
            }

            for (Map.Entry<String, String> entry : recovered.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (!freshConfig.contains(key)) continue;

                // Preserve type of the existing default value
                Object existing = freshConfig.get(key);
                if (existing instanceof Boolean) {
                    freshConfig.set(key, Boolean.parseBoolean(value));
                } else if (existing instanceof Integer) {
                    try { freshConfig.set(key, Integer.parseInt(value)); } catch (NumberFormatException ignored) {}
                } else if (existing instanceof Long) {
                    try { freshConfig.set(key, Long.parseLong(value)); } catch (NumberFormatException ignored) {}
                } else {
                    freshConfig.set(key, value);
                }
            }

            freshConfig.save(configFile);
        } catch (Exception e) {
            logger.warning("Failed to merge recovered config values: " + e.getMessage());
        }
    }

    /**
     * Check whether the essential config values are properly set.
     */
    public boolean isValid() {
        if (botToken == null || botToken.isEmpty() || botToken.equals("BOT_TOKEN")) return false;
        if (chatId == null || chatId.isEmpty() || chatId.equals("CHAT_ID")) return false;
        if (sendToThread && (threadId == null || threadId.isEmpty() || threadId.equals("THREAD_ID"))) return false;
        if (enableSendCommandExecutes) {
            if (commandExecutesChatId == null || commandExecutesChatId.isEmpty()
                    || commandExecutesChatId.equals("CHAT_ID")) return false;
            if (sendCommandExecutesToThread && (commandExecutesGroupThreadId == null
                    || commandExecutesGroupThreadId.isEmpty()
                    || commandExecutesGroupThreadId.equals("THREAD_ID"))) return false;
        }
        return true;
    }

    // ─── Getters ─────────────────────────────────────────

    public String getBotToken() { return botToken; }
    public String getChatId() { return chatId; }
    public String getThreadId() { return threadId; }
    public boolean isSendToThread() { return sendToThread; }
    public boolean isSendTelegramMessagesToGame() { return sendTelegramMessagesToGame; }
    public boolean isDebugMode() { return debugMode; }
    public void setDebugMode(boolean debug) { this.debugMode = debug; }

    public String getPluginPrefix() { return MessageUtils.colorize(pluginPrefix); }
    public String getRawPluginPrefix() { return pluginPrefix; }
    public String getTelegramGameMessage() { return telegramGameMessage; }

    public boolean isEnableServerStartStop() { return enableServerStartStop; }
    public String getServerStartMessage() { return serverStartMessage; }
    public String getServerStopMessage() { return serverStopMessage; }

    public boolean isEnableJoin() { return enableJoin; }
    public String getJoinMessage() { return joinMessage; }
    public boolean isEnableLeave() { return enableLeave; }
    public String getLeaveMessage() { return leaveMessage; }

    public boolean isEnableFirstJoin() { return enableFirstJoin; }
    public String getFirstJoinMessage() { return firstJoinMessage; }

    public boolean isEnableChat() { return enableChat; }
    public String getChatMessage() { return chatMessage; }

    public boolean isEnableAdvancement() { return enableAdvancement; }
    public String getAdvancementMessage() { return advancementMessage; }

    public boolean isEnableDeath() { return enableDeath; }
    public String getDeathMessage() { return deathMessage; }

    public boolean isEnableWorldSwitch() { return enableWorldSwitch; }
    public String getWorldSwitchMessage() { return worldSwitchMessage; }

    public boolean isEnablePetDeath() { return enablePetDeath; }
    public String getPetDeathMessage() { return petDeathMessage; }

    public boolean isEnableChatFilter() { return enableChatFilter; }
    public List<String> getFilteredWords() { return filteredWords; }
    public String getFilteredMessage() { return filteredMessage; }

    public boolean isEnableSendCommandExecutes() { return enableSendCommandExecutes; }
    public String getCommandExecuteMessage() { return commandExecuteMessage; }
    public String getCommandExecutesChatId() { return commandExecutesChatId; }
    public boolean isSendCommandExecutesToThread() { return sendCommandExecutesToThread; }
    public String getCommandExecutesGroupThreadId() { return commandExecutesGroupThreadId; }
    public List<String> getIgnoredCommands() { return ignoredCommands; }

    public boolean isEnableSudoCommand() { return enableSudoCommand; }
    public boolean isSudoShowOutput() { return sudoShowOutput; }
    public List<String> getSudoBlacklist() { return sudoBlacklist; }

    public boolean isAntiFloodEnabled() { return antiFloodEnabled; }
    public int getAntiFloodMaxMessages() { return antiFloodMaxMessages; }
    public long getAntiFloodWindowSeconds() { return antiFloodWindowSeconds; }

    public String getErrorNotAdmin() { return errorNotAdmin; }
    public Map<String, String> getPrefixReplacements() { return prefixReplacements; }

    // Telegram to Game enhancements
    public boolean isEnableTelegramToTelegramRelay() { return enableTelegramToTelegramRelay; }
    public String getTelegramToTelegramRelayFormat() { return telegramToTelegramRelayFormat; }
    public boolean isEnableMentionNotifications() { return enableMentionNotifications; }
    public String getMentionSound() { return mentionSound; }
    public float getMentionVolume() { return mentionVolume; }
    public float getMentionPitch() { return mentionPitch; }
    public String getMentionTitle() { return mentionTitle; }
    public String getMentionSubtitle() { return mentionSubtitle; }
    public int getMentionTitleFadeIn() { return mentionTitleFadeIn; }
    public int getMentionTitleStay() { return mentionTitleStay; }
    public int getMentionTitleFadeOut() { return mentionTitleFadeOut; }
    public String getMentionActionbar() { return mentionActionbar; }
    public int getMentionActionbarDuration() { return mentionActionbarDuration; }
    public String getMentionBossBar() { return mentionBossBar; }
    public int getMentionBossBarDuration() { return mentionBossBarDuration; }
    public String getMentionHighlightColor() { return mentionHighlightColor; }
}
