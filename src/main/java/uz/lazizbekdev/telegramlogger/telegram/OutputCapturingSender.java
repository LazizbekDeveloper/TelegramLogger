package uz.lazizbekdev.telegramlogger.telegram;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A CommandSender wrapper that captures all output messages.
 * Used by /sudo to collect command output and send it back to Telegram.
 */
public class OutputCapturingSender implements CommandSender {

    private final CommandSender wrapped;
    private final List<String> output;

    public OutputCapturingSender(CommandSender wrapped, List<String> output) {
        this.wrapped = wrapped;
        this.output = output;
    }

    @Override
    public void sendMessage(String message) {
        output.add(message);
        wrapped.sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        Collections.addAll(output, messages);
        wrapped.sendMessage(messages);
    }

    @Override
    public Server getServer() { return wrapped.getServer(); }

    @Override
    public String getName() { return wrapped.getName(); }

    @Override
    public Spigot spigot() { return wrapped.spigot(); }

    // ─── Permissible delegation ─────────────────────────

    @Override
    public boolean isPermissionSet(String name) { return wrapped.isPermissionSet(name); }

    @Override
    public boolean isPermissionSet(Permission perm) { return wrapped.isPermissionSet(perm); }

    @Override
    public boolean hasPermission(String name) { return wrapped.hasPermission(name); }

    @Override
    public boolean hasPermission(Permission perm) { return wrapped.hasPermission(perm); }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return wrapped.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return wrapped.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return wrapped.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return wrapped.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        wrapped.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() { wrapped.recalculatePermissions(); }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return wrapped.getEffectivePermissions();
    }

    @Override
    public boolean isOp() { return wrapped.isOp(); }

    @Override
    public void setOp(boolean value) { wrapped.setOp(value); }

    @Override
    public void sendMessage(UUID sender, String message) {
        output.add(message);
        wrapped.sendMessage(sender, message);
    }

    @Override
    public void sendMessage(UUID sender, String[] messages) {
        Collections.addAll(output, messages);
        wrapped.sendMessage(sender, messages);
    }
}
