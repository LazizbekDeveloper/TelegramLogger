package uz.lazizbekdev.telegramlogger.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import uz.lazizbekdev.telegramlogger.TelegramLogger;
import uz.lazizbekdev.telegramlogger.config.ConfigManager;
import uz.lazizbekdev.telegramlogger.managers.AdminManager;
import uz.lazizbekdev.telegramlogger.managers.DataManager;
import uz.lazizbekdev.telegramlogger.utils.MessageUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final TelegramLogger plugin;

    public CommandHandler(TelegramLogger plugin) {
        this.plugin = plugin;
    }

    private String prefix() { return plugin.getConfigManager().getPluginPrefix(); }

    private void send(CommandSender sender, String message) {
        sender.sendMessage(MessageUtils.colorize(prefix() + message));
    }

    private void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(MessageUtils.colorize(message));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("telegramlogger")) return false;

        if (!sender.hasPermission("telegramlogger.admin")) {
            send(sender, "&cYou don't have permission!");
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
                send(sender, "&cUnknown command. Use &e/tl help");
        }
        return true;
    }

    private void doReload(CommandSender sender) {
        try {
            plugin.performReload();
            send(sender, "&aPlugin reloaded successfully!");
        } catch (Exception e) {
            send(sender, "&cReload failed: &e" + e.getMessage());
        }
    }

    private void doStart(CommandSender sender) {
        if (plugin.isPluginActive()) {
            send(sender, "&cMessaging is already active!");
            return;
        }
        plugin.setPluginActive(true);
        send(sender, "&aMessaging started!");
    }

    private void doStop(CommandSender sender) {
        if (!plugin.isPluginActive()) {
            send(sender, "&cMessaging is already inactive!");
            return;
        }
        plugin.setPluginActive(false);
        send(sender, "&cMessaging stopped!");
    }

    private void doStats(CommandSender sender) {
        DataManager data = plugin.getDataManager();
        ConfigManager cfg = plugin.getConfigManager();
        String stats = data.getFormattedStats(cfg.isEnableSendCommandExecutes());

        String[] lines = stats.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i == 0 && line.trim().isEmpty()) continue;
            sender.sendMessage(MessageUtils.colorize(
                    (i <= 1 ? prefix() : "    ") + line));
        }
    }

    private void doDebug(CommandSender sender) {
        ConfigManager cfg = plugin.getConfigManager();
        cfg.setDebugMode(!cfg.isDebugMode());
        send(sender, "&eDebug mode " +
                (cfg.isDebugMode() ? "&aenabled" : "&cdisabled"));
    }

    private void doStatus(CommandSender sender) {
        ConfigManager cfg = plugin.getConfigManager();

        send(sender, "&6=== TelegramLogger Status ===");
        sendRaw(sender, "");
        sendRaw(sender, MessageUtils.colorize("  &7Plugin: " + toggleLabel(plugin.isPluginActive())
                + "   &7Bot: " + toggleLabel(plugin.isBotActive())
                + "   &7Debug: " + toggleLabel(cfg.isDebugMode())));
        sendRaw(sender, "");
        sendRaw(sender, MessageUtils.colorize("  &6Features:"));
        sendRaw(sender, MessageUtils.colorize("  &7Join " + toggleLabel(cfg.isEnableJoin())
                + "  &7Leave " + toggleLabel(cfg.isEnableLeave())
                + "  &7Chat " + toggleLabel(cfg.isEnableChat())
                + "  &7Death " + toggleLabel(cfg.isEnableDeath())));
        sendRaw(sender, MessageUtils.colorize("  &7Advancement " + toggleLabel(cfg.isEnableAdvancement())
                + "  &7World " + toggleLabel(cfg.isEnableWorldSwitch())
                + "  &7Filter " + toggleLabel(cfg.isEnableChatFilter())));
        sendRaw(sender, MessageUtils.colorize("  &7Sudo " + toggleLabel(cfg.isEnableSudoCommand())
                + "  &7FirstJoin " + toggleLabel(cfg.isEnableFirstJoin())
                + "  &7AntiFlood " + toggleLabel(cfg.isAntiFloodEnabled())));
        sendRaw(sender, "");
        sendRaw(sender, MessageUtils.colorize("  &7Server: &f" + Bukkit.getVersion()));
        sendRaw(sender, MessageUtils.colorize("  &7Players: &f" + Bukkit.getOnlinePlayers().size()
                + "/" + Bukkit.getMaxPlayers()));
    }

    private String toggleLabel(boolean on) {
        return on ? "&a\u2714" : "&c\u2716";
    }

    private void doAdmin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "&cUsage: &e/tl admin <add|remove|list>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "add":   doAdminAdd(sender, args); break;
            case "remove": doAdminRemove(sender, args); break;
            case "list":  doAdminList(sender); break;
            default:
                send(sender, "&cUnknown: use add, remove, or list");
        }
    }

    private void doAdminAdd(CommandSender sender, String[] args) {
        if (args.length < 4) {
            send(sender, "&cUsage: &e/tl admin add <telegram_id> <name>");
            return;
        }

        String id = args[2];
        String name = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            send(sender, "&cInvalid Telegram ID (must be a number)");
            return;
        }

        AdminManager admins = plugin.getAdminManager();
        if (admins.isRegistered(id)) {
            send(sender, "&cID &e" + id + " &cis already registered!");
            return;
        }

        admins.addAdmin(id, name);
        send(sender, "&aAdmin &e" + name + " &aadded! (ID: " + id + ")");
    }

    private void doAdminRemove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            send(sender, "&cUsage: &e/tl admin remove <telegram_id>");
            return;
        }

        AdminManager admins = plugin.getAdminManager();
        String id = args[2];
        String name = admins.getName(id);

        if (name == null) {
            send(sender, "&cID &e" + id + " &cis not registered!");
            return;
        }

        admins.removeAdmin(id);
        send(sender, "&cAdmin &e" + name + " &cremoved!");
    }

    private void doAdminList(CommandSender sender) {
        AdminManager admins = plugin.getAdminManager();
        Set<Map.Entry<String, JsonElement>> list = admins.getAll();

        if (list.isEmpty()) {
            send(sender, "&cNo admins registered!");
            return;
        }

        send(sender, "&6Registered Admins (" + list.size() + "):");
        for (Map.Entry<String, JsonElement> entry : list) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            sendRaw(sender, MessageUtils.colorize("  &e" + obj.get("name").getAsString()
                    + " &7(ID: &f" + entry.getKey() + "&7)"));
        }
    }

    private void showHelp(CommandSender sender) {
        send(sender, "&6=== TelegramLogger v" + TelegramLogger.PLUGIN_VERSION + " ===");
        sendRaw(sender, "");
        String[] lines = {
            "  &e/tl reload &8- &7Reload configuration",
            "  &e/tl start &8- &7Start message forwarding",
            "  &e/tl stop &8- &7Stop message forwarding",
            "  &e/tl stats &8- &7View statistics",
            "  &e/tl status &8- &7Plugin status",
            "  &e/tl debug &8- &7Toggle debug mode",
            "  &e/tl admin add <id> <name> &8- &7Add admin",
            "  &e/tl admin remove <id> &8- &7Remove admin",
            "  &e/tl admin list &8- &7List admins",
            "  &e/tl help &8- &7This help"
        };
        for (String line : lines) {
            sendRaw(sender, MessageUtils.colorize(line));
        }
    }

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
