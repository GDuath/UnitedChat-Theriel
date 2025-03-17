package org.unitedlands.unitedchat.tabcompleters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.unitedchat.UnitedChat;

public class GradientCommandTabCompleter implements TabCompleter {

    private final UnitedChat plugin;

    private final List<String> gradientCommands = Arrays.asList("toggle", "set");
    private final List<String> gradientToggleCommands = Arrays.asList("off", "on");

    public GradientCommandTabCompleter(UnitedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> options = null;
        String input = args[args.length - 1];

        if (args.length == 0)
            return null;

        switch (args.length) {
            case 1:
                options = gradientCommands;
                break;
            case 2:
                if (args[0].equalsIgnoreCase("toggle")) {
                    options = gradientToggleCommands;
                } else if (args[0].equalsIgnoreCase("set")) {
                    options = getPresetNames();
                }
        }

        List<String> completions = null;
        if (options != null) {
            completions = options.stream().filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                    .collect(Collectors.toList());
            Collections.sort(completions);
        }
        return completions;
    }

    private List<String> getPresetNames() {
        var presetSection = plugin.getConfig().getConfigurationSection("Presets");
        return new ArrayList<String>(presetSection.getKeys(false));
    }

}
