package uz.lazizbekdev.telegramlogger.telegram;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import uz.lazizbekdev.telegramlogger.TelegramLogger;
import uz.lazizbekdev.telegramlogger.config.ConfigManager;
import uz.lazizbekdev.telegramlogger.utils.AntiFloodManager;
import uz.lazizbekdev.telegramlogger.utils.MessageUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Low-level HTTP communication with the Telegram Bot API.
 * All calls are asynchronous unless explicitly synchronous.
 */
public class TelegramAPI {

    private final TelegramLogger plugin;

    public TelegramAPI(TelegramLogger plugin) {
        this.plugin = plugin;
    }

    /**
     * Send a message to the main configured chat (async).
     */
    public void sendMessage(String message) {
        ConfigManager cfg = plugin.getConfigManager();
        sendMessage(cfg.getChatId(), cfg.getThreadId(), message, cfg.isSendToThread());
    }

    /**
     * Send a message to a specific chat/thread (async).
     */
    public void sendMessage(String chatId, String threadId, String message, boolean useThread) {
        if (!plugin.isBotActive() || !plugin.isPluginActive()) return;

        AntiFloodManager flood = plugin.getAntiFloodManager();
        if (!flood.tryAcquireGlobal()) {
            if (plugin.getConfigManager().isDebugMode()) {
                plugin.getLogger().info("[AntiFlood] Message rate-limited");
            }
            return;
        }

        String clean = MessageUtils.stripColors(message);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String urlString = "https://api.telegram.org/bot" + plugin.getConfigManager().getBotToken() + "/sendMessage";
                StringBuilder params = new StringBuilder();
                params.append("chat_id=").append(URLEncoder.encode(chatId, "UTF-8"));
                params.append("&text=").append(URLEncoder.encode(clean, "UTF-8"));
                params.append("&parse_mode=HTML&disable_web_page_preview=true");
                if (useThread && threadId != null && !threadId.isEmpty()) {
                    params.append("&message_thread_id=").append(URLEncoder.encode(threadId, "UTF-8"));
                }

                int code = doPost(urlString, params.toString());
                if (code != 200 && plugin.getConfigManager().isDebugMode()) {
                    plugin.getLogger().warning("Telegram sendMessage failed, HTTP " + code);
                }
            } catch (Exception e) {
                if (plugin.getConfigManager().isDebugMode()) {
                    plugin.getLogger().warning("Telegram send error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Reply to a specific message (async).
     * Handles missing thread ID gracefully.
     */
    public void sendReply(long chatId, int threadId, int messageId, String text) {
        if (!plugin.isBotActive()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String urlString = "https://api.telegram.org/bot" + plugin.getConfigManager().getBotToken() + "/sendMessage";
                StringBuilder params = new StringBuilder();
                params.append("chat_id=").append(chatId);
                params.append("&reply_to_message_id=").append(messageId);
                params.append("&text=").append(URLEncoder.encode(text, "UTF-8"));
                params.append("&parse_mode=HTML");
                if (threadId > 0) {
                    params.append("&message_thread_id=").append(threadId);
                }

                doPost(urlString, params.toString());
            } catch (Exception e) {
                if (plugin.getConfigManager().isDebugMode()) {
                    plugin.getLogger().warning("Telegram reply error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Send a command execution log to the command-specific chat (async).
     */
    public void sendCommandExecute(String message) {
        ConfigManager cfg = plugin.getConfigManager();
        if (!cfg.isEnableSendCommandExecutes() || cfg.getCommandExecutesChatId().isEmpty()) return;
        sendMessage(cfg.getCommandExecutesChatId(), cfg.getCommandExecutesGroupThreadId(),
                message, cfg.isSendCommandExecutesToThread());
    }

    /**
     * Validate the configured bot token. Blocks the calling thread.
     */
    public boolean checkToken(String token) {
        if (token == null || token.isEmpty() || token.equals("BOT_TOKEN")) return false;
        try {
            URL url = new URL("https://api.telegram.org/bot" + token + "/getMe");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fetch pending updates from Telegram (blocking call, used in async task).
     */
    public String getUpdates(long offset, int timeout) throws IOException {
        String token = plugin.getConfigManager().getBotToken();
        String urlString = String.format(
                "https://api.telegram.org/bot%s/getUpdates?offset=%d&timeout=%d",
                token, offset, timeout);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(timeout * 1000 + 5000);
        conn.setReadTimeout(timeout * 1000 + 5000);

        if (conn.getResponseCode() != 200) {
            conn.disconnect();
            throw new IOException("HTTP " + conn.getResponseCode());
        }

        String response = readStream(conn.getInputStream());
        conn.disconnect();
        return response;
    }

    /**
     * Delete any existing webhook to allow getUpdates (polling).
     * Blocks the calling thread.
     */
    public boolean deleteWebhook() {
        String token = plugin.getConfigManager().getBotToken();
        if (token == null || token.isEmpty() || token.equals("BOT_TOKEN")) return false;
        try {
            URL url = new URL("https://api.telegram.org/bot" + token + "/deleteWebhook");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get a user's chat member status (blocking).
     */
    public String getChatMemberStatus(long userId) {
        try {
            String token = plugin.getConfigManager().getBotToken();
            String chatId = plugin.getConfigManager().getChatId();
            String urlString = String.format(
                    "https://api.telegram.org/bot%s/getChatMember?chat_id=%s&user_id=%d",
                    token, chatId, userId);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                return "";
            }

            String response = readStream(conn.getInputStream());
            conn.disconnect();

            JsonObject result = JsonParser.parseString(response).getAsJsonObject();
            if (result.get("ok").getAsBoolean() && result.has("result")) {
                return result.get("result").getAsJsonObject().get("status").getAsString();
            }
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugMode()) {
                plugin.getLogger().warning("getChatMemberStatus error: " + e.getMessage());
            }
        }
        return "";
    }

    // ─── Internal helpers ────────────────────────────────

    private int doPost(String urlString, String params) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        conn.disconnect();
        return code;
    }

    private String readStream(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}
