package uz.lazizbekdev.telegramlogger.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import uz.lazizbekdev.telegramlogger.TelegramLogger;
import uz.lazizbekdev.telegramlogger.config.ConfigManager;
import uz.lazizbekdev.telegramlogger.managers.AdminManager;
import uz.lazizbekdev.telegramlogger.managers.DataManager;
import uz.lazizbekdev.telegramlogger.utils.MessageUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles /telegramlogger (/tl) in-game commands and tab completion.
 */
public class CommandHandler implements CommandExecutor, TabCompleter {

    private final TelegramLogger plugin;

    public CommandHandler(TelegramLogger plugin) {
        this.plugin = plugin;
    }

    private String prefix() { return plugin.getConfigManager().getPluginPrefix(); }

    private void send(CommandSender sender, String message) {
        sender.sendMessage(MessageUtils.colorize(prefix() + message));
    }

    // ─── Command routing ────────────────────────────────

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("telegramlogger")) return false;

        if (!sender.hasPermission("telegramlogger.admin")) {
            send(sender, "&c&l\u274C You don't have permission!");
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload": doReload(sender); break;
            case "start":  doStart(sender); break;
            case "stop":   doStop(sender); break;
            case "stats":  doStats(sender); break;
            case "debug":  doDebug(sender); break;
            case "status": doStatus(sender); break;
            case "admin":  doAdmin(sender, args); break;
            case "help":   showHelp(sender); break;
            default:
                send(sender, "&c&l\u274C Unknown command. Use &e/tl help");
        }
        return true;
    }

    // ─── Subcommands ────────────────────────────────────

    private void doReload(CommandSender sender) {
        try {
            plugin.performReload();
            send(sender, "&a&l\u26A1 Plugin reloaded successfully!");
        } catch (Exception e) {
            send(sender, "&c&l\u274C Reload failed: &e" + e.getMessage());
        }
    }

    private void doStart(CommandSender sender) {
        if (plugin.isPluginActive()) {
            send(sender, "&c&l\u274C Messaging is already active!");
            return;
        }
        plugin.setPluginActive(true);
        send(sender, "&a&l\u26A1 Messaging started!");
    }

    private void doStop(CommandSender sender) {
        if (!plugin.isPluginActive()) {
            send(sender, "&c&l\u274C Messaging is already inactive!");
            return;
        }
        plugin.setPluginActive(false);
        send(sender, "&c&l\u26A1 Messaging stopped!");
    }

    private void doStats(CommandSender sender) {
        DataManager data = plugin.getDataManager();
        ConfigManager cfg = plugin.getConfigManager();
        String stats = data.getFormattedStats(cfg.isEnableSendCommandExecutes());

        // Send with prefix only on the first line
        String[] lines = stats.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i == 0 && line.trim().isEmpty()) continue;
            sender.sendMessage(MessageUtils.colorize(
                    (i <= 1 ? prefix() : "  ") + line));
        }
    }

    private void doDebug(CommandSender sender) {
        ConfigManager cfg = plugin.getConfigManager();
        cfg.setDebugMode(!cfg.isDebugMode());
        send(sender, "&e&l\u26A1 Debug mode " +
                (cfg.isDebugMode() ? "&a&lenabled" : "&c&ldisabled"));
    }

    private void doStatus(CommandSender sender) {
        ConfigManager cfg = plugin.getConfigManager();

        send(sender, "&6&l=== TelegramLogger Status ===");
        send(sender, "&e\u2022 Plugin: " + toggleLabel(plugin.isPluginActive()));
        send(sender, "&e\u2022 Bot: " + toggleLabel(plugin.isBotActive()));
        send(sender, "&e\u2022 Debug: " + toggleLabel(cfg.isDebugMode()));
        send(sender, "");
        send(sender, "&7Features:");
        send(sender, "&e  Join: " + toggleLabel(cfg.isEnableJoin())
                + "  &eLeave: " + toggleLabel(cfg.isEnableLeave())
                + "  &eChat: " + toggleLabel(cfg.isEnableChat()));
        send(sender, "&e  Advancement: " + toggleLabel(cfg.isEnableAdvancement())
                + "  &eDeath: " + toggleLabel(cfg.isEnableDeath())
                + "  &eWorld: " + toggleLabel(cfg.isEnableWorldSwitch()));
        send(sender, "&e  Filter: " + toggleLabel(cfg.isEnableChatFilter())
                + "  &eSudo: " + toggleLabel(cfg.isEnableSudoCommand())
                + "  &eFirst Join: " + toggleLabel(cfg.isEnableFirstJoin()));
        send(sender, "");
        send(sender, "&7Server: &f" + Bukkit.getVersion());
        send(sender, "&7Players: &f" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
    }

    private String toggleLabel(boolean on) {
        return on ? "&a\u2714" : "&c\u2716";
    }

    private void doAdmin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "&c&lUsage: &e/tl admin <add|remove|list>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "add":   doAdminAdd(sender, args); break;
            case "remove": doAdminRemove(sender, args); break;
            case "list":  doAdminList(sender); break;
            default:
                send(sender, "&c&l\u274C Unknown: use add, remove, or list");
        }
    }

    private void doAdminAdd(CommandSender sender, String[] args) {
        if (args.length < 4) {
            send(sender, "&c&lUsage: &e/tl admin add <telegram_id> <name>");
            return;
        }

        String id = args[2];
        String name = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            send(sender, "&c&l\u274C Invalid Telegram ID (must be a number)");
            return;
        }

        AdminManager admins = plugin.getAdminManager();
        if (admins.isRegistered(id)) {
            send(sender, "&c&l\u274C ID &e" + id + " &cis already registered!");
            return;
        }

        admins.addAdmin(id, name);
        send(sender, "&a&l\uD83D\uDC51 Admin &e" + name + " &aadded! (ID: " + id + ")");
    }

    private void doAdminRemove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            send(sender, "&c&lUsage: &e/tl admin remove <telegram_id>");
            return;
        }

        AdminManager admins = plugin.getAdminManager();
        String id = args[2];
        String name = admins.getName(id);

        if (name == null) {
            send(sender, "&c&l\u274C ID &e" + id + " &cis not registered!");
            return;
        }

        admins.removeAdmin(id);
        send(sender, "&c&l\uD83D\uDC51 Admin &e" + name + " &cremoved!");
    }

    private void doAdminList(CommandSender sender) {
        AdminManager admins = plugin.getAdminManager();
        Set<Map.Entry<String, JsonElement>> list = admins.getAll();

        if (list.isEmpty()) {
            send(sender, "&c&l\u274C No admins registered!");
            return;
        }

        send(sender, "&6&l=== Registered Admins (" + list.size() + ") ===");
        for (Map.Entry<String, JsonElement> entry : list) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            send(sender, "&e" + obj.get("name").getAsString()
                    + " &7(ID: &f" + entry.getKey() + "&7)");
        }
    }

    private void showHelp(CommandSender sender) {
        String[] help = {
            "&6&l=== TelegramLogger v" + TelegramLogger.PLUGIN_VERSION + " ===",
            "&e/tl reload &7- &fReload configuration",
            "&e/tl start &7- &fStart message forwarding",
            "&e/tl stop &7- &fStop message forwarding",
            "&e/tl stats &7- &fView statistics",
            "&e/tl status &7- &fPlugin status",
            "&e/tl debug &7- &fToggle debug mode",
            "&e/tl admin add <id> <name> &7- &fAdd admin",
            "&e/tl admin remove <id> &7- &fRemove admin",
            "&e/tl admin list &7- &fList admins",
            "&e/tl help &7- &fThis help"
        };
        for (String line : help) {
            send(sender, line);
        }
    }

    // ─── Tab completion ─────────────────────────────────

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("telegramlogger.admin")) return Collections.emptyList();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String[] cmds = {"reload", "start", "stop", "stats", "debug", "admin", "status", "help"};
            for (String c : cmds) {
                if (c.startsWith(args[0].toLowerCase())) completions.add(c);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            for (String sub : new String[]{"add", "remove", "list"}) {
                if (sub.startsWith(args[1].toLowerCase())) completions.add(sub);
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin")
                && args[1].equalsIgnoreCase("remove")) {
            plugin.getAdminManager().getAll().forEach(entry -> {
                if (entry.getKey().startsWith(args[2])) completions.add(entry.getKey());
            });
        } else if (args.length == 4 && args[0].equalsIgnoreCase("admin")
                && args[1].equalsIgnoreCase("add")) {
            completions.add("<name>");
        }

        return completions;
    }
}
