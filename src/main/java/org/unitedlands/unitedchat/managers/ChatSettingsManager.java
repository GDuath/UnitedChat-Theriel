package org.unitedlands.unitedchat.managers;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.unitedlands.unitedchat.UnitedChat;
import org.unitedlands.unitedchat.player.ChatFeature;

public class ChatSettingsManager {

    private final UnitedChat plugin;

    public ChatSettingsManager(UnitedChat plugin) {
        this.plugin = plugin;
    }

    public String getGradient(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.has(getKey("gradient"))) {
            return null;
        }
        return pdc.get(getKey("gradient"), PersistentDataType.STRING);
    }

    public void setGradient(Player player, String gradient) {
        player.getPersistentDataContainer().set(getKey("gradient"), PersistentDataType.STRING, gradient);
    }

    public void setGradientEnabled(Player player, boolean toggle) {
        player.getPersistentDataContainer().set(getKey("gradient-enabled"), PersistentDataType.BOOLEAN, toggle);
    }

    public boolean isGradientEnabled(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (getGradient(player) == null) {
            return false;
        }
        if (!pdc.has(getKey("gradient-enabled"))) {
            return false;
        }
        return pdc.get(getKey("gradient-enabled"), PersistentDataType.BOOLEAN);
    }

    public void toggleChatFeature(Player player, ChatFeature feature, boolean toggle) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, feature.toString());
        if (toggle) {
            pdc.set(key, PersistentDataType.INTEGER, 1);
        } else {
            pdc.set(key, PersistentDataType.INTEGER, 0);
        }
    }

    public void removeKey(Player player, String name) {
        NamespacedKey key = getKey(name);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (pdc.has(key))
            pdc.remove(key);
    }

    private NamespacedKey getKey(String name) {
        return new NamespacedKey(plugin, name);
    }

}
