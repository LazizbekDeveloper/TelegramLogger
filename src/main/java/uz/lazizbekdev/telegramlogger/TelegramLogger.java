package uz.lazizbekdev.telegramlogger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import uz.lazizbekdev.telegramlogger.commands.CommandHandler;
import uz.lazizbekdev.telegramlogger.config.ConfigManager;
import uz.lazizbekdev.telegramlogger.listeners.EventListener;
import uz.lazizbekdev.telegramlogger.managers.AdminManager;
import uz.lazizbekdev.telegramlogger.managers.DataManager;
import uz.lazizbekdev.telegramlogger.telegram.TelegramAPI;
import uz.lazizbekdev.telegramlogger.telegram.TelegramHandler;
import uz.lazizbekdev.telegramlogger.utils.AntiFloodManager;
import uz.lazizbekdev.telegramlogger.utils.MessageUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TelegramLogger - Minecraft ↔ Telegram bridge plugin.
 *
 * @author LazizbekDev
 * @version 5.0.0
 */
public class TelegramLogger extends JavaPlugin {

    public static final String PLUGIN_VERSION = "5.0.0";

    private ConfigManager configManager;
    private DataManager dataManager;
    private AdminManager adminManager;
    private TelegramAPI telegramAPI;
    private TelegramHandler telegramHandler;
    private AntiFloodManager antiFloodManager;

    private volatile boolean botActive = false;
    private volatile boolean pluginActive = false;

    // ─── Lifecycle ──────────────────────────────────────

    @Override
    public void onEnable() {
        try {
            // Initialize managers
            configManager = new ConfigManager(this);
            configManager.load();

            dataManager = new DataManager(getDataFolder(), getLogger());
            adminManager = new AdminManager(getDataFolder(), getLogger());

            antiFloodManager = new AntiFloodManager(
                    configManager.isAntiFloodEnabled(),
                    configManager.getAntiFloodMaxMessages(),
                    configManager.getAntiFloodWindowSeconds());

            telegramAPI = new TelegramAPI(this);
            telegramHandler = new TelegramHandler(this);

            // Register events and commands
            getServer().getPluginManager().registerEvents(new EventListener(this), this);
            CommandHandler cmdHandler = new CommandHandler(this);
            getCommand("telegramlogger").setExecutor(cmdHandler);
            getCommand("telegramlogger").setTabCompleter(cmdHandler);

            // Validate bot token and start polling
            if (configManager.isValid() && telegramAPI.checkToken(configManager.getBotToken())) {
                botActive = true;
                pluginActive = true;
                telegramHandler.startPolling();
                logAdmin("&a&l\u26A1 Plugin enabled with active bot connection!");

                // Send server start notification
                if (configManager.isEnableServerStartStop()) {
                    Map<String, String> ph = new LinkedHashMap<>();
                    ph.put("%version%", Bukkit.getVersion());
                    ph.put("%max%", String.valueOf(Bukkit.getMaxPlayers()));
                    String msg = MessageUtils.applyPlaceholders(configManager.getServerStartMessage(), ph);
                    telegramAPI.sendMessage(MessageUtils.stripColors(msg));
                    dataManager.incrementStat("server_start_count");
                }
            } else {
                botActive = false;
                pluginActive = true;
                logAdmin("&c&l\u26A0 Plugin enabled but bot is inactive! Check config.");
            }

        } catch (Exception e) {
            botActive = false;
            pluginActive = true;
            getLogger().severe("Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            // Send server stop notification
            if (botActive && configManager != null && configManager.isEnableServerStartStop()) {
                // Sync send since plugin is shutting down
                sendShutdownMessage();
            }

            Bukkit.getScheduler().cancelTasks(this);
            HandlerList.unregisterAll(this);

            if (dataManager != null) dataManager.save();
            if (adminManager != null) adminManager.save();

        } catch (Exception e) {
            getLogger().severe("Error during shutdown: " + e.getMessage());
        }
    }

    /**
     * Synchronous shutdown message to Telegram (can't use async during onDisable).
     */
    private void sendShutdownMessage() {
        try {
            String msg = MessageUtils.stripColors(configManager.getServerStopMessage());
            String urlString = "https://api.telegram.org/bot" + configManager.getBotToken() + "/sendMessage";
            StringBuilder params = new StringBuilder();
            params.append("chat_id=").append(java.net.URLEncoder.encode(configManager.getChatId(), "UTF-8"));
            params.append("&text=").append(java.net.URLEncoder.encode(msg, "UTF-8"));
            params.append("&parse_mode=HTML");
            if (configManager.isSendToThread()) {
                params.append("&message_thread_id=").append(
                        java.net.URLEncoder.encode(configManager.getThreadId(), "UTF-8"));
            }

            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(params.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception ignored) {}
    }

    // ─── Reload ─────────────────────────────────────────

    /**
     * Full plugin reload: cancel tasks, re-read config, reconnect bot.
     */
    public void performReload() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        dataManager.save();
        adminManager.save();

        configManager.reload();
        dataManager = new DataManager(getDataFolder(), getLogger());
        adminManager = new AdminManager(getDataFolder(), getLogger());

        antiFloodManager.updateSettings(
                configManager.isAntiFloodEnabled(),
                configManager.getAntiFloodMaxMessages(),
                configManager.getAntiFloodWindowSeconds());

        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        CommandHandler cmdHandler = new CommandHandler(this);
        getCommand("telegramlogger").setExecutor(cmdHandler);
        getCommand("telegramlogger").setTabCompleter(cmdHandler);

        if (configManager.isValid() && telegramAPI.checkToken(configManager.getBotToken())) {
            botActive = true;
            pluginActive = true;
            telegramHandler = new TelegramHandler(this);
            telegramHandler.startPolling();
            logAdmin("&a&l\u26A1 Plugin reloaded successfully!");
        } else {
            botActive = false;
            pluginActive = false;
            logAdmin("&c&l\u274C Invalid bot token after reload!");
        }
    }

    // ─── Accessors ──────────────────────────────────────

    public ConfigManager getConfigManager() { return configManager; }
    public DataManager getDataManager() { return dataManager; }
    public AdminManager getAdminManager() { return adminManager; }
    public TelegramAPI getTelegramAPI() { return telegramAPI; }
    public TelegramHandler getTelegramHandler() { return telegramHandler; }
    public AntiFloodManager getAntiFloodManager() { return antiFloodManager; }

    public boolean isBotActive() { return botActive; }
    public boolean isPluginActive() { return pluginActive; }
    public void setBotActive(boolean active) { this.botActive = active; }
    public void setPluginActive(boolean active) { this.pluginActive = active; }

    /**
     * Send a colored message to online admins and console.
     */
    public void logAdmin(String message) {
        String formatted = MessageUtils.colorize(configManager.getPluginPrefix() + message);
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("telegramlogger.admin"))
                .forEach(p -> p.sendMessage(formatted));
        getLogger().info(MessageUtils.stripColors(message));
    }
}
