package org.unitedlands.unitedchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.unitedchat.UnitedChat;
import org.unitedlands.unitedchat.player.ChatFeature;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

public class ChatToggleCommand implements CommandExecutor {

    private final UnitedChat plugin;

    public ChatToggleCommand(UnitedChat plugin) {
        this.plugin = plugin;
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
            } else {
                player.sendMessage(plugin.getChatMessageManager().getMessage("chat-toggle-command"));
                return true;
            }
        } else {
            ChatFeature feature;
            try {
                feature = ChatFeature.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage(plugin.getChatMessageManager().getMessage("invalid-feature"));
                return true;
            }
            boolean toggle = args[1].equalsIgnoreCase("on");

            plugin.getChatSettingsManager().toggleChatFeature(player, feature, toggle);
            player.sendMessage(plugin.getChatMessageManager().getMessage("toggled-feature",
                    component("feature", text(feature.name().toLowerCase())),
                    component("toggle", text(args[1]))));
        }

        return true;
    }

    private void handleReset(Player player) {
        plugin.getChatSettingsManager().removeKey(player, "gradient");
        plugin.getChatSettingsManager().removeKey(player, "gradient-enabled");
        plugin.getChatSettingsManager().removeKey(player, "prefixes");
        plugin.getChatSettingsManager().removeKey(player, "ranks");
        plugin.getChatSettingsManager().removeKey(player, "broadcasts");
        plugin.getChatSettingsManager().removeKey(player, "gradients");
        plugin.getChatSettingsManager().removeKey(player, "games");
        player.sendMessage(plugin.getChatMessageManager().getMessage("reset"));
    }

}
