package org.unitedlands.unitedchat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.unitedchat.commands.ChatToggleCommand;
import org.unitedlands.unitedchat.commands.GradientCommand;
import org.unitedlands.unitedchat.hooks.Placeholders;
import org.unitedlands.unitedchat.managers.BroadcastManager;
import org.unitedlands.unitedchat.managers.ChatMessageManager;
import org.unitedlands.unitedchat.managers.ChatSettingsManager;
import org.unitedlands.unitedchat.player.PlayerListener;
import org.unitedlands.unitedchat.tabcompleters.ChatToggleCommandCompleter;
import org.unitedlands.unitedchat.tabcompleters.GradientCommandTabCompleter;


public class UnitedChat extends JavaPlugin {

    private ChatMessageManager chatMessageManager;
    private ChatSettingsManager chatSettingsManager;

    @Override
    public void onEnable() {

        chatMessageManager = new ChatMessageManager(this);
        chatSettingsManager = new ChatSettingsManager(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("[Exception] PlaceholderAPI is required!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        new Placeholders(this, chatSettingsManager).register();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        getCommand("gradient").setExecutor(new GradientCommand(this, chatMessageManager, chatSettingsManager));
        getCommand("gradient").setTabCompleter(new GradientCommandTabCompleter(this));

        var chatToggle = new ChatToggleCommand(this);
        getCommand("unitedchat").setExecutor(chatToggle);
        getCommand("unitedchat").setTabCompleter(new ChatToggleCommandCompleter(this));

        saveDefaultConfig();

        int intervalTicks = getConfig().getInt("broadcaster.interval", 10) * 60 * 20;
        new BroadcastManager(this).runTaskTimer(this, intervalTicks, intervalTicks);

        getLogger().info("UnitedChat initialized.");
    }

    public ChatMessageManager getChatMessageManager() {
        return chatMessageManager;
    }

    public ChatSettingsManager getChatSettingsManager() {
        return chatSettingsManager;
    }

}