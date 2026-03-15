package uz.lazizbekdev.telegramlogger.managers;

import com.google.gson.*;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Manages Telegram admin registrations stored in admins.json.
 */
public class AdminManager {

    private final File adminsFile;
    private final Logger logger;
    private JsonObject admins;

    public AdminManager(File dataFolder, Logger logger) {
        this.adminsFile = new File(dataFolder, "admins.json");
        this.logger = logger;
        load();
    }

    private void load() {
        if (!adminsFile.exists()) {
            admins = new JsonObject();
            save();
            return;
        }
        try (FileReader reader = new FileReader(adminsFile)) {
            admins = JsonParser.parseReader(reader).getAsJsonObject();
            validate();
        } catch (Exception e) {
            logger.warning("Failed to load admins.json, creating fresh: " + e.getMessage());
            admins = new JsonObject();
            save();
        }
    }

    private void validate() {
        boolean changed = false;
        for (Map.Entry<String, JsonElement> entry : admins.entrySet()) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            if (!obj.has("name")) {
                obj.addProperty("name", "Unknown");
                changed = true;
            }
            if (!obj.has("addedDate")) {
                obj.addProperty("addedDate", new Date().toString());
                changed = true;
            }
        }
        if (changed) save();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(adminsFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(admins, writer);
        } catch (IOException e) {
            logger.severe("Failed to save admins.json: " + e.getMessage());
        }
    }

    public boolean isRegistered(String telegramId) {
        return admins.has(telegramId);
    }

    public String getName(String telegramId) {
        if (!admins.has(telegramId)) return null;
        return admins.get(telegramId).getAsJsonObject().get("name").getAsString();
    }

    public void addAdmin(String telegramId, String name) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("addedDate", new Date().toString());
        admins.add(telegramId, obj);
        save();
    }

    public boolean removeAdmin(String telegramId) {
        if (!admins.has(telegramId)) return false;
        admins.remove(telegramId);
        save();
        return true;
    }

    public Set<Map.Entry<String, JsonElement>> getAll() {
        return admins.entrySet();
    }

    public int count() {
        return admins.size();
    }

    /**
     * Build an HTML-formatted admin list for Telegram.
     */
    public String getTelegramAdminList() {
        if (admins.size() == 0) return "\u274C No admins registered.";

        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDC51 <b>Registered Admins</b> (").append(admins.size()).append(")\n\n");
        for (Map.Entry<String, JsonElement> entry : admins.entrySet()) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            sb.append("\u2022 <b>").append(obj.get("name").getAsString()).append("</b>\n");
            sb.append("  ID: <code>").append(entry.getKey()).append("</code>\n");
            sb.append("  Added: ").append(obj.get("addedDate").getAsString()).append("\n\n");
        }
        return sb.toString();
    }
}
