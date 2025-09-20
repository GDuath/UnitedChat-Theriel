package org.unitedlands.unitedchat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.unitedchat.UnitedChat;
import org.unitedlands.unitedchat.managers.BroadcastManager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

import java.util.List;

public class ChatToggleCommand implements CommandExecutor {

    private final UnitedChat plugin;
    private final List<String> chatFeatures;

    public ChatToggleCommand(UnitedChat plugin) {
        this.plugin = plugin;
        chatFeatures = plugin.getConfig().getStringList("features");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String @NotNull [] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("united.chat.admin")) {
                sender.sendMessage(plugin.getChatMessageManager().getMessage("no-perm"));
                return false;
            }

            plugin.reloadConfig();

            // Reapply runtime configuration
            Bukkit.getScheduler().cancelTasks(plugin);
            int intervalTicks = plugin.getConfig().getInt("broadcaster.interval", 10) * 60 * 20;
            new BroadcastManager(plugin).runTaskTimer(plugin, intervalTicks, intervalTicks);

            sender.sendMessage(plugin.getChatMessageManager().getMessage("reloaded"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.getChatMessageManager().getMessage("chat-toggle-command"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
            handleReset(player);
            return true;
        }

        if (args.length == 3) {
            var feature = args[1];
            if (!chatFeatures.contains(feature)) {
                player.sendMessage(plugin.getChatMessageManager().getMessage("invalid-feature"));
                return false;
            }

            boolean toggleOn = args[2].equalsIgnoreCase("on");

            if (feature.equalsIgnoreCase("chatgames")) {
                toggleChatGames(player, toggleOn);
                return true;
            }

            plugin.getChatSettingsManager().setKeyValue(player, feature, toggleOn ? "on" : "off");

            player.sendMessage(plugin.getChatMessageManager().getMessage("toggled-feature",
                    component("feature", text(feature)),
                    component("toggle", text(toggleOn ? "on" : "off"))));
            return true;
        }

        player.sendMessage(plugin.getChatMessageManager().getMessage("chat-toggle-command"));
        return true;
    }


    private void toggleChatGames(Player player, Boolean toggleOn) {

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        user.data().remove(PermissionNode.builder("cb.default").build());
        user.data().add(PermissionNode.builder("cb.default").value(toggleOn).build());

        luckPerms.getUserManager().saveUser(user);

        player.sendMessage(plugin.getChatMessageManager().getMessage("toggled-feature",
                component("feature", text("Chat games")),
                component("toggle", text(toggleOn ? "on" : "off"))));

    }

    private void handleReset(Player player) {
        plugin.getChatSettingsManager().removeKey(player, "gradient");
        plugin.getChatSettingsManager().removeKey(player, "gradient-enabled");

        plugin.getChatSettingsManager().removeKey(player, "chatrank");
        plugin.getChatSettingsManager().removeKey(player, "chatprefix");
        plugin.getChatSettingsManager().removeKey(player, "tabprefix");
        plugin.getChatSettingsManager().removeKey(player, "chatnation");
        plugin.getChatSettingsManager().removeKey(player, "tabnation");
        plugin.getChatSettingsManager().removeKey(player, "chattown");
        plugin.getChatSettingsManager().removeKey(player, "tabtown");
        plugin.getChatSettingsManager().removeKey(player, "chatgames");

        plugin.getChatSettingsManager().setKeyValue(player, "chatrank", "on");
        plugin.getChatSettingsManager().setKeyValue(player, "chatprefix", "on");
        plugin.getChatSettingsManager().setKeyValue(player, "chatgames", "on");

        // Legacy settings, remove those to keep everything clean
        plugin.getChatSettingsManager().removeKey(player, "prefixes");
        plugin.getChatSettingsManager().removeKey(player, "ranks");
        plugin.getChatSettingsManager().removeKey(player, "broadcasts");
        plugin.getChatSettingsManager().removeKey(player, "gradients");
        plugin.getChatSettingsManager().removeKey(player, "games");

        player.sendMessage(plugin.getChatMessageManager().getMessage("reset"));
    }

}
