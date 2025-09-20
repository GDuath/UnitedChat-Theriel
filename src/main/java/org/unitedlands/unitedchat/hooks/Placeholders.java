package org.unitedlands.unitedchat.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.unitedchat.UnitedChat;
import org.unitedlands.unitedchat.managers.ChatSettingsManager;

public class Placeholders extends PlaceholderExpansion implements Relational {
    @Override
    public @NotNull String getIdentifier() {
        return "unitedchat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "maroon28";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    private final UnitedChat plugin;
    @SuppressWarnings("unused")
    private final ChatSettingsManager chatSettingsManager;

    public Placeholders(UnitedChat plugin, ChatSettingsManager chatSettingsManager) {
        this.plugin = plugin;
        this.chatSettingsManager = chatSettingsManager;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        String[] args = PlaceholderAPI.setBracketPlaceholders(player, params).split("_");
        String feature = args[0];

        if (args.length < 2)
            return "";
        String innerPlaceholder = args[1];
        if (plugin.getChatSettingsManager().getKeyValue(player.getPlayer(), feature).equalsIgnoreCase("on")) {
            return innerPlaceholder.trim() + " ";
        } else
            return ""; // disabled, return empty.
    }

    @Override
    public String onPlaceholderRequest(Player one, Player two, String params) {
        // prefix_{vault_prefix}
        String[] args = PlaceholderAPI.setBracketPlaceholders(two, params).split("_");
        String feature = args[0];
        
        if (args.length < 2)
            return "";
        String innerPlaceholder = args[1];
        if (plugin.getChatSettingsManager().getKeyValue(one.getPlayer(), feature).equalsIgnoreCase("on")) {
            return innerPlaceholder.trim() + " ";
        } else
            return ""; // disabled, return empty.
    }

}
