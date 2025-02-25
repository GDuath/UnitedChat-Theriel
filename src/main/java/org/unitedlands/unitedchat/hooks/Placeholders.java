package org.unitedlands.unitedchat.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.unitedchat.UnitedChat;
import org.unitedlands.unitedchat.player.ChatFeature;

public class Placeholders extends PlaceholderExpansion {
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

    public Placeholders(UnitedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        String[] args = PlaceholderAPI.setBracketPlaceholders(player, params).split("_");
        ChatFeature feature = ChatFeature.valueOf(args[0].toUpperCase());
        if (args.length < 2)
            return "";
        String innerPlaceholder = args[1];

        if (isChatFeatureEnabled(feature, player)) {
            if (feature == ChatFeature.PREFIXES)
                return innerPlaceholder; // prefixes are built in with a space
            return innerPlaceholder + " "; // other stuff isn't
        } else
            return ""; // disabled, return empty.
    }

    public boolean isChatFeatureEnabled(ChatFeature feature, OfflinePlayer player) {
        if (player.getPlayer() == null)
            return false;
        PersistentDataContainer pdc = player.getPlayer().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, feature.toString());
        // features are on by default, which means it wasn't ever toggled before if
        // there is no key.
        if (!pdc.has(key))
            return true;
        return pdc.get(key, PersistentDataType.INTEGER) == 1;
    }

}
