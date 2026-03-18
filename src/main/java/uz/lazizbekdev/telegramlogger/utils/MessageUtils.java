package uz.lazizbekdev.telegramlogger.utils;

import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Centralized message formatting utilities for both Minecraft and Telegram output.
 */
public final class MessageUtils {

    private MessageUtils() {}

    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String stripColors(String message) {
        if (message == null) return "";
        // Protect HTML entities from color code stripping (&lt; contains &l which is bold code)
        message = message.replace("&lt;", "\u0000LT\u0000");
        message = message.replace("&gt;", "\u0000GT\u0000");
        message = message.replace("&amp;", "\u0000AMP\u0000");
        String stripped = ChatColor.stripColor(message);
        stripped = stripped.replaceAll("&[0-9a-fk-orA-FK-OR]", "");
        stripped = stripped.replaceAll("§[0-9a-fk-orA-FK-OR]", "");
        stripped = stripped.replace("\u0000LT\u0000", "&lt;");
        stripped = stripped.replace("\u0000GT\u0000", "&gt;");
        stripped = stripped.replace("\u0000AMP\u0000", "&amp;");
        return stripped;
    }

    /**
     * Replace all placeholders in a template string.
     */
    public static String applyPlaceholders(String template, Map<String, String> placeholders) {
        if (template == null) return "";
        String result = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Format world name with emoji for display.
     */
    public static String formatWorldName(String worldName) {
        if (worldName == null) return "Unknown";
        switch (worldName.toLowerCase()) {
            case "world":
            case "playworld":
                return "\uD83C\uDF0D Overworld";
            case "world_nether":
            case "playworld_nether":
                return "\uD83D\uDD25 The Nether";
            case "world_the_end":
            case "playworld_the_end":
                return "\uD83C\uDF0C The End";
            case "spawn":
                return "\uD83C\uDFD4\uFE0F Spawn";
            case "lobby":
                return "\uD83C\uDFD4\uFE0F Lobby";
            case "mining":
                return "\uD83E\uDEA8 Mining";
            default:
                String[] words = worldName.split("_");
                StringBuilder formatted = new StringBuilder("\uD83C\uDF0E ");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        formatted.append(word.substring(0, 1).toUpperCase())
                                .append(word.substring(1).toLowerCase())
                                .append(" ");
                    }
                }
                return formatted.toString().trim();
        }
    }

    /**
     * Convert advancement key to human-readable name.
     */
    public static String formatAdvancement(String advancement) {
        if (advancement == null) return "Unknown";
        String[] parts = advancement.split("/");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            String[] words = part.split("_");
            for (String word : words) {
                if (!word.isEmpty()) {
                    formatted.append(word.substring(0, 1).toUpperCase())
                            .append(word.substring(1).toLowerCase())
                            .append(" ");
                }
            }
        }
        return formatted.toString().trim();
    }

    public static boolean isImportantAdvancement(String advancement) {
        if (advancement == null) return false;
        String lower = advancement.toLowerCase();
        return lower.contains("the end") || lower.contains("nether")
                || lower.contains("wither") || lower.contains("dragon")
                || lower.contains("elytra") || lower.contains("beacon");
    }

    /**
     * Format seconds into a human-readable duration string.
     */
    public static String formatDuration(long seconds) {
        if (seconds < 60) return seconds + "s";
        long minutes = seconds / 60;
        seconds %= 60;
        if (minutes < 60) return String.format("%dm %ds", minutes, seconds);
        long hours = minutes / 60;
        minutes %= 60;
        if (hours < 24) return String.format("%dh %dm %ds", hours, minutes, seconds);
        long days = hours / 24;
        hours %= 24;
        return String.format("%dd %dh %dm", days, hours, minutes);
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String getDateTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    /**
     * Build a visual progress bar for stats display.
     */
    public static String generateProgressBar(double percentage) {
        int barLength = 20;
        int filled = (int) Math.round(barLength * percentage / 100.0);
        StringBuilder bar = new StringBuilder("&8[&a");
        for (int i = 0; i < filled; i++) bar.append("\u25A0");
        bar.append("&7");
        for (int i = filled; i < barLength; i++) bar.append("\u25A0");
        bar.append("&8]");
        return bar.toString();
    }

    /**
     * Escape HTML special characters for safe Telegram message rendering.
     */
    public static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Clean chat component tags from player messages.
     * Plugins like mention/chat formatters inject tags like {@code <chat=UUID:text>}
     * which should be stripped before sending to Telegram.
     */
    public static String cleanChatComponents(String message) {
        if (message == null) return "";
        // Handle the specific mention format, which is a tag containing @ and followed by :>
        message = message.replaceAll("(<[^>]*@([^>]*)>):>", "$2");
        // Generic cleaning for other chat components
        return message.replaceAll("<[^>]*=[^>]*>", "").trim();
    }

    /**
     * Extract prefix from a player's display name (everything before the raw name).
     */
    public static String extractPrefix(String displayName, String playerName) {
        if (displayName == null || playerName == null) return "";
        String clean = stripColors(displayName);
        int idx = clean.indexOf(playerName);
        if (idx <= 0) return "";
        return clean.substring(0, idx).trim();
    }

    /**
     * Extract suffix from a player's display name (everything after the raw name).
     */
    public static String extractSuffix(String displayName, String playerName) {
        if (displayName == null || playerName == null) return "";
        String clean = stripColors(displayName);
        int idx = clean.indexOf(playerName);
        if (idx < 0) return "";
        int end = idx + playerName.length();
        if (end >= clean.length()) return "";
        return clean.substring(end).trim();
    }

    /**
     * Apply prefix replacement rules from config.
     */
    public static String applyPrefixReplacements(String text, Map<String, String> replacements) {
        if (text == null || replacements == null || replacements.isEmpty()) return text;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }
}
