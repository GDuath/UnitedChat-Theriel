package org.unitedlands.unitedchat.managers;

import org.unitedlands.unitedchat.UnitedChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class ChatMessageManager {

    private final UnitedChat plugin;

    public ChatMessageManager(UnitedChat plugin) {
        this.plugin = plugin;
    }

    public Component getMessage(String name) {
        String prefix = plugin.getConfig().getString("messages.prefix");
        String message = plugin.getConfig().getString("messages." + name);
        return MiniMessage.miniMessage().deserialize(prefix + message);
    }

    public Component getMessage(String message, TagResolver.Single... resolvers) {
        String prefix = plugin.getConfig().getString("messages.prefix");
        String configuredMessage = plugin.getConfig().getString("messages." + message);
        if (configuredMessage == null) {
            return MiniMessage.miniMessage()
                    .deserialize("<red>Message <yellow>" + message + "<red> could not be found in the config file!");
        }
        return MiniMessage.miniMessage().deserialize(prefix + configuredMessage, resolvers);
    }

}
