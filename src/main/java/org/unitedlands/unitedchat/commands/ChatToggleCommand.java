package org.unitedlands.unitedchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.unitedchat.UnitedChat;
import org.unitedlands.unitedchat.player.ChatFeature;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
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
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                handleReset(player);
            }
        } else if (args.length == 2) {

        } else if (args.length == 3) {
            var feature = args[1];
            if (!chatFeatures.contains(feature)) {
                player.sendMessage(plugin.getChatMessageManager().getMessage("invalid-feature"));
                return false;
            }

            Boolean toggleOn = args[2].equalsIgnoreCase("on");

            // Chat games need to be toggled via perms
            if (feature.equalsIgnoreCase("chatgames")) {
                toggleChatGames(player, toggleOn);
                return true;
            }

            if (toggleOn) {
                plugin.getChatSettingsManager().setKeyValue(player, feature, "on");
            } else {
                plugin.getChatSettingsManager().setKeyValue(player, feature, "off");
            }

            player.sendMessage(plugin.getChatMessageManager().getMessage("toggled-feature",
                    component("feature", text(feature)),
                    component("toggle", text(toggleOn ? "on" : "off"))));
        }

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
