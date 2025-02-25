package org.unitedlands.unitedchat.commands;

import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.unitedchat.UnitedChat;
import org.unitedlands.unitedchat.managers.ChatMessageManager;
import org.unitedlands.unitedchat.managers.ChatSettingsManager;

public class GradientCommand implements CommandExecutor {

    private final UnitedChat plugin;
    private final ChatMessageManager messageManager;
    private final ChatSettingsManager settingsManager;

    public GradientCommand(UnitedChat plugin, ChatMessageManager messageManager, ChatSettingsManager settingsManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
            String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }
        var player = (Player) sender;

        if (!player.hasPermission("united.chat.gradient")) {
            player.sendMessage(messageManager.getMessage("no-perm"));
            return false;
        }

        switch (args[0]) {
            case "info" -> handleGradientInfo(player);
            case "toggle" -> handleGradientToggle(player, args);
            case "set" -> handleSetGradient(player, args);
        }

        return false;
    }

    private void handleGradientInfo(Player player) {
        String gradient = settingsManager.getGradient(player).replace(":", "\n- ");
        TextReplacementConfig gradientPlaceholder = TextReplacementConfig.builder().match("<gradient>")
                .replacement(gradient).build();
        player.sendMessage(messageManager.getMessage("current-gradient").replaceText(gradientPlaceholder));
    }

    private void handleGradientToggle(Player player, String[] args) {
        if (args.length != 2)
            return;

        switch (args[1]) {
            case "on" -> toggleGradient(player, true);
            case "off" -> toggleGradient(player, false);
        }
    }

    private void handleSetGradient(Player player, String[] args) {

        if (args.length != 2) {
            player.sendMessage(messageManager.getMessage("gradient-set-command"));
            return;
        }

        if (getPresetSection().contains(args[1])) {
            setGradientPreset(player, args[1]);
            return;
        }

        if (args[1].contains("#")) {
            if (player.hasPermission("united.chat.gradient.all")) {
                settingsManager.setGradient(player, args[1]);
                player.sendMessage(messageManager.getMessage("gradient-changed"));
            } else {

            }
        } else {
            player.sendMessage(messageManager.getMessage("gradient-set-command"));
        }
    }

    private void setGradientPreset(Player player, String presetName) {
        if (getPresetSection().getString(presetName) == null) {
            player.sendMessage(messageManager.getMessage("gradient-unknown-preset"));
            return;
        }

        if (player.hasPermission("united.chat.gradient." + presetName)) {
            String preset = getPresetSection().getString(presetName);
            settingsManager.setGradient(player, preset);
            player.sendMessage(messageManager.getMessage("gradient-changed"));
        } else {
            player.sendMessage(messageManager.getMessage("no-perm"));
        }
    }

    private void toggleGradient(Player player, Boolean enable) {
        if (enable) {
            if (!settingsManager.isGradientEnabled(player)) {
                settingsManager.setGradientEnabled(player, true);
                player.sendMessage(messageManager.getMessage("gradient-on"));
            } else {
                player.sendMessage(messageManager.getMessage("gradient-is-on"));
            }
        } else {
            if (settingsManager.isGradientEnabled(player)) {
                settingsManager.setGradientEnabled(player, false);
                player.sendMessage(messageManager.getMessage("gradient-off"));
            } else {
                player.sendMessage(messageManager.getMessage("gradient-is-off"));
            }
        }

    }

    @Nullable
    private ConfigurationSection getPresetSection() {
        return plugin.getConfig().getConfigurationSection("Presets");
    }

}
