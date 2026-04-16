package uz.lazizbekdev.telegramlogger.managers;

import com.google.gson.*;
import uz.lazizbekdev.telegramlogger.utils.MessageUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages plugin statistics stored in data.json.
 * Tracks message counts by category and command usage.
 */
public class DataManager {

    private static final String[] REQUIRED_STATS = {
        "total_messages", "join_messages", "leave_messages", "chat_messages",
        "advancement_messages", "death_messages", "world_switch_messages",
        "filtered_messages", "command_messages", "ignored_commands_count",
        "first_join_messages", "server_start_count", "pet_death_messages"
    };

    private final File dataFile;
    private final Logger logger;
    private JsonObject data;

    public DataManager(File dataFolder, Logger logger) {
        this.dataFile = new File(dataFolder, "data.json");
        this.logger = logger;
        load();
    }

    private void load() {
        if (!dataFile.exists()) {
            data = new JsonObject();
            initDefaults();
            save();
            return;
        }
        try (FileReader reader = new FileReader(dataFile)) {
            data = JsonParser.parseReader(reader).getAsJsonObject();
            validate();
        } catch (Exception e) {
            logger.warning("Failed to load data.json, creating fresh: " + e.getMessage());
            data = new JsonObject();
            initDefaults();
            save();
        }
    }

    private void initDefaults() {
        for (String stat : REQUIRED_STATS) {
            if (!data.has(stat)) data.addProperty(stat, 0);
        }
        if (!data.has("command_tracking")) {
            JsonObject tracking = new JsonObject();
            tracking.addProperty("total_tracked", 0);
            data.add("command_tracking", tracking);
        }
    }

    private void validate() {
        boolean changed = false;
        for (String stat : REQUIRED_STATS) {
            if (!data.has(stat)) {
                data.addProperty(stat, 0);
                changed = true;
            }
        }
        if (!data.has("command_tracking")) {
            JsonObject tracking = new JsonObject();
            tracking.addProperty("total_tracked", 0);
            data.add("command_tracking", tracking);
            changed = true;
        }
        if (changed) save();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (IOException e) {
            logger.severe("Failed to save data.json: " + e.getMessage());
        }
    }

    public void incrementStat(String name) {
        data.addProperty(name, getStat(name) + 1);
        data.addProperty("total_messages", getStat("total_messages") + 1);
        save();
    }

    public int getStat(String name) {
        return data.has(name) ? data.get(name).getAsInt() : 0;
    }

    public void incrementCommandTracking() {
        JsonObject tracking = data.getAsJsonObject("command_tracking");
        if (tracking == null) {
            tracking = new JsonObject();
            tracking.addProperty("total_tracked", 0);
            data.add("command_tracking", tracking);
        }
        tracking.addProperty("total_tracked", tracking.get("total_tracked").getAsInt() + 1);
        save();
    }

    public void incrementIgnoredCommands() {
        data.addProperty("ignored_commands_count", getStat("ignored_commands_count") + 1);
        save();
    }

    /**
     * Build a detailed stats report for in-game display (with color codes).
     */
    public String getFormattedStats(boolean includeCommands) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n&6&l=== TelegramLogger Statistics ===\n");

        int total = getStat("total_messages");
        if (total == 0) {
            sb.append("\n&e&lNo messages recorded yet.\n");
            return sb.toString();
        }

        sb.append("\n&e&lTotal Messages: &f").append(total).append("\n");

        Map<String, String> statLabels = new LinkedHashMap<>();
        statLabels.put("join_messages", "&a&lJoin");
        statLabels.put("leave_messages", "&c&lLeave");
        statLabels.put("chat_messages", "&f&lChat");
        statLabels.put("advancement_messages", "&d&lAdvancement");
        statLabels.put("death_messages", "&4&lDeath");
        statLabels.put("pet_death_messages", "&d&lPet Death");
        statLabels.put("world_switch_messages", "&b&lWorld Switch");
        statLabels.put("filtered_messages", "&6&lFiltered");
        statLabels.put("first_join_messages", "&a&lFirst Join");
        if (includeCommands) {
            statLabels.put("command_messages", "&3&lCommand");
        }

        for (Map.Entry<String, String> entry : statLabels.entrySet()) {
            int value = getStat(entry.getKey());
            double pct = (value * 100.0) / total;
            String bar = MessageUtils.generateProgressBar(pct);
            sb.append("\n").append(entry.getValue())
              .append("&7: &f").append(value)
              .append(" &7(&e").append(String.format("%.1f%%&7)", pct))
              .append("\n&7").append(bar);
        }

        if (includeCommands) {
            sb.append("\n\n&3&lCommand Details:");
            sb.append("\n&7  Ignored: &f").append(getStat("ignored_commands_count"));
        }

        return sb.toString();
    }

    /**
     * Build stats for Telegram display (HTML formatted).
     */
    public String getTelegramStats(boolean includeCommands) {
        int total = getStat("total_messages");
        if (total == 0) return "\uD83D\uDCCA <b>No messages recorded yet.</b>";

        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCCA <b>TelegramLogger Statistics</b>\n\n");
        sb.append("\uD83D\uDCE8 Total Messages: <b>").append(total).append("</b>\n\n");

        String[][] stats = {
            {"join_messages", "\u2795 Join", String.valueOf(getStat("join_messages"))},
            {"leave_messages", "\u2796 Leave", String.valueOf(getStat("leave_messages"))},
            {"chat_messages", "\uD83D\uDCAC Chat", String.valueOf(getStat("chat_messages"))},
            {"advancement_messages", "\uD83C\uDFC6 Advancement", String.valueOf(getStat("advancement_messages"))},
            {"death_messages", "\uD83D\uDC80 Death", String.valueOf(getStat("death_messages"))},
            {"pet_death_messages", "\uD83D\uDC3E Pet Death", String.valueOf(getStat("pet_death_messages"))},
            {"world_switch_messages", "\uD83C\uDF0D World Switch", String.valueOf(getStat("world_switch_messages"))},
            {"filtered_messages", "\uD83D\uDEAB Filtered", String.valueOf(getStat("filtered_messages"))},
            {"first_join_messages", "\uD83C\uDF1F First Join", String.valueOf(getStat("first_join_messages"))},
        };

        for (String[] stat : stats) {
            int val = Integer.parseInt(stat[2]);
            double pct = (val * 100.0) / total;
            sb.append(stat[1]).append(": <b>").append(val).append("</b>")
              .append(" (").append(String.format("%.1f%%", pct)).append(")\n");
        }

        if (includeCommands) {
            sb.append("\n\uD83D\uDD27 Commands: <b>").append(getStat("command_messages")).append("</b>");
            sb.append("\n\u26D4 Ignored: <b>").append(getStat("ignored_commands_count")).append("</b>");
        }

        return sb.toString();
    }
}
