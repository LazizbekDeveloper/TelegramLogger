package uz.lazizbekdev.telegramlogger.telegram;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import uz.lazizbekdev.telegramlogger.TelegramLogger;
import uz.lazizbekdev.telegramlogger.config.ConfigManager;
import uz.lazizbekdev.telegramlogger.managers.AdminManager;
import uz.lazizbekdev.telegramlogger.managers.DataManager;
import uz.lazizbekdev.telegramlogger.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

public class TelegramHandler {

    private final TelegramLogger plugin;
    private volatile long lastUpdateId = 0;
    private final AtomicBoolean polling = new AtomicBoolean(false);

    public TelegramHandler(TelegramLogger plugin) {
        this.plugin = plugin;
    }

    public long getLastUpdateId() { return lastUpdateId; }
    public void setLastUpdateId(long id) { this.lastUpdateId = id; }

    public void startPolling() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfigManager().isSendTelegramMessagesToGame()
                        && !plugin.getConfigManager().isEnableSudoCommand()) {
                    return;
                }
                if (!plugin.isPluginActive() || !plugin.isBotActive()) return;
                if (!polling.compareAndSet(false, true)) return;

                try {
                    String response = plugin.getTelegramAPI().getUpdates(lastUpdateId + 1, 10);
                    JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                    if (json.get("ok").getAsBoolean() && json.has("result")) {
                        processUpdates(json.get("result").getAsJsonArray());
                    }
                } catch (Exception e) {
                    if (plugin.getConfigManager().isDebugMode()) {
                        plugin.getLogger().warning("Poll error: " + e.getMessage());
                    }
                } finally {
                    polling.set(false);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    private void processUpdates(JsonArray updates) {
        for (JsonElement elem : updates) {
            try {
                JsonObject update = elem.getAsJsonObject();
                long updateId = update.get("update_id").getAsLong();

                if (updateId <= lastUpdateId) continue;
                lastUpdateId = updateId;

                if (!update.has("message")) continue;
                JsonObject message = update.get("message").getAsJsonObject();

                if (!message.has("text") || !message.has("from") || !message.has("chat")) continue;

                String messageText = message.get("text").getAsString();
                long userId = message.get("from").getAsJsonObject().get("id").getAsLong();
                String userIdStr = String.valueOf(userId);
                long chatIdFromMsg = message.get("chat").getAsJsonObject().get("id").getAsLong();
                int messageId = message.get("message_id").getAsInt();

                int threadIdFromMsg = message.has("message_thread_id")
                        ? message.get("message_thread_id").getAsInt() : 0;

                if (plugin.getConfigManager().isEnableSudoCommand()
                        && messageText.startsWith("/sudo")) {
                    handleSudo(userIdStr, messageText, chatIdFromMsg, threadIdFromMsg, messageId);
                    continue;
                }

                String expectedChatId = plugin.getConfigManager().getChatId();
                if (!String.valueOf(chatIdFromMsg).equals(expectedChatId)) continue;

                if (!isValidThread(message)) continue;

                if (messageText.startsWith("/")) {
                    handleCommand(userIdStr, messageText);
                } else if (plugin.getConfigManager().isSendTelegramMessagesToGame()) {
                    handleChatMessage(userId, userIdStr, messageText);
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Error processing Telegram update: " + e.getMessage());
            }
        }
    }

    private boolean isValidThread(JsonObject message) {
        if (!plugin.getConfigManager().isSendToThread()) return true;
        if (!message.has("message_thread_id")) return false;
        String expected = plugin.getConfigManager().getThreadId();
        return String.valueOf(message.get("message_thread_id").getAsInt()).equals(expected);
    }

    // --- Sudo command (uses ConsoleSender + logger capture) ---

    private void handleSudo(String userIdStr, String messageText, long chatId, int threadId, int messageId) {
        AdminManager admins = plugin.getAdminManager();

        if (!admins.isRegistered(userIdStr)) {
            plugin.getTelegramAPI().sendReply(chatId, threadId, messageId,
                    "\u274C You are not authorized to use sudo commands!");
            return;
        }

        String command = messageText;
        int spaceIdx = command.indexOf(' ');
        if (spaceIdx == -1) {
            plugin.getTelegramAPI().sendReply(chatId, threadId, messageId,
                    "\u274C Usage: <code>/sudo &lt;command&gt;</code>");
            return;
        }
        command = command.substring(spaceIdx + 1).trim();
        if (command.isEmpty()) {
            plugin.getTelegramAPI().sendReply(chatId, threadId, messageId,
                    "\u274C Usage: <code>/sudo &lt;command&gt;</code>");
            return;
        }

        ConfigManager cfg = plugin.getConfigManager();
        String cmdLower = command.toLowerCase();
        for (String blocked : cfg.getSudoBlacklist()) {
            if (cmdLower.startsWith(blocked)) {
                plugin.getTelegramAPI().sendReply(chatId, threadId, messageId,
                        "\u274C This command is blacklisted.");
                return;
            }
        }

        String adminName = admins.getName(userIdStr);
        final String finalCommand = command;

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                List<String> output = new ArrayList<>();

                // Capture console output via a temporary logger handler
                java.util.logging.Logger serverLogger = Bukkit.getServer().getLogger();
                Handler captureHandler = new Handler() {
                    @Override
                    public void publish(LogRecord record) {
                        if (record.getMessage() != null) {
                            output.add(record.getMessage());
                        }
                    }
                    @Override public void flush() {}
                    @Override public void close() {}
                };
                serverLogger.addHandler(captureHandler);

                boolean success;
                try {
                    success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                } finally {
                    serverLogger.removeHandler(captureHandler);
                }

                StringBuilder response = new StringBuilder();
                if (success) {
                    response.append("\u2705 <b>Command executed</b>\n");
                } else {
                    response.append("\u26A0\uFE0F <b>Command returned false</b>\n");
                }
                response.append("\uD83D\uDCBB <code>").append(MessageUtils.escapeHtml(finalCommand)).append("</code>\n");
                response.append("\uD83D\uDC64 Admin: <b>").append(MessageUtils.escapeHtml(adminName)).append("</b>");

                if (cfg.isSudoShowOutput() && !output.isEmpty()) {
                    response.append("\n\n\uD83D\uDCCB <b>Output:</b>\n<pre>");
                    String outputText = String.join("\n", output);
                    if (outputText.length() > 3000) {
                        outputText = outputText.substring(0, 3000) + "\n... (truncated)";
                    }
                    response.append(MessageUtils.escapeHtml(outputText));
                    response.append("</pre>");
                }

                plugin.getTelegramAPI().sendReply(chatId, threadId, messageId, response.toString());

            } catch (Exception e) {
                plugin.getTelegramAPI().sendReply(chatId, threadId, messageId,
                        "\u274C Error: " + MessageUtils.escapeHtml(e.getMessage()));
            }
        });
    }

    // --- Telegram commands ---

    private void handleCommand(String userIdStr, String messageText) {
        AdminManager admins = plugin.getAdminManager();
        if (!admins.isRegistered(userIdStr)) {
            plugin.getTelegramAPI().sendMessage(plugin.getConfigManager().getErrorNotAdmin());
            return;
        }

        String cmd = messageText.split("@")[0].toLowerCase().trim();

        if (cmd.equals("/status")) {
            sendTelegramStatus();
        } else if (cmd.equals("/stats")) {
            sendTelegramStats();
        } else if (cmd.equals("/players") || cmd.equals("/online")) {
            sendOnlinePlayers();
        } else if (cmd.equals("/start")) {
            handleTelegramStart(admins.getName(userIdStr));
        } else if (cmd.equals("/stop")) {
            handleTelegramStop(admins.getName(userIdStr));
        } else if (cmd.equals("/debug")) {
            handleTelegramDebug(admins.getName(userIdStr));
        } else if (cmd.equals("/help")) {
            sendTelegramHelp();
        } else if (cmd.equals("/tps")) {
            sendServerTps();
        } else {
            plugin.getTelegramAPI().sendMessage(
                    "\u274C Unknown command. Use /help for available commands.");
        }
    }

    private void sendTelegramStatus() {
        ConfigManager cfg = plugin.getConfigManager();
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCCA <b>TelegramLogger Status</b>\n\n");
        sb.append("\u2022 Plugin: ").append(plugin.isPluginActive() ? "\u2705 Active" : "\u274C Inactive").append("\n");
        sb.append("\u2022 Bot: ").append(plugin.isBotActive() ? "\u2705 Connected" : "\u274C Disconnected").append("\n");
        sb.append("\u2022 Debug: ").append(cfg.isDebugMode() ? "\u2705 On" : "\u274C Off").append("\n\n");

        sb.append("<b>Features:</b>\n");
        sb.append(featureStatus("Join", cfg.isEnableJoin()));
        sb.append(featureStatus("Leave", cfg.isEnableLeave()));
        sb.append(featureStatus("Chat", cfg.isEnableChat()));
        sb.append(featureStatus("Advancement", cfg.isEnableAdvancement()));
        sb.append(featureStatus("Death", cfg.isEnableDeath()));
        sb.append(featureStatus("World Switch", cfg.isEnableWorldSwitch()));
        sb.append(featureStatus("Chat Filter", cfg.isEnableChatFilter()));
        sb.append(featureStatus("Sudo", cfg.isEnableSudoCommand()));
        sb.append(featureStatus("First Join", cfg.isEnableFirstJoin()));
        sb.append(featureStatus("Anti-Flood", cfg.isAntiFloodEnabled()));

        sb.append("\n<b>Server:</b>\n");
        sb.append("\u2022 Version: <code>").append(Bukkit.getVersion()).append("</code>\n");
        sb.append("\u2022 Players: <b>").append(Bukkit.getOnlinePlayers().size())
          .append("/").append(Bukkit.getMaxPlayers()).append("</b>\n");

        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        long totalMB = rt.totalMemory() / 1024 / 1024;
        sb.append("\u2022 Memory: <code>").append(usedMB).append("/").append(totalMB).append(" MB</code>");

        plugin.getTelegramAPI().sendMessage(sb.toString());
    }

    private String featureStatus(String name, boolean enabled) {
        return "\u2022 " + name + ": " + (enabled ? "\u2705" : "\u274C") + "\n";
    }

    private void sendTelegramStats() {
        String stats = plugin.getDataManager().getTelegramStats(
                plugin.getConfigManager().isEnableSendCommandExecutes());
        plugin.getTelegramAPI().sendMessage(stats);
    }

    private void sendOnlinePlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            plugin.getTelegramAPI().sendMessage(
                    "\uD83D\uDC65 <b>No players online</b>");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDC65 <b>Online Players</b> (")
          .append(players.size()).append("/").append(Bukkit.getMaxPlayers()).append(")\n\n");

        List<? extends Player> list = players.stream().limit(20).collect(Collectors.toList());
        for (Player p : list) {
            sb.append("\u2022 ");
            if (p.hasPermission("telegramlogger.admin")) sb.append("\uD83D\uDC51 ");
            sb.append("<b>").append(MessageUtils.escapeHtml(p.getName())).append("</b>");
            String displayClean = MessageUtils.stripColors(p.getDisplayName());
            if (!displayClean.equals(p.getName())) {
                sb.append(" (").append(MessageUtils.escapeHtml(displayClean)).append(")");
            }
            sb.append("\n");
        }

        if (players.size() > 20) {
            sb.append("\n<i>...and ").append(players.size() - 20).append(" more</i>");
        }

        plugin.getTelegramAPI().sendMessage(sb.toString());
    }

    private void handleTelegramStart(String adminName) {
        if (plugin.isPluginActive()) {
            plugin.getTelegramAPI().sendMessage("\u274C Messaging is already active!");
            return;
        }
        plugin.setPluginActive(true);
        plugin.getTelegramAPI().sendMessage(
                "\u25B6\uFE0F Messaging started by " + adminName);
    }

    private void handleTelegramStop(String adminName) {
        if (!plugin.isPluginActive()) {
            plugin.getTelegramAPI().sendMessage("\u274C Messaging is already inactive!");
            return;
        }
        plugin.setPluginActive(false);
        plugin.getTelegramAPI().sendMessage(
                "\u23F9\uFE0F Messaging stopped by " + adminName);
    }

    private void handleTelegramDebug(String adminName) {
        ConfigManager cfg = plugin.getConfigManager();
        cfg.setDebugMode(!cfg.isDebugMode());
        String state = cfg.isDebugMode() ? "enabled" : "disabled";
        plugin.getTelegramAPI().sendMessage(
                "\uD83D\uDC1E Debug mode " + state + " by " + adminName);
    }

    private void sendTelegramHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCDA <b>TelegramLogger Commands</b>\n\n");
        sb.append("\uD83D\uDD39 /status \u2014 Server &amp; plugin status\n");
        sb.append("\uD83D\uDCCA /stats \u2014 Message statistics\n");
        sb.append("\uD83D\uDC65 /players \u2014 Online player list\n");
        sb.append("\u25B6\uFE0F /start \u2014 Start message forwarding\n");
        sb.append("\u23F9\uFE0F /stop \u2014 Stop message forwarding\n");
        sb.append("\uD83D\uDC1E /debug \u2014 Toggle debug mode\n");
        sb.append("\uD83D\uDCBB /tps \u2014 Server performance\n");
        if (plugin.getConfigManager().isEnableSudoCommand()) {
            sb.append("\uD83D\uDC51 /sudo [command] \u2014 Execute server command\n");
        }
        sb.append("\u2753 /help \u2014 This help message\n\n");
        sb.append("\u2728 Send any text to broadcast it in-game.");
        plugin.getTelegramAPI().sendMessage(sb.toString());
    }

    private void sendServerTps() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
                double[] tps = (double[]) server.getClass().getField("recentTps").get(server);
                StringBuilder sb = new StringBuilder();
                sb.append("\uD83D\uDCBB <b>Server Performance</b>\n\n");
                sb.append("\u2022 TPS (1m): <code>").append(String.format("%.2f", Math.min(tps[0], 20.0))).append("</code>\n");
                sb.append("\u2022 TPS (5m): <code>").append(String.format("%.2f", Math.min(tps[1], 20.0))).append("</code>\n");
                sb.append("\u2022 TPS (15m): <code>").append(String.format("%.2f", Math.min(tps[2], 20.0))).append("</code>\n\n");

                Runtime rt = Runtime.getRuntime();
                long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
                long totalMB = rt.maxMemory() / 1024 / 1024;
                sb.append("\u2022 Memory: <code>").append(usedMB).append("/").append(totalMB).append(" MB</code>\n");
                sb.append("\u2022 Players: <b>").append(Bukkit.getOnlinePlayers().size())
                  .append("/").append(Bukkit.getMaxPlayers()).append("</b>");

                plugin.getTelegramAPI().sendMessage(sb.toString());
            } catch (Exception e) {
                plugin.getTelegramAPI().sendMessage(
                        "\u274C Could not retrieve TPS information.");
            }
        });
    }

    // --- Chat message handling ---

    private void handleChatMessage(long userId, String userIdStr, String messageText) {
        AdminManager admins = plugin.getAdminManager();

        String status = plugin.getTelegramAPI().getChatMemberStatus(userId);
        boolean isGroupAdmin = status.equals("creator") || status.equals("administrator");
        boolean isRegistered = admins.isRegistered(userIdStr);

        if (!isGroupAdmin || !isRegistered) {
            plugin.getTelegramAPI().sendMessage(plugin.getConfigManager().getErrorNotAdmin());
            return;
        }

        String adminName = admins.getName(userIdStr);
        if (adminName == null) adminName = "Admin";

        if (messageText.length() > 512) {
            plugin.getTelegramAPI().sendMessage("\u274C Message too long (max 512 chars).");
            return;
        }

        if (containsFilteredWord(messageText)) {
            plugin.getTelegramAPI().sendMessage("\u274C Message contains filtered words.");
            return;
        }

        broadcastToMinecraft(adminName, messageText);
        plugin.getDataManager().incrementStat("chat_messages");
    }

    private boolean containsFilteredWord(String message) {
        String lower = message.toLowerCase();
        return plugin.getConfigManager().getFilteredWords().stream().anyMatch(lower::contains);
    }

    public void broadcastToMinecraft(String senderName, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigManager cfg = plugin.getConfigManager();
                String template = cfg.getTelegramGameMessage();
                String formatted = MessageUtils.colorize(
                        template.replace("%name%", senderName)
                                .replace("%message%", message));

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(formatted);
                }
                plugin.getLogger().info("[TG] " + senderName + ": " + message);
            }
        }.runTask(plugin);
    }
}
