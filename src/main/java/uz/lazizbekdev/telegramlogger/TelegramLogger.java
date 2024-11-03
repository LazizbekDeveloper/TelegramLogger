package uz.lazizbekdev.telegramlogger;

import com.google.gson.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TelegramLogger - Minecraft server event forwarder to Telegram
 * @author LazizbekDev
 * @version 1.0
 */
public class TelegramLogger extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
    private static final String PLUGIN_VERSION = "1.0";
    
    // Bot & Chat Settings
    private String botToken;
    private String chatId;
    private String threadId;
    private String pluginPrefix;
    private boolean sendToThread;
    private boolean sendTelegramMessagesToGame;
    private boolean isBotActive = false;
    private boolean isPluginActive = false;
    private boolean debugMode = false;
    private long lastUpdateId = 0;

    // Message Templates
    private String joinMessage;
    private String leaveMessage;
    private String chatMessage;
    private String advancementMessage; 
    private String deathMessage;
    private String worldSwitchMessage;
    private String filteredMessage;
    private String telegramGameMessage;
    private String errorNotAdmin;

    // Feature Toggles
    private boolean enableJoin;
    private boolean enableLeave;
    private boolean enableChat;
    private boolean enableAdvancement;
    private boolean enableDeath;
    private boolean enableWorldSwitch;
    private boolean enableChatFilter;
    private List<String> filteredWords;

    // Data Storage
    private JsonObject data;
    private JsonObject admins;
    private File dataFile;
    private File adminsFile;
    private File configFile;

    /**
     * Debug message utility
     */
    private void logDebug(String message, boolean force) {
        if (!debugMode && !force) return;
        
        if (!message.contains(pluginPrefix)) {
            message = pluginPrefix + " " + message;
        }
        
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        getServer().getConsoleSender().sendMessage("");
        getServer().getConsoleSender().sendMessage(message);
        getServer().getConsoleSender().sendMessage("");
    }

    /**
     * Color utility methods
     */
    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String stripColor(String message) {
        return ChatColor.stripColor(message);
    }


    /**
     * Plugin enable logic
     */
    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            loadConfig();
            loadDataFile();
            loadAdminsFile();
            
            getServer().getPluginManager().registerEvents(this, this);
            getCommand("telegramlogger").setExecutor(this);
            getCommand("telegramlogger").setTabCompleter(this);
            
            if (checkBotToken()) {
                startTelegramPolling();
                isBotActive = true;
                isPluginActive = true;
                broadcastPluginMessage("&a&l⚡ Plugin enabled with active bot connection!");
            } else {
                isBotActive = false;
                isPluginActive = true;
                broadcastPluginMessage("&c&l⚠ Plugin enabled but bot is inactive! Please check your bot token.");
            }
            
        } catch (Exception e) {
            isBotActive = false;
            isPluginActive = true;
            logDebug("&c&lError during plugin initialization: &e" + e.getMessage(), true);
            logDebug("&e&lPlugin will continue to work with limited functionality.", true);
            if (debugMode) {
                e.printStackTrace();
            }
            broadcastPluginMessage("&c&l❌ Error initializing plugin! Check console for details.");
        }
    }

    /**
     * Plugin disable logic
     */
    @Override
    public void onDisable() {
        try {
            // Cancel all tasks
            Bukkit.getScheduler().cancelTasks(this);
            
            // Unregister all listeners
            HandlerList.unregisterAll((JavaPlugin) this);
            
            // Send shutdown messages
            broadcastPluginMessage("&c&l⚡ Plugin is shutting down...");
            
            // Save all data
            saveDataFile();
            saveAdminsFile();
            
            // Final debug message
            logDebug("&c&l⚡ Plugin has been disabled!", true);
            
        } catch (Exception e) {
            logDebug("&c&l❌ Error during plugin shutdown: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Bot token validation
     */
    private boolean checkBotToken() {
        if (botToken == null || botToken.isEmpty() || botToken.equals("BOT_TOKEN")) {
            if (debugMode) {
                logDebug("&c&l⚠ Bot token is empty or default", false);
            }
            return false;
        }
    
        try {
            String urlString = "https://api.telegram.org/bot" + botToken + "/getMe";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
    
            int responseCode = conn.getResponseCode();
            boolean isValid = responseCode == 200;
            
            /**
            *if (debugMode) {
            *    logDebug("&a&lBot token check result: &e" + isValid + 
            *        " &7(Response code: &e" + responseCode + "&7)", false);
            *} 
            */
            
            return isValid;
        } catch (Exception e) {
            if (debugMode) {
                logDebug("&c&l❌ Failed to check bot token: &e" + e.getMessage(), false);
            }
            return false;
        }
    }

    /**
     * Gets current plugin status
     */
    private String getPluginStatus() {
        StringBuilder status = new StringBuilder();
        status.append("&6&l=== TelegramLogger Status ===\n\n");
        
        status.append("&7&lCore Status:\n");
        status.append("&e&l• Plugin: ").append(isPluginActive ? "&a&lActive ⚡" : "&c&lInactive ❌").append("\n");
        status.append("&e&l• Bot: ").append(isBotActive ? "&a&lConnected ⚡" : "&c&lDisconnected ❌").append("\n");
        status.append("&e&l• Debug Mode: ").append(debugMode ? "&a&lEnabled ⚡" : "&c&lDisabled ❌").append("\n");
        
        status.append("\n&7&lFeatures:\n");
        status.append("&e&l• Join Messages: ").append(getToggleStatus(enableJoin)).append("\n");
        status.append("&e&l• Leave Messages: ").append(getToggleStatus(enableLeave)).append("\n");
        status.append("&e&l• Chat Messages: ").append(getToggleStatus(enableChat)).append("\n");
        status.append("&e&l• Advancements: ").append(getToggleStatus(enableAdvancement)).append("\n");
        status.append("&e&l• Death Messages: ").append(getToggleStatus(enableDeath)).append("\n");
        status.append("&e&l• World Switch: ").append(getToggleStatus(enableWorldSwitch)).append("\n");
        status.append("&e&l• Chat Filter: ").append(getToggleStatus(enableChatFilter)).append("\n");
        
        status.append("\n&7&lServer Info:\n");
        status.append("&e&l• Version: &f").append(Bukkit.getVersion()).append("\n");
        status.append("&e&l• Players: &f")
              .append(Bukkit.getOnlinePlayers().size())
              .append("&7/&f")
              .append(Bukkit.getMaxPlayers());
        
        return status.toString();
    }
    
    /**
     * Gets feature toggle status with emoji
     */
    private String getToggleStatus(boolean enabled) {
        return enabled ? "&a&lEnabled ⚡" : "&c&lDisabled ❌";
    }

    /**
     * Time formatter utility
     */
    private String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + " seconds";
        }
        
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        if (minutes < 60) {
            return String.format("%d minutes, %d seconds", minutes, seconds);
        }
        
        long hours = minutes / 60;
        minutes = minutes % 60;
        
        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }
    
    /**
     * Configuration management
     */
    private void loadConfig() {
        try {
            reloadConfig();
            configFile = new File(getDataFolder(), "config.yml");
    
            if (!configFile.exists()) {
                broadcastPluginMessage("&e&l⚠ Config file not found, creating new one...");
                saveDefaultConfig();
            }
    
            // Faylni UTF-8 kodlash orqali yuklash
            FileConfiguration config;
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                config = YamlConfiguration.loadConfiguration(reader);
            }
    
            // Load all config values
            loadConfigValues(config);
    
            broadcastPluginMessage("&a&l⚡ Configuration loaded successfully!");
            if (debugMode) {
                logDebug("&a&l⚡ Config file loaded with following values:", false);
                logDebugConfigValues();
            }
    
        } catch (Exception e) {
            broadcastPluginMessage("&c&l❌ Error loading config: &e" + e.getMessage());
            logDebug("&c&lError loading config: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Create default config with all values
     */
    private void createConfig() {
        try {
            // Create config file if not exists
            saveDefaultConfig();
            File configFile = new File(getDataFolder(), "config.yml");
            
            if (!configFile.exists()) {
                broadcastPluginMessage("&e&l⚠ Creating new config file...");
                
                FileConfiguration config = getConfig();
                
                // Bot Settings
                config.set("bot_token", "BOT_TOKEN");
                config.set("chat_id", "CHAT_ID");
                config.set("thread_id", "THREAD_ID");
                config.set("send_to_thread", false);
                config.set("send_telegram_messages_to_game", true);
                config.set("debug_mode", false);

                // Message Prefix
                config.set("plugin_prefix", "&6&lTelegramLogger&7 ➜ &r&a");
                config.set("telegram_game_message", "&7[&9TG&7] &c%name% &8» &f%message%");

                // Join Messages
                config.set("enable_join", true);
                config.set("join_message", "<blockquote>ㅤㅤㅤㅤㅤ\n ➕ <b><u>%player%</u></b> joined the game! (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>");

                // Leave Messages
                config.set("enable_leave", true);
                config.set("leave_message", "<blockquote>ㅤㅤㅤㅤㅤ\n ➖ <b><u>%player%</u></b> left the game! (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>");

                // Chat Messages
                config.set("enable_chat", true);
                config.set("chat_message", "<b><u>%player%</u></b> <b>➥</b> %message%");

                // Advancement Messages
                config.set("enable_advancement", true);
                config.set("advancement_message", "<blockquote>ㅤㅤㅤㅤㅤ\n 🏆 <b><u>%player%</u></b> made the advancement <u>[%advancement%]</u> (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>");

                // Death Messages
                config.set("enable_death", true);
                config.set("death_message", "<blockquote>ㅤㅤㅤㅤㅤ\n 💀 <b><u>%player%</u></b> death: %death_message% (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>");

                // World Switch Messages
                config.set("enable_world_switch", true);
                config.set("world_switch_message", "<blockquote>ㅤㅤㅤㅤㅤ\n 🌍 <b><u>%player%</u></b> moved from <u>%from_world%</u> to <u>%to_world%</u> (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>");

                // Chat Filter
                config.set("enable_chat_filter", true);
                List<String> defaultFilteredWords = new ArrayList<>();
                defaultFilteredWords.add("badword1");
                defaultFilteredWords.add("badword2");
                config.set("filtered_words", defaultFilteredWords);
                config.set("filtered_message", "<blockquote>ㅤㅤㅤㅤㅤ\n 🚫 <b><u>%player%</u></b> used a filtered word. (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>");

                // Error Messages
                config.set("error_not_admin", "<blockquote>ㅤㅤㅤㅤㅤ\n ❌ You are not registered as an admin!\nㅤㅤㅤㅤ</blockquote>");

                // Plugin Version
                config.set("version", PLUGIN_VERSION);

                // Save config
                config.save(configFile);
                broadcastPluginMessage("&a&l⚡ New config file created successfully!");
                
                if (debugMode) {
                    logDebug("&a&l⚡ Created new config file with default values", false);
                }
            }
        } catch (Exception e) {
            broadcastPluginMessage("&c&l❌ Error creating config file: &e" + e.getMessage());
            logDebug("&c&l❌ Failed to create config: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save default config from resources
     */
    @Override
    public void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            if (getDataFolder().mkdir()) {
                if (debugMode) {
                    logDebug("&a&l⚡ Plugin directory created", false);
                }
            } else {
                logDebug("&c&l❌ Failed to create plugin directory", true);
                return;
            }
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
            if (debugMode) {
                logDebug("&a&l⚡ Saved default config.yml", false);
            }
        }
    }

    /**
     * Gets config file
     */
    public FileConfiguration getConfiguration() {
        if (configFile == null) {
            reloadConfig();
        }
        return getConfig();
    }

    /**
     * Reloads config from file
     */
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (configFile == null) {
            configFile = new File(getDataFolder(), "config.yml");
        }
        
        if (!configFile.exists()) {
            createConfig();
        }
    }
    
    /**
     * Load configuration values
     */
    private void loadConfigValues(FileConfiguration config) {
        // Bot settings
        botToken = config.getString("bot_token", "");
        chatId = config.getString("chat_id", "");
        threadId = config.getString("thread_id", "");
        sendToThread = config.getBoolean("send_to_thread", false);
        sendTelegramMessagesToGame = config.getBoolean("send_telegram_messages_to_game", true);
        debugMode = config.getBoolean("debug_mode", false);
        
        // Message templates
        pluginPrefix = colorize(config.getString("plugin_prefix", "&6&lTelegramLogger&7 ➜ &r&a"));
        joinMessage = config.getString("join_message", "<b>%player% joined the game!</b>");
        leaveMessage = config.getString("leave_message", "<b>%player% left the game!</b>");
        chatMessage = config.getString("chat_message", "<b>%player%:</b> %message%");
        advancementMessage = config.getString("advancement_message", 
            "<b>%player%</b> has made the advancement <b>[%advancement%]</b>");
        deathMessage = config.getString("death_message", "<b>%player%</b> %death_message%");
        worldSwitchMessage = config.getString("world_switch_message", 
            "<b>%player%</b> moved from %from_world% to %to_world%");
        filteredMessage = config.getString("filtered_message", "<b>%player%</b> used a filtered word");
        telegramGameMessage = config.getString("telegram_game_message", 
            "&7[&9TG&7] &c%name% &8» &f%message%");
        errorNotAdmin = config.getString("error_not_admin", 
            "<blockquote>ㅤㅤㅤㅤㅤ\n ❌ You are not registered as an admin!\nㅤㅤㅤㅤ</blockquote>");
        
        // Feature toggles
        enableJoin = config.getBoolean("enable_join", true);
        enableLeave = config.getBoolean("enable_leave", true);
        enableChat = config.getBoolean("enable_chat", true);
        enableAdvancement = config.getBoolean("enable_advancement", false);
        enableDeath = config.getBoolean("enable_death", true);
        enableWorldSwitch = config.getBoolean("enable_world_switch", true);
        enableChatFilter = config.getBoolean("enable_chat_filter", true);
        
        // Filter words
        filteredWords = config.getStringList("filtered_words").stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    }

    /**
     * Log config values in debug mode
     */
    private void logDebugConfigValues() {
        logDebug("&7&lBot Settings:", false);
        logDebug("&e&l• Bot Token: &f" + (botToken.isEmpty() ? "Not Set" : "Set"), false);
        logDebug("&e&l• Chat ID: &f" + (chatId.isEmpty() ? "Not Set" : chatId), false);
        logDebug("&e&l• Thread ID: &f" + (threadId.isEmpty() ? "Not Set" : threadId), false);
        logDebug("&e&l• Send To Thread: &f" + sendToThread, false);
        logDebug("&e&l• Telegram Messages to Game: &f" + sendTelegramMessagesToGame, false);
        logDebug("&e&l• Debug Mode: &f" + debugMode, false);

        logDebug("\n&7&lFeature Toggles:", false);
        logDebug("&e&l• Join Messages: &f" + enableJoin, false);
        logDebug("&e&l• Leave Messages: &f" + enableLeave, false);
        logDebug("&e&l• Chat Messages: &f" + enableChat, false);
        logDebug("&e&l• Advancements: &f" + enableAdvancement, false);
        logDebug("&e&l• Death Messages: &f" + enableDeath, false);
        logDebug("&e&l• World Switch: &f" + enableWorldSwitch, false);
        logDebug("&e&l• Chat Filter: &f" + enableChatFilter, false);

        if (enableChatFilter && !filteredWords.isEmpty()) {
            logDebug("\n&7&lFiltered Words:", false);
            logDebug("&f" + String.join(", ", filteredWords), false);
        }
    }

    /**
     * Broadcast message to all players and console
     */
    private void broadcastPluginMessage(String message) {
        String formattedMessage = colorize(pluginPrefix + message);
        
        // Send to all players
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(formattedMessage));
        
        // Log to console
        logDebug(message, true);
        
        // Send to Telegram if needed
        if (message.startsWith("&c") || message.contains("error") || message.contains("failed")) {
            sendToTelegram("⚠️ " + stripColor(message));
        } else {
            sendToTelegram(stripColor(message));
        }
    }

    /**
     * Broadcast message to admins only
     */
    private void broadcastAdminMessage(String message) {
        String formattedMessage = colorize(pluginPrefix + "&c&lADMIN &7» &f" + message);
        
        // Send to admins only
        Bukkit.getOnlinePlayers().stream()
            .filter(player -> player.hasPermission("telegramlogger.admin"))
            .forEach(player -> player.sendMessage(formattedMessage));
        
        // Log to console
        logDebug("&c&lADMIN &7» &f" + message, true);
    }

    /**
     * Broadcast to specific player
     */
    private void broadcastToPlayer(Player player, String message) {
        player.sendMessage(colorize(pluginPrefix + message));
        
        if (debugMode) {
            logDebug("&7Message sent to &f" + player.getName() + "&7: &f" + message, false);
        }
    }

    /**
     * Send detailed stats
     */
    private void sendDetailedStats(CommandSender sender) {
        StringBuilder stats = new StringBuilder();
        stats.append("\n&6&l=== TelegramLogger Detailed Statistics ===\n");
        
        int total = getStat("total_messages");
        if (total > 0) {
            stats.append("\n&e&lTotal Messages: &f").append(total).append("\n");
            
            Map<String, String> statMap = new LinkedHashMap<>();
            statMap.put("join_messages", "&a&lJoin Messages");
            statMap.put("leave_messages", "&c&lLeave Messages");
            statMap.put("chat_messages", "&f&lChat Messages");
            statMap.put("advancement_messages", "&d&lAdvancement Messages");
            statMap.put("death_messages", "&4&lDeath Messages");
            statMap.put("world_switch_messages", "&b&lWorld Switch Messages");
            statMap.put("filtered_messages", "&6&lFiltered Messages");

            for (Map.Entry<String, String> entry : statMap.entrySet()) {
                int value = getStat(entry.getKey());
                double percentage = (value * 100.0) / total;
                String bar = generateProgressBar(percentage);
                
                stats.append("\n").append(entry.getValue())
                     .append("&7: &f").append(value)
                     .append(" &7(&e").append(String.format("%.1f%%&7)", percentage))
                     .append("\n&7").append(bar);
            }
        } else {
            stats.append("\n&e&lNo messages recorded yet\n");
        }

        // Send stats
        Arrays.stream(stats.toString().split("\n"))
            .forEach(line -> sender.sendMessage(colorize(line.isEmpty() ? line : pluginPrefix + line)));

        // Send to Telegram if from console
        if (!(sender instanceof Player)) {
            sendToTelegram(stripColor(stats.toString()));
        }
    }

    /**
     * Get online players list (limited to 10 players)
     */
    private String getOnlinePlayersList() {
        Collection<? extends Player> allPlayers = Bukkit.getOnlinePlayers();
        
        if (allPlayers.isEmpty()) {
            return "👥 No players online";
        }
        
        StringBuilder list = new StringBuilder();
        list.append("👥 <b>Online Players</b> (")
            .append(allPlayers.size())
            .append("/")
            .append(Bukkit.getMaxPlayers())
            .append(")\n\n");

        // Get first 10 players
        List<Player> limitedPlayers = allPlayers.stream()
            .limit(10)
            .collect(Collectors.toList());

        // Add each player
        for (Player player : limitedPlayers) {
            list.append("• ");
            
            // Add admin crown for admins
            if (player.hasPermission("telegramlogger.admin")) {
                list.append("👑 ");
            }
            
            // Add player name
            list.append("<b>").append(player.getName()).append("</b>");
            
            // Add display name if different from player name
            if (!player.getDisplayName().equals(player.getName())) {
                list.append(" (").append(player.getDisplayName()).append(")");
            }
            
            list.append("\n");
        }

        // Add message if there are more players
        if (allPlayers.size() > 10) {
            int remaining = allPlayers.size() - 10;
            list.append("\n<i>...and ").append(remaining)
                .append(" more player").append(remaining == 1 ? "" : "s")
                .append("</i>");
        }

        return list.toString();
    }

    /**
     * Handle stats command
     */
    private void handleStatsCommand(CommandSender sender) {
        sendDetailedStats(sender);
    }

    /**
     * Generate progress bar for stats
     * @param percentage Value percentage (0-100)
     * @return Formatted progress bar string
     */
    private String generateProgressBar(double percentage) {
        int barLength = 20;
        int filledLength = (int) Math.round(barLength * percentage / 100);
        
        StringBuilder bar = new StringBuilder();
        bar.append("&8[");
        
        // Filled part
        bar.append("&a");
        for (int i = 0; i < filledLength; i++) {
            bar.append("■");
        }
        
        // Empty part
        bar.append("&7");
        for (int i = filledLength; i < barLength; i++) {
            bar.append("■");
        }
        
        bar.append("&8]");
        return bar.toString();
    }

    /**
     * Save config with current values
     */
    private void savePluginConfig() {
        try {
            FileConfiguration config = getConfig();
            
            // Save current values
            config.set("bot_token", botToken);
            config.set("chat_id", chatId);
            config.set("thread_id", threadId);
            config.set("send_to_thread", sendToThread);
            config.set("send_telegram_messages_to_game", sendTelegramMessagesToGame);
            config.set("debug_mode", debugMode);
            
            config.set("plugin_prefix", pluginPrefix);
            config.set("telegram_game_message", telegramGameMessage);
            config.set("error_not_admin", errorNotAdmin);
            
            config.set("enable_join", enableJoin);
            config.set("join_message", joinMessage);
            config.set("enable_leave", enableLeave);
            config.set("leave_message", leaveMessage);
            config.set("enable_chat", enableChat);
            config.set("chat_message", chatMessage);
            config.set("enable_advancement", enableAdvancement);
            config.set("advancement_message", advancementMessage);
            config.set("enable_death", enableDeath);
            config.set("death_message", deathMessage);
            config.set("enable_world_switch", enableWorldSwitch);
            config.set("world_switch_message", worldSwitchMessage);
            config.set("enable_chat_filter", enableChatFilter);
            config.set("filtered_words", filteredWords);
            config.set("filtered_message", filteredMessage);
            
            // Save config
            config.save(configFile);
            
            broadcastPluginMessage("&a&l⚡ Configuration saved successfully!");
            
            if (debugMode) {
                logDebug("&a&l⚡ Configuration saved with current values", false);
            }
            
        } catch (Exception e) {
            broadcastPluginMessage("&c&l❌ Error saving config: &e" + e.getMessage());
            logDebug("&c&lError saving config: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if config is valid
     */
    private boolean isConfigValid() {
        if (botToken == null || botToken.isEmpty() || botToken.equals("BOT_TOKEN")) {
            broadcastPluginMessage("&c&l❌ Bot token is not configured!");
            return false;
        }
        
        if (chatId == null || chatId.isEmpty() || chatId.equals("CHAT_ID")) {
            broadcastPluginMessage("&c&l❌ Chat ID is not configured!");
            return false;
        }
        
        if (sendToThread && (threadId == null || threadId.isEmpty() || threadId.equals("THREAD_ID"))) {
            broadcastPluginMessage("&c&l❌ Thread ID is required when send_to_thread is enabled!");
            return false;
        }
        
        return true;
    }
    
    /**
     * Data file management
     */
    private void loadDataFile() {
        dataFile = new File(getDataFolder(), "data.json");
        if (!dataFile.exists()) {
            broadcastPluginMessage("&e&l⚠ Data file not found, creating new one...");
            data = new JsonObject();
            initializeDefaultData();
            saveDataFile();
            broadcastPluginMessage("&a&l⚡ New data file created successfully!");
        } else {
            try (FileReader reader = new FileReader(dataFile)) {
                data = JsonParser.parseReader(reader).getAsJsonObject();
                validateDataFile();
                broadcastPluginMessage("&a&l⚡ Data file loaded successfully!");
                
                if (debugMode) {
                    logDebugDataValues();
                }
            } catch (Exception e) {
                broadcastPluginMessage("&c&l❌ Error loading data file: &e" + e.getMessage());
                logDebug("&c&lFailed to load data file: &e" + e.getMessage(), true);
                data = new JsonObject();
                initializeDefaultData();
                saveDataFile();
            }
        }
    }
    
    /**
     * Initialize default statistics
     */
    private void initializeDefaultData() {
        data.addProperty("total_messages", 0);
        data.addProperty("join_messages", 0);
        data.addProperty("leave_messages", 0);
        data.addProperty("chat_messages", 0);
        data.addProperty("advancement_messages", 0);
        data.addProperty("death_messages", 0);
        data.addProperty("world_switch_messages", 0);
        data.addProperty("filtered_messages", 0);
        
        if (debugMode) {
            logDebug("&e&l⚠ Initialized default statistics data", false);
        }
    }
    
    /**
     * Validate data file structure
     */
    private void validateDataFile() {
        boolean needsSave = false;
        String[] requiredStats = {
            "total_messages", "join_messages", "leave_messages",
            "chat_messages", "advancement_messages", "death_messages",
            "world_switch_messages", "filtered_messages"
        };
    
        for (String stat : requiredStats) {
            if (!data.has(stat)) {
                data.addProperty(stat, 0);
                needsSave = true;
                if (debugMode) {
                    logDebug("&e&l⚠ Added missing stat: &f" + stat, false);
                }
            }
        }
    
        if (needsSave) {
            saveDataFile();
            broadcastPluginMessage("&e&l⚠ Data file structure updated!");
        }
    }
    
    /**
     * Save data file
     */
    private void saveDataFile() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
            
            if (debugMode) {
                logDebug("&a&l⚡ Data file saved successfully", false);
            }
        } catch (IOException e) {
            broadcastPluginMessage("&c&l❌ Failed to save data file: &e" + e.getMessage());
            logDebug("&c&lFailed to save data file: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Log current statistics in debug mode 
     */
    private void logDebugDataValues() {
        logDebug("\n&7&lCurrent Statistics:", false);
        logDebug("&e&l• Total Messages: &f" + getStat("total_messages"), false);
        logDebug("&e&l• Join Messages: &f" + getStat("join_messages"), false);
        logDebug("&e&l• Leave Messages: &f" + getStat("leave_messages"), false);
        logDebug("&e&l• Chat Messages: &f" + getStat("chat_messages"), false);
        logDebug("&e&l• Advancement Messages: &f" + getStat("advancement_messages"), false);
        logDebug("&e&l• Death Messages: &f" + getStat("death_messages"), false);
        logDebug("&e&l• World Switch Messages: &f" + getStat("world_switch_messages"), false);
        logDebug("&e&l• Filtered Messages: &f" + getStat("filtered_messages"), false);
    }

    /**
     * Admins file management
     */
    private void loadAdminsFile() {
        adminsFile = new File(getDataFolder(), "admins.json");
        if (!adminsFile.exists()) {
            broadcastPluginMessage("&e&l⚠ Admins file not found, creating new one...");
            admins = new JsonObject();
            saveAdminsFile();
            broadcastPluginMessage("&a&l⚡ New admins file created successfully!");
        } else {
            try (FileReader reader = new FileReader(adminsFile)) {
                admins = JsonParser.parseReader(reader).getAsJsonObject();
                validateAdminsFile();
                broadcastPluginMessage("&a&l⚡ Admins file loaded successfully!");
                
                if (debugMode) {
                    logDebugAdminsValues();
                }
            } catch (Exception e) {
                broadcastPluginMessage("&c&l❌ Error loading admins file: &e" + e.getMessage());
                logDebug("&c&lFailed to load admins file: &e" + e.getMessage(), true);
                admins = new JsonObject();
                saveAdminsFile();
            }
        }
    }
    
    /**
     * Validate admins file structure
     */
    private void validateAdminsFile() {
        boolean needsSave = false;
        for (Map.Entry<String, JsonElement> entry : admins.entrySet()) {
            JsonObject adminObj = entry.getValue().getAsJsonObject();
            if (!adminObj.has("name")) {
                adminObj.addProperty("name", "Unknown Admin");
                needsSave = true;
                if (debugMode) {
                    logDebug("&e&l⚠ Fixed missing name for admin ID: &f" + entry.getKey(), false);
                }
            }
            if (!adminObj.has("addedDate")) {
                adminObj.addProperty("addedDate", new Date().toString());
                needsSave = true;
                if (debugMode) {
                    logDebug("&e&l⚠ Added missing date for admin ID: &f" + entry.getKey(), false);
                }
            }
        }
        
        if (needsSave) {
            saveAdminsFile();
            broadcastPluginMessage("&e&l⚠ Admins file structure updated!");
        }
    }
    
    /**
     * Save admins file
     */
    private void saveAdminsFile() {
        try (FileWriter writer = new FileWriter(adminsFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(admins, writer);
            
            if (debugMode) {
                logDebug("&a&l⚡ Admins file saved successfully", false);
            }
        } catch (IOException e) {
            broadcastPluginMessage("&c&l❌ Failed to save admins file: &e" + e.getMessage());
            logDebug("&c&lFailed to save admins file: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Log current admins in debug mode
     */
    private void logDebugAdminsValues() {
        logDebug("\n&7&lRegistered Admins:", false);
        if (admins.size() == 0) {
            logDebug("&e&l• No admins registered", false);
            return;
        }
        
        for (Map.Entry<String, JsonElement> entry : admins.entrySet()) {
            JsonObject adminObj = entry.getValue().getAsJsonObject();
            logDebug("&e&l• &f" + adminObj.get("name").getAsString() + 
                    " &7(ID: &f" + entry.getKey() + "&7)" +
                    " &7[Added: &f" + adminObj.get("addedDate").getAsString() + "&7]", false);
        }
    }

    /**
     * Stats management utilities
     */
    private void incrementStat(String statName) {
        data.addProperty(statName, data.get(statName).getAsInt() + 1);
        data.addProperty("total_messages", data.get("total_messages").getAsInt() + 1);
        saveDataFile();
        
        if (debugMode) {
            logDebug("&a&l⚡ Incremented stat &f" + statName + " &7(New value: &f" + 
                    data.get(statName).getAsInt() + "&7)", false);
        }
    }
    
    private int getStat(String statName) {
        if (!data.has(statName)) {
            if (debugMode) {
                logDebug("&c&l❌ Attempted to get non-existent stat: &f" + statName, false);
            }
            return 0;
        }
        return data.get(statName).getAsInt();
    }
    
    /**
     * Telegram bot core functionality
     */
    private void startTelegramPolling() {
        broadcastPluginMessage("&a&l⚡ Starting Telegram bot polling...");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                pollTelegramUpdates();
            }
        }.runTaskTimerAsynchronously(this, 20L, 20L); // Run every second
        
        if (debugMode) {
            logDebug("&a&l⚡ Telegram polling started with interval: &f1 second", false);
        }
    }
    
    /**
     * Poll for Telegram updates
     */
    private void pollTelegramUpdates() {
        if (!sendTelegramMessagesToGame || !isPluginActive || !isBotActive) return;
    
        try {
            String urlString = String.format("https://api.telegram.org/bot%s/getUpdates?offset=%d&timeout=30", 
                botToken, lastUpdateId + 1);
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(35000);
            conn.setReadTimeout(35000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                // if (debugMode) {
                    // logDebug("&c&l❌ Error polling Telegram updates. Response code: &e" + responseCode, false);
                // }
                return;
            }
    
            String response = readResponse(conn);
            JsonObject updates = JsonParser.parseString(response).getAsJsonObject();
            
            if (updates.get("ok").getAsBoolean() && updates.has("result")) {
                processUpdates(updates.get("result").getAsJsonArray());
            }
            
        } catch (Exception e) {
            if (debugMode) {
                logDebug("&c&l❌ Error polling Telegram updates: &e" + e.getMessage(), false);
            }
        }
    }
    
    /**
     * Read HTTP response
     */
    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * Process error response
     */
    private String getErrorResponse(HttpURLConnection conn) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        } catch (IOException e) {
            return "Could not read error response: " + e.getMessage();
        }
    }
    
    /**
     * Process Telegram updates
     */
    private void processUpdates(JsonArray updates) {
        for (JsonElement updateElem : updates) {
            try {
                JsonObject update = updateElem.getAsJsonObject();
                lastUpdateId = update.get("update_id").getAsLong();
                
                if (update.has("message")) {
                    JsonObject message = update.get("message").getAsJsonObject();
                    processMessage(message);
                }
                
                if (debugMode) {
                    logDebug("&a&l⚡ Processed update ID: &f" + lastUpdateId, false);
                }
                
            } catch (Exception e) {
                logDebug("&c&l❌ Error processing update: &e" + e.getMessage(), false);
                if (debugMode) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Check chat member status
     */
    private String getChatMemberStatus(long userId) {
        try {
            String urlString = String.format("https://api.telegram.org/bot%s/getChatMember?chat_id=%s&user_id=%d",
                botToken, chatId, userId);
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            String response = readResponse(conn);
            JsonObject result = JsonParser.parseString(response).getAsJsonObject();
            
            if (result.get("ok").getAsBoolean() && result.has("result")) {
                String status = result.get("result").getAsJsonObject().get("status").getAsString();
                
                if (debugMode) {
                    logDebug("&a&l⚡ Chat member status for &f" + userId + "&a: &f" + status, false);
                }
                
                return status;
            }
        } catch (Exception e) {
            if (debugMode) {
                logDebug("&c&l❌ Error checking admin status: &e" + e.getMessage(), false);
            }
        }
        return "";
    }
    
    /**
     * Send message to Telegram
     */
    private void sendToTelegram(String message) {
        if (!isBotActive || !isPluginActive) return;
    
        String cleanMessage = ChatColor.stripColor(message);
        
        // Then strip any remaining & codes
        String cleanMessage2 = cleanMessage.replaceAll("&[0-9a-fk-orA-FK-OR]", "");
        // Also strip § codes
        String cleanMessage3 = cleanMessage2.replaceAll("§[0-9a-fk-orA-FK-OR]", "");
        
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
                String encodedMessage = URLEncoder.encode(cleanMessage3, StandardCharsets.UTF_8.toString());
                String params = String.format("chat_id=%s&text=%s&parse_mode=HTML&disable_web_page_preview=true%s",
                    URLEncoder.encode(chatId, "UTF-8"),
                    encodedMessage,
                    sendToThread ? "&message_thread_id=" + URLEncoder.encode(threadId, "UTF-8") : "");
    
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
    
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = params.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    String errorResponse = getErrorResponse(conn);
                    
                    if (debugMode) {
                        broadcastPluginMessage("&c&l❌ Failed to send message to Telegram!");
                        logDebug("&c&lResponse code: &e" + responseCode + 
                            "&c, Error: &e" + errorResponse, false);
                    }
                } else if (debugMode) {
                    logDebug("&a&l⚡ Message sent to Telegram successfully", false);
                }
    
            } catch (Exception e) {
                
                if (debugMode) {
                    e.printStackTrace();
                    broadcastPluginMessage("&c&l❌ Error sending message to Telegram: &e" + e.getMessage());
                }
            }
        });
    }

    /**
     * Broadcast message from Telegram to Minecraft
     */
    private void broadcastToMinecraft(String adminName, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String formattedMessage = colorize(telegramGameMessage
                    .replace("%name%", adminName)
                    .replace("%message%", message));
                
                // Broadcast to all players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(formattedMessage);
                }

                // Log to console
                logDebug(stripColor(formattedMessage), true);
                
                // Send confirmation back to Telegram
                sendToTelegram(formattedMessage);
                
                if (debugMode) {
                    logDebug("&a&l⚡ Telegram message broadcast from: &f" + adminName, false);
                }
            }
        }.runTask(this);
    }

    /**
     * Check if Telegram bot is functioning
     */
    private boolean isBotFunctional() {
        if (!isBotActive || !isPluginActive) {
            if (debugMode) {
                logDebug("&c&l❌ Bot status check failed - Bot: " + 
                    (isBotActive ? "&a&lActive" : "&c&lInactive") + 
                    " &7| Plugin: " + (isPluginActive ? "&a&lActive" : "&c&lInactive"), false);
            }
            return false;
        }
        return true;
    }
    
    /**
     * Process incoming Telegram messages
     */
    private void processMessage(JsonObject message) {
        try {
            // Basic message validation
            if (!isValidMessage(message)) {
                if (debugMode) {
                    logDebug("&e&l⚠ Skipped invalid message format", false);
                }
                return;
            }
            
            // Get basic message info
            String chatIdFromMsg = message.get("chat").getAsJsonObject().get("id").getAsString();
            if (!chatIdFromMsg.equals(chatId)) {
                if (debugMode) {
                    logDebug("&e&l⚠ Message from unknown chat: &f" + chatIdFromMsg, false);
                }
                return;
            }
    
            // Thread check if enabled
            if (!isValidThread(message)) {
                if (debugMode) {
                    logDebug("&e&l⚠ Message from wrong thread or missing thread ID", false);
                }
                return;
            }
    
            // Get sender info
            long userId = message.get("from").getAsJsonObject().get("id").getAsLong();
            String messageText = message.get("text").getAsString();
            String userIdStr = String.valueOf(userId);
            
            // Process command if message starts with /
            if (messageText.startsWith("/")) {
                processTelegramCommand(userIdStr, messageText);
                return;
            }
            
            // Validate sender permissions
            String status = getChatMemberStatus(userId);
            boolean isGroupAdmin = status.equals("creator") || status.equals("administrator");
            boolean isRegisteredAdmin = isAdminRegistered(userIdStr);
    
            if (isGroupAdmin && isRegisteredAdmin) {
                handleAdminMessage(userIdStr, messageText);
            } else {
                handleUnauthorizedMessage(userIdStr, status);
            }
            
        } catch (Exception e) {
            logDebug("&c&l❌ Error processing message: &e" + e.getMessage(), false);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Validate basic message structure
     */
    private boolean isValidMessage(JsonObject message) {
        if (!message.has("text") || !message.has("from")) {
            return false;
        }
        
        try {
            message.get("chat").getAsJsonObject().get("id").getAsString();
            message.get("from").getAsJsonObject().get("id").getAsLong();
            message.get("text").getAsString();
            return true;
        } catch (Exception e) {
            if (debugMode) {
                logDebug("&c&l❌ Message validation failed: &e" + e.getMessage(), false);
            }
            return false;
        }
    }

    /**
     * Validate thread settings if enabled
     */
    private boolean isValidThread(JsonObject message) {
        if (!sendToThread) return true;
        
        if (!message.has("message_thread_id")) {
            return false;
        }
        
        try {
            return message.get("message_thread_id").getAsString().equals(threadId);
        } catch (Exception e) {
            if (debugMode) {
                logDebug("&c&l❌ Thread validation failed: &e" + e.getMessage(), false);
            }
            return false;
        }
    }

    /**
     * Process Telegram commands
     */
    private void processTelegramCommand(String userId, String messageText) {
        if (!isAdminRegistered(userId)) {
            sendToTelegram(errorNotAdmin);
            return;
        }

        String adminName = getAdminName(userId);
        messageText = messageText.toLowerCase();

        if (debugMode) {
            logDebug("&a&l⚡ Processing command from &f" + adminName + "&a: &f" + messageText, false);
        }

        // Match command start
        if (messageText.startsWith("/status")) {
            sendToTelegram(colorize(getPluginStatus()));
        }
        
        else if (messageText.startsWith("/stats")) {
            sendDetailedStats(Bukkit.getConsoleSender());
        }
        
        else if (messageText.startsWith("/players")) {
            sendToTelegram(colorize(getOnlinePlayersList()));
        }

        else if (messageText.startsWith("/reload")) {
            handleReloadFromTelegram(adminName);
        }
        
        else if (messageText.startsWith("/stop")) {
            handleStopFromTelegram(adminName);
        }
        
        else if (messageText.startsWith("/start")) {
            handleStartFromTelegram(adminName);
        }
        
        else if (messageText.startsWith("/debug")) {
            handleDebugFromTelegram(adminName);
        }
        
        else if (messageText.startsWith("/help")) {
            sendTelegramHelp();
        }
        
        else if (messageText.startsWith("/")) {
            sendToTelegram("❌ Unknown command. Use /help for available commands.");
        }
    }

    /**
     * Send Telegram help message
     */
    private void sendTelegramHelp() {
        StringBuilder help = new StringBuilder();
        help.append("📚 <b>Available Commands:</b>\n\n");
        help.append("🔹 /status - Show plugin status\n");
        help.append("📊 /stats - Show message statistics\n");
        help.append("👥 /players - Show online players\n");
        help.append("🔄 /reload - Reload the plugin\n");
        help.append("▶️ /start - Start message forwarding\n");
        help.append("⏹️ /stop - Stop message forwarding\n");
        help.append("🐞 /debug - Toggle debug mode\n");
        help.append("❓ /help - Show this help message\n\n");
        help.append("✨ You can also send normal messages that will be broadcast to the game.");

        sendToTelegram(help.toString());
    }

    /**
     * Handle reload command from Telegram
     */
    private void handleReloadFromTelegram(String adminName) {
        broadcastPluginMessage("&e&l⚡ Plugin reload initiated by Telegram admin &f" + adminName);
        
        try {
            // Same reload logic as command handler
            Bukkit.getScheduler().cancelTasks(this);
            HandlerList.unregisterAll((JavaPlugin) this);
            saveDataFile();
            saveAdminsFile();
            
            reloadConfig();
            loadConfig();
            loadDataFile();
            loadAdminsFile();
            
            if (checkBotToken()) {
                getServer().getPluginManager().registerEvents(this, this);
                startTelegramPolling();
                isBotActive = true;
                isPluginActive = true;
                
                broadcastPluginMessage("&a&l⚡ Plugin reloaded successfully by Telegram admin &f" + adminName);
                sendToTelegram("✅ Plugin reloaded successfully!");
            } else {
                isPluginActive = false;
                isBotActive = false;
                broadcastPluginMessage("&c&l❌ Bot token is invalid! Plugin won't send messages until fixed.");
                sendToTelegram("❌ Invalid bot token after reload!");
            }
        } catch (Exception e) {
            broadcastPluginMessage("&c&l❌ Error during reload: &e" + e.getMessage());
            sendToTelegram("❌ Error during reload: " + e.getMessage());
        }
    }

    /**
     * Handle stop command from Telegram
     */
    private void handleStopFromTelegram(String adminName) {
        if (isPluginActive) {
            isPluginActive = false;
            broadcastPluginMessage("&c&l⏹️ Messaging stopped by Telegram admin &f" + adminName);
            sendToTelegram("✅ Messaging has been stopped!");
        } else {
            sendToTelegram("❌ Messaging is already inactive!");
        }
    }

    /**
     * Handle start command from Telegram
     */
    private void handleStartFromTelegram(String adminName) {
        if (!isPluginActive) {
            isPluginActive = true;
            broadcastPluginMessage("&a&l▶️ Messaging started by Telegram admin &f" + adminName);
            sendToTelegram("✅ Messaging has been started!");
        } else {
            sendToTelegram("❌ Messaging is already active!");
        }
    }

    /**
     * Handle debug command from Telegram
     */
    private void handleDebugFromTelegram(String adminName) {
        debugMode = !debugMode;
        String status = debugMode ? "enabled" : "disabled";
        broadcastPluginMessage("&e&l🐞 Debug mode " + status + " by Telegram admin &f" + adminName);
        sendToTelegram("✅ Debug mode has been " + status + "!");
        
        if (debugMode) {
            logDebug("&a&l⚡ Debug mode enabled by Telegram admin &f" + adminName, true);
            sendToTelegram(getDebugStatus());
        }
    }

    /**
     * Get debug status information
     */
    private String getDebugStatus() {
        StringBuilder status = new StringBuilder();
        status.append("🐞 <b>Debug Information:</b>\n\n");
        status.append("• Plugin Active: ").append(isPluginActive ? "✅" : "❌").append("\n");
        status.append("• Bot Active: ").append(isBotActive ? "✅" : "❌").append("\n");
        status.append("• Messages Today: ").append(getStat("total_messages")).append("\n");
        status.append("• Online Players: ").append(Bukkit.getOnlinePlayers().size())
              .append("/").append(Bukkit.getMaxPlayers()).append("\n");
        status.append("• Server Version: ").append(Bukkit.getVersion());
        return status.toString();
    }

    /**
     * Handle authorized admin message
     */
    private void handleAdminMessage(String userId, String messageText) {
        String adminName = getAdminName(userId);
        if (adminName == null || adminName.isEmpty()) {
            adminName = "Unknown Admin";
        }

        // Log admin message
        if (debugMode) {
            logDebug("&a&l⚡ Admin message from &f" + adminName + "&a: &f" + messageText, false);
        }

        // Check message content
        if (messageText.length() > 256) {
            sendToTelegram("❌ Message too long! Maximum length is 256 characters.");
            return;
        }

        if (containsFilteredWord(messageText.toLowerCase())) {
            sendToTelegram("❌ Message contains filtered words!");
            logDebug("&c&l⚠ Filtered message blocked from admin &f" + adminName, true);
            return;
        }

        // Broadcast message
        broadcastToMinecraft(adminName, messageText);

        // Update stats
        incrementStat("chat_messages");
    }

    /**
     * Handle unauthorized message attempt
     */
    private void handleUnauthorizedMessage(String userId, String status) {
        sendToTelegram(errorNotAdmin);
        
        if (debugMode) {
            logDebug("&c&l⚠ Unauthorized message attempt:", false);
            logDebug("&c&l• User ID: &f" + userId, false);
            logDebug("&c&l• Status: &f" + status, false);
            logDebug("&c&l• Is Group Admin: &f" + (status.equals("creator") || status.equals("administrator")), false);
            logDebug("&c&l• Is Registered: &f" + isAdminRegistered(userId), false);
        }
    }

    /**
     * Format Telegram error message
     */
    private String formatTelegramError(String error) {
        return "❌ <b>Error:</b> " + error;
    }

    /**
     * Format Telegram success message
     */
    private String formatTelegramSuccess(String message) {
        return "✅ " + message;
    }
    
    /**
     * Player Join Event Handler
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isPluginActive || !enableJoin) return;
        
        try {
            Player player = event.getPlayer();
            String message = joinMessage
                .replace("%player%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
            
            // Send to Telegram
            sendToTelegram(stripColor(message));
            incrementStat("join_messages");
            
            // if (player.hasPermission("telegramlogger.admin")) {
            //     sendToTelegram("👑 Admin " + player.getName() + " connected to the server");
            // }
            
            // Debug info
            if (debugMode) {
                logDebug("&a&l⚡ Player join processed:", false);
                logDebug("&e&l• Player: &f" + player.getName(), false);
                logDebug("&e&l• Display Name: &f" + player.getDisplayName(), false);
                logDebug("&e&l• Is Admin: &f" + player.hasPermission("telegramlogger.admin"), false);
                logDebug("&e&l• Online Players: &f" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers(), false);
            }
            
        } catch (Exception e) {
            logDebug("&c&l❌ Error handling player join event: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Player Quit Event Handler
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!isPluginActive || !enableLeave) return;
        
        try {
            Player player = event.getPlayer();
            String message = leaveMessage
                .replace("%player%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size() - 1))
                .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
            
            // Send to Telegram
            sendToTelegram(stripColor(message));
            incrementStat("leave_messages");
            
            // if (player.hasPermission("telegramlogger.admin")) {
            //     sendToTelegram("👑 Admin " + player.getName() + " disconnected from the server");
            // }
            
            // Debug info
            if (debugMode) {
                logDebug("&c&l⚡ Player quit processed:", false);
                logDebug("&e&l• Player: &f" + player.getName(), false);
                logDebug("&e&l• Display Name: &f" + player.getDisplayName(), false);
                logDebug("&e&l• Is Admin: &f" + player.hasPermission("telegramlogger.admin"), false);
                logDebug("&e&l• Remaining Players: &f" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + Bukkit.getMaxPlayers(), false);
            }
            
        } catch (Exception e) {
            logDebug("&c&l❌ Error handling player quit event: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Player Chat Event Handler
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!isPluginActive || !enableChat) return;
        
        try {
            Player player = event.getPlayer();
            String message;
            
            // Check for filtered words
            if (enableChatFilter && containsFilteredWord(event.getMessage().toLowerCase())) {
                handleFilteredMessage(player);
                return;
            }
            
            // Format message based on player type
            if (player.hasPermission("telegramlogger.admin")) {
                message = chatMessage
                    .replace("%player%", player.getName())
                    .replace("%displayname%", player.getDisplayName())
                    .replace("%message%", event.getMessage())
                    .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
            } else {
                message = chatMessage
                    .replace("%player%", player.getName())
                    .replace("%displayname%", player.getDisplayName())
                    .replace("%message%", event.getMessage())
                    .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
            }
            
            // Send to Telegram
            sendToTelegram(stripColor(message));
            incrementStat("chat_messages");
            
            // Debug info
            if (debugMode) {
                logDebug("&a&l⚡ Chat message processed:", false);
                logDebug("&e&l• Player: &f" + player.getName(), false);
                logDebug("&e&l• Message: &f" + event.getMessage(), false);
                logDebug("&e&l• Is Admin: &f" + player.hasPermission("telegramlogger.admin"), false);
                logDebug("&e&l• Format: &f" + (player.hasPermission("telegramlogger.admin") ? "Admin" : "Regular"), false);
            }
            
        } catch (Exception e) {
            logDebug("&c&l❌ Error handling chat event: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle filtered message
     */
    private void handleFilteredMessage(Player player) {
        // Send filtered message notification
        String filteredMsg = filteredMessage
            .replace("%player%", player.getName())
            .replace("%displayname%", player.getDisplayName())
            .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
            .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
        
        sendToTelegram(stripColor(filteredMsg));
        incrementStat("filtered_messages");
        
        // Notify admins
        broadcastAdminMessage("&c&l⚠ Filtered message from &f" + player.getName());
        
        // Debug info
        if (debugMode) {
            logDebug("&c&l⚠ Filtered message caught:", false);
            logDebug("&e&l• Player: &f" + player.getName(), false);
            logDebug("&e&l• Action: &fMessage blocked and admins notified", false);
        }
    }

    /**
     * Check for filtered words
     */
    private boolean containsFilteredWord(String message) {
        // If no filter words, return false
        if (filteredWords.isEmpty()) return false;
        
        // Check each word
        boolean found = filteredWords.stream().anyMatch(message.toLowerCase()::contains);
        
        if (found && debugMode) {
            logDebug("&c&l⚠ Filtered word detected in message", false);
        }
        
        return found;
    }

    /**
     * Format time for events
     */
    private String getTimeFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
    
    /**
     * Player Advancement Event Handler
     */
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        if (!isPluginActive || !enableAdvancement) return;
        
        try {
            // Skip recipe unlock advancements
            if (event.getAdvancement().getKey().getKey().contains("recipes/")) {
                if (debugMode) {
                    logDebug("&e&l⚠ Skipped recipe advancement", false);
                }
                return;
            }
            
            Player player = event.getPlayer();
            String advancementName = formatAdvancement(event.getAdvancement().getKey().getKey());
            
            // Format message with achievement emoji
            String message = advancementMessage
                .replace("%player%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%advancement%", advancementName)
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
            
            // Add special formatting for important advancements
            // if (isImportantAdvancement(advancementName)) {
            //     message = "🏆 " + message + " 🏆";
            //     broadcastPluginMessage("&6&l[Achievement] &e" + player.getName() + " &fhas made an important advancement!");
            // }
            
            // Send to Telegram
            sendToTelegram(stripColor(message));
            incrementStat("advancement_messages");
            
            // Debug info
            if (debugMode) {
                logDebug("&a&l⚡ Advancement processed:", false);
                logDebug("&e&l• Player: &f" + player.getName(), false);
                logDebug("&e&l• Advancement: &f" + advancementName, false);
                logDebug("&e&l• Important: &f" + isImportantAdvancement(advancementName), false);
            }
            
        } catch (Exception e) {
            logDebug("&c&l❌ Error handling advancement event: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Player Death Event Handler
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isPluginActive || !enableDeath) return;
        
        try {
            Player player = event.getEntity();
            Player killer = player.getKiller();
            
            // Format death message based on killer
            String formattedDeathMessage;
            if (killer != null) {
                formattedDeathMessage = "☠ " + player.getName() + " was killed by " + killer.getName() + 
                    (event.getDeathMessage() != null ? " (" + event.getDeathMessage() + ")" : "");
            } else {
                formattedDeathMessage = "☠ " + (event.getDeathMessage() != null ? event.getDeathMessage() : 
                    player.getName() + " died");
            }
            
            String message = deathMessage
                .replace("%player%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%death_message%", formattedDeathMessage)
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
            
            // Send to Telegram
            sendToTelegram(stripColor(message));
            incrementStat("death_messages");
            
            // Special notification for admin deaths
            // if (player.hasPermission("telegramlogger.admin")) {
            //    sendToTelegram("👑 ☠ Admin " + player.getName() + " has died!");
            // }
            
            // Debug info
            if (debugMode) {
                logDebug("&c&l☠ Death event processed:", false);
                logDebug("&e&l• Player: &f" + player.getName(), false);
                logDebug("&e&l• Killer: &f" + (killer != null ? killer.getName() : "None"), false);
                logDebug("&e&l• Death Message: &f" + event.getDeathMessage(), false);
                logDebug("&e&l• Is Admin: &f" + player.hasPermission("telegramlogger.admin"), false);
            }
            
        } catch (Exception e) {
            logDebug("&c&l❌ Error handling death event: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Player World Change Event Handler
     */
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (!isPluginActive || !enableWorldSwitch) return;
        
        try {
            Player player = event.getPlayer();
            String fromWorld = formatWorldName(event.getFrom().getName());
            String toWorld = formatWorldName(player.getWorld().getName());
            
            // Format message with appropriate emoji
            String worldEmoji = getWorldEmoji(toWorld);
            String message = worldSwitchMessage
                .replace("%player%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%from_world%", fromWorld)
                .replace("%to_world%", toWorld)
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
            
            // message = worldEmoji + " " + message;
            
            // Send to Telegram
            sendToTelegram(stripColor(message));
            incrementStat("world_switch_messages");
            
            // Special notification for admin world changes
            // if (player.hasPermission("telegramlogger.admin")) {
            //     broadcastAdminMessage("&e" + player.getName() + " &fchanged world to &e" + toWorld);
            // }
            
            // Debug info
            if (debugMode) {
                logDebug("&a&l⚡ World change processed:", false);
                logDebug("&e&l• Player: &f" + player.getName(), false);
                logDebug("&e&l• From World: &f" + fromWorld, false);
                logDebug("&e&l• To World: &f" + toWorld, false);
                logDebug("&e&l• Is Admin: &f" + player.hasPermission("telegramlogger.admin"), false);
            }
            
        } catch (Exception e) {
            logDebug("&c&l❌ Error handling world change event: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Format advancement name
     */
    private String formatAdvancement(String advancement) {
        String[] parts = advancement.split("/");
        StringBuilder formatted = new StringBuilder();
        
        for (String part : parts) {
            String[] words = part.split("_");
            for (String word : words) {
                if (word.length() > 0) {
                    formatted.append(word.substring(0, 1).toUpperCase())
                            .append(word.substring(1).toLowerCase())
                            .append(" ");
                }
            }
        }
        
        return formatted.toString().trim();
    }
    
    /**
     * Format world name with emoji
     */
    private String formatWorldName(String worldName) {
        switch (worldName.toLowerCase()) {
            case "world":
                return "🌍 Overworld";
            case "world_nether":
                return "🔥 The Nether";
            case "world_the_end":
                return "🌌 The End";
            case "playworld":
                return "🌍 Overworld";
            case "playworld_nether":
                return "🔥 The Nether";
            case "playworld_the_end":
                return "🌌 The End";
            case "spawn":
                return "🏔️ Spawn";
            case "lobby":
                return "🏔️ Lobby";
            case "mining":
                return "🪨 Mining";
            default:
                String[] words = worldName.split("_");
                StringBuilder formatted = new StringBuilder("🌎 ");
                for (String word : words) {
                    if (word.length() > 0) {
                        formatted.append(word.substring(0, 1).toUpperCase())
                                .append(word.substring(1).toLowerCase())
                                .append(" ");
                    }
                }
                return formatted.toString().trim();
        }
    }

    /**
     * Get world emoji
     */
    private String getWorldEmoji(String worldName) {
        switch (worldName.toLowerCase()) {
            case "overworld":
                return "🌍";
            case "the nether":
                return "🔥";
            case "the end":
                return "🌌";
            default:
                return "🌎";
        }
    }

    /**
     * Check if advancement is important
     */
    private boolean isImportantAdvancement(String advancement) {
        return advancement.toLowerCase().contains("the end") || 
               advancement.toLowerCase().contains("nether") ||
               advancement.toLowerCase().contains("wither") ||
               advancement.toLowerCase().contains("dragon");
    }
    
    /**
     * Main command handler
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("telegramlogger")) return false;
    
        if (!sender.hasPermission("telegramlogger.admin")) {
            broadcastPluginMessage("&c&l❌ You don't have permission to use this command!");
            return true;
        }
    
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
    
        try {
            switch (args[0].toLowerCase()) {
                case "reload":
                    handleReloadCommand(sender);
                    break;
                case "start":
                    handleStartCommand(sender);
                    break;
                case "stop":
                    handleStopCommand(sender);
                    break;
                case "stats":
                    handleStatsCommand(sender);
                    break;
                case "debug":
                    handleDebugCommand(sender);
                    break;
                case "admin":
                    handleAdminCommand(sender, args);
                    break;
                case "status":
                    handleStatusCommand(sender);
                    break;
                case "help":
                    sendHelpMessage(sender);
                    break;
                default:
                    sender.sendMessage(colorize(pluginPrefix + 
                        "&c&l❌ Unknown command. &eUse &f/telegramlogger help &efor commands list."));
            }
        } catch (Exception e) {
            sender.sendMessage(colorize(pluginPrefix + "&c&l❌ Error executing command: &e" + e.getMessage()));
            if (debugMode) {
                e.printStackTrace();
            }
        }
    
        return true;
    }
    
    /**
     * Handle reload command
     */
    private void handleReloadCommand(CommandSender sender) {
        if (debugMode) {
            logDebug("&e&l⚡ Reload initiated by: &f" + sender.getName(), false);
        }
        
        try {
            // Cancel all tasks and unregister listeners
            Bukkit.getScheduler().cancelTasks(this);
            HandlerList.unregisterAll((JavaPlugin) this);
            
            // Save current data
            saveDataFile();
            saveAdminsFile();
            
            // Reload config and files
            reloadConfig();
            loadConfig();
            loadDataFile();
            loadAdminsFile();
            
            // Reinitialize
            if (checkBotToken()) {
                getServer().getPluginManager().registerEvents(this, this);
                startTelegramPolling();
                isBotActive = true;
                isPluginActive = true;
                
                broadcastPluginMessage("&a&l⚡ Plugin has been reloaded successfully!");
                logDebug("&a&l⚡ Reloaded by &f" + sender.getName(), true);
                //sendToTelegram("🔄 Plugin has been reloaded by " + sender.getName());
            } else {
                isPluginActive = false;
                isBotActive = false;
                broadcastPluginMessage("&c&l❌ Bot token is invalid! Plugin won't send messages until fixed.");
                logDebug("&c&l❌ Invalid bot token after reload by &f" + sender.getName(), true);
            }
        } catch (Exception e) {
            broadcastPluginMessage("&c&l❌ Error reloading plugin: &e" + e.getMessage());
            logDebug("&c&l❌ Error during reload by &f" + sender.getName() + "&c: &e" + e.getMessage(), true);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Handle start command
     */
    private void handleStartCommand(CommandSender sender) {
        if (!isPluginActive) {
            isPluginActive = true;
            if (debugMode) {
                logDebug("&a&l⚡ Plugin activated by: &f" + sender.getName(), false);
            }
            broadcastPluginMessage("&a&l⚡ Messaging has been started!");
            //sendToTelegram("✅ Messaging has been started by " + sender.getName() + "!");
        } else {
            sender.sendMessage(colorize(pluginPrefix + "&c&l❌ Messaging is already active!"));
        }
    }
    
    /**
     * Handle stop command
     */
    private void handleStopCommand(CommandSender sender) {
        if (isPluginActive) {
            isPluginActive = false;
            if (debugMode) {
                logDebug("&c&l⚡ Plugin deactivated by: &f" + sender.getName(), false);
            }
            broadcastPluginMessage("&c&l⚡ Messaging has been stopped!");
            //sendToTelegram("🛑 Messaging has been stopped by " + sender.getName() + "!");
        } else {
            sender.sendMessage(colorize(pluginPrefix + "&c&l❌ Messaging is already inactive!"));
        }
    }
    
    /**
     * Handle debug command
     */
    private void handleDebugCommand(CommandSender sender) {
        debugMode = !debugMode;
        broadcastAdminMessage("&e&l⚡ Debug mode has been " + 
            (debugMode ? "&a&lenabled" : "&c&ldisabled") + " &e&lby &f" + sender.getName());
        
        if (debugMode) {
            // Show current status when debug enabled
            logDebug("&a&l⚡ Debug mode enabled - Current Status:", true);
            logDebug("&e&l• Plugin Active: &f" + isPluginActive, true);
            logDebug("&e&l• Bot Active: &f" + isBotActive, true);
            logDebug("&e&l• Total Messages: &f" + getStat("total_messages"), true);
            logDebug("&e&l• Online Players: &f" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers(), true);
        }
    }

    /**
     * Handle status command
     */
    private void handleStatusCommand(CommandSender sender) {
        String status = getPluginStatus();
        Arrays.stream(status.split("\n"))
            .forEach(line -> sender.sendMessage(colorize(pluginPrefix + line)));
        
        
        if (debugMode) {
            logDebug("&a&l⚡ Status checked by: &f" + sender.getName(), false);
        }
    }

    /**
     * Send help message
     */
    private void sendHelpMessage(CommandSender sender) {
        String[] helpMessages = {
            "&6&l=== TelegramLogger Help ===",
            "&e/telegramlogger reload &7- &fReload the plugin &a⚡",
            "&e/telegramlogger start &7- &fStart message forwarding &a✅",
            "&e/telegramlogger stop &7- &fStop message forwarding &c🛑",
            "&e/telegramlogger stats &7- &fView message statistics &b📊",
            "&e/telegramlogger debug &7- &fToggle debug mode &e⚠",
            "&e/telegramlogger status &7- &fShow plugin status &b💡",
            "&e/telegramlogger admin add <id> <name> &7- &fAdd new admin &a👑",
            "&e/telegramlogger admin remove <id> &7- &fRemove admin &c👑",
            "&e/telegramlogger admin list &7- &fList all admins &f📋",
            "&e/telegramlogger help &7- &fShow this help message &b❔"
        };
    
        Arrays.stream(helpMessages)
            .map(msg -> colorize(pluginPrefix + msg))
            .forEach(sender::sendMessage);
            
        if (debugMode) {
            logDebug("&a&l⚡ Help shown to: &f" + sender.getName(), false);
        }
    }
    
    /**
     * Handle admin commands
     */
    private void handleAdminCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(colorize(pluginPrefix + 
                "&c&l❌ Usage: &e/telegramlogger admin <add|remove|list>"));
            return;
        }
    
        switch (args[1].toLowerCase()) {
            case "add":
                handleAdminAdd(sender, args);
                break;
            case "remove":
                handleAdminRemove(sender, args);
                break;
            case "list":
                handleAdminList(sender);
                break;
            default:
                sender.sendMessage(colorize(pluginPrefix + 
                    "&c&l❌ Unknown admin command. Use add, remove, or list."));
        }
    }
    
    /**
     * Handle admin add command
     */
    private void handleAdminAdd(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(colorize(pluginPrefix + 
                "&c&l❌ Usage: &e/telegramlogger admin add <telegram_id> <name>"));
            return;
        }
    
        String telegramId = args[2];
        String name = args[3];
    
        try {
            // Validate telegram_id is numeric
            Long.parseLong(telegramId);
            
            if (isAdminRegistered(telegramId)) {
                broadcastAdminMessage("&c&l❌ Telegram ID &e" + telegramId + 
                    " &cis already registered as admin!");
                return;
            }
    
            // Add admin
            addAdmin(telegramId, name);
    
            if (debugMode) {
                logDebug("&a&l👑 New admin added:", false);
                logDebug("&e&l• ID: &f" + telegramId, false);
                logDebug("&e&l• Name: &f" + name, false);
                logDebug("&e&l• Added by: &f" + sender.getName(), false);
            }
    
            // Broadcast messages
            broadcastPluginMessage("&a&l👑 New admin &e" + name + " &aadded successfully!");
            //sendToTelegram("👑 <b>" + name + "</b> has been added as an admin by " + sender.getName());
    
        } catch (NumberFormatException e) {
            sender.sendMessage(colorize(pluginPrefix + 
                "&c&l❌ Invalid Telegram ID! Must be a number."));
        }
    }
    
    /**
     * Handle admin remove command
     */
    private void handleAdminRemove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(colorize(pluginPrefix + 
                "&c&l❌ Usage: &e/telegramlogger admin remove <telegram_id>"));
            return;
        }
    
        String telegramId = args[2];
        String adminName = getAdminName(telegramId);
    
        if (adminName == null) {
            broadcastAdminMessage("&c&l❌ Telegram ID &e" + telegramId + 
                " &cis not registered as admin!");
            return;
        }
    
        if (removeAdmin(telegramId)) {
            if (debugMode) {
                logDebug("&c&l👑 Admin removed:", false);
                logDebug("&e&l• ID: &f" + telegramId, false);
                logDebug("&e&l• Name: &f" + adminName, false);
                logDebug("&e&l• Removed by: &f" + sender.getName(), false);
            }
    
            // Broadcast messages
            broadcastPluginMessage("&c&l👑 Admin &e" + adminName + " &cremoved successfully!");
            //sendToTelegram("👑 <b>" + adminName + "</b> has been removed from admins by " + sender.getName());
        }
    }
    
    /**
     * Handle admin list command
     */
    private void handleAdminList(CommandSender sender) {
        Set<Map.Entry<String, JsonElement>> adminList = getAllAdmins();
        
        if (adminList.isEmpty()) {
            sender.sendMessage(colorize(pluginPrefix + "&c&l❌ No admins registered!"));
            return;
        }
    
        // Send header
        sender.sendMessage(colorize(pluginPrefix + "&6&l=== Registered Admins ==="));
        
        // Send admin list
        for (Map.Entry<String, JsonElement> entry : adminList) {
            String telegramId = entry.getKey();
            JsonObject adminObj = entry.getValue().getAsJsonObject();
            String name = adminObj.get("name").getAsString();
            String addedDate = adminObj.get("addedDate").getAsString();
            
            sender.sendMessage(colorize(pluginPrefix + 
                "&e" + name + " &7(&fID: &e" + telegramId + "&7)" +
                "\n" + pluginPrefix + "&7Added: &f" + addedDate));
        }
    
        if (debugMode) {
            logDebug("&a&l👑 Admin list viewed by: &f" + sender.getName(), false);
            logDebug("&e&l• Total Admins: &f" + adminList.size(), false);
        }
        
        // Send to Telegram if from console
        if (!(sender instanceof Player)) {
            sendAdminListToTelegram();
        }
    }
    
    /**
     * Send admin list to Telegram
     */
    private void sendAdminListToTelegram() {
        Set<Map.Entry<String, JsonElement>> adminList = getAllAdmins();
        StringBuilder message = new StringBuilder();
        message.append("👥 <b>Registered Admins</b>\n\n");
        
        if (adminList.isEmpty()) {
            message.append("❌ No admins registered!");
        } else {
            for (Map.Entry<String, JsonElement> entry : adminList) {
                JsonObject adminObj = entry.getValue().getAsJsonObject();
                message.append("👑 <b>").append(adminObj.get("name").getAsString()).append("</b>\n");
                message.append("📋 ID: ").append(entry.getKey()).append("\n");
                message.append("📅 Added: ").append(adminObj.get("addedDate").getAsString()).append("\n\n");
            }
        }
        
        sendToTelegram(message.toString());
    }

    /**
     * Admin utility methods
     */
    private boolean isAdminRegistered(String telegramId) {
        boolean registered = admins.has(telegramId);
        if (debugMode) {
            logDebug("&e&l👑 Admin check for ID " + telegramId + ": " + 
                (registered ? "&a&lRegistered" : "&c&lNot Registered"), false);
        }
        return registered;
    }
    
    private void addAdmin(String telegramId, String name) {
        JsonObject adminObj = new JsonObject();
        adminObj.addProperty("name", name);
        adminObj.addProperty("addedDate", new Date().toString());
        admins.add(telegramId, adminObj);
        saveAdminsFile();
        
        if (debugMode) {
            logDebug("&a&l👑 Admin added to database:", false);
            logDebug("&e&l• ID: &f" + telegramId, false);
            logDebug("&e&l• Name: &f" + name, false);
            logDebug("&e&l• Date: &f" + adminObj.get("addedDate").getAsString(), false);
        }
    }
    
    private boolean removeAdmin(String telegramId) {
        if (admins.has(telegramId)) {
            String name = getAdminName(telegramId);
            admins.remove(telegramId);
            saveAdminsFile();
            
            if (debugMode) {
                logDebug("&c&l👑 Admin removed from database:", false);
                logDebug("&e&l• ID: &f" + telegramId, false);
                logDebug("&e&l• Name: &f" + name, false);
            }
            return true;
        }
        return false;
    }
    
    private String getAdminName(String telegramId) {
        if (admins.has(telegramId)) {
            String name = admins.get(telegramId).getAsJsonObject().get("name").getAsString();
            if (debugMode) {
                logDebug("&e&l👑 Retrieved admin name for ID " + telegramId + ": &f" + name, false);
            }
            return name;
        }
        if (debugMode) {
            logDebug("&c&l❌ No admin found for ID: &f" + telegramId, false);
        }
        return null;
    }
    
    private Set<Map.Entry<String, JsonElement>> getAllAdmins() {
        Set<Map.Entry<String, JsonElement>> adminList = admins.entrySet();
        if (debugMode) {
            logDebug("&e&l👑 Retrieved admin list - Total admins: &f" + adminList.size(), false);
        }
        return adminList;
    }
    
    /**
     * Handle tab completion for commands
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Only suggest if has permission
        if (!sender.hasPermission("telegramlogger.admin")) {
            return new ArrayList<>();
        }
    
        List<String> completions = new ArrayList<>();
    
        try {
            // Main commands
            if (args.length == 1) {
                String[] commands = {
                    "reload", "start", "stop", "stats", 
                    "debug", "admin", "help", "status"
                };
                
                for (String cmd : commands) {
                    if (cmd.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(cmd);
                    }
                }
            }
            
            // Sub commands
            else if (args.length == 2) {
                // Admin sub commands
                if (args[0].equalsIgnoreCase("admin")) {
                    String[] subCommands = {"add", "remove", "list"};
                    
                    for (String sub : subCommands) {
                        if (sub.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(sub);
                        }
                    }
                }
            }
            
            // Admin remove command - show registered admin IDs
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("admin") && 
                    args[1].equalsIgnoreCase("remove")) {
                    
                    getAllAdmins().forEach(entry -> {
                        String id = entry.getKey();
                        String name = entry.getValue().getAsJsonObject()
                            .get("name").getAsString();
                            
                        if (id.startsWith(args[2])) {
                            completions.add(id + " (" + name + ")"); 
                        }
                    });
                }
            }

            // Admin add command - suggest format
            else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("admin") && 
                    args[1].equalsIgnoreCase("add")) {
                    completions.add("<name>");
                }
            }
            
            if (debugMode) {
                logDebug("&7Tab complete for: &f" + String.join(" ", args), false);
                logDebug("&7Suggestions: &f" + String.join(", ", completions), false);
            }
            
            return completions;
            
        } catch (Exception e) {
            if (debugMode) {
                logDebug("&c&l❌ Error in tab completion: &e" + e.getMessage(), false);
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }
    
}