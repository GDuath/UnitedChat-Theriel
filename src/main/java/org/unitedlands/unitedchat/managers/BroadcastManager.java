package org.unitedlands.unitedchat.managers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.unitedchat.UnitedChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BroadcastManager extends BukkitRunnable {


    private final UnitedChat plugin;
    private final List<List<String>> broadcasts;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Random random = new Random();

    public BroadcastManager(UnitedChat plugin) {
        this.plugin = plugin;
        this.broadcasts = loadBroadcasts();
    }

    private List<List<String>> loadBroadcasts() {
        List<List<String>> messages = new ArrayList<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("broadcaster.messages");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                List<String> lines = section.getStringList(key);
                if (!lines.isEmpty()) {
                    messages.add(lines);
                }
            }
        }
        return messages;
    }

    @Override
    public void run() {
        if (broadcasts.isEmpty()) return;

        List<String> broadcast = broadcasts.get(random.nextInt(broadcasts.size()));
        for (String line : broadcast) {
            Bukkit.getOnlinePlayers().forEach(player ->
                    player.sendMessage(miniMessage.deserialize(line))
            );
        }
    }
}
