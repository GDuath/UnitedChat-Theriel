package org.unitedlands.unitedchat.tabcompleters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.unitedchat.UnitedChat;

public class ChatToggleCommandCompleter implements TabCompleter {

    private final UnitedChat plugin;

    private final List<String> chatCommands = Arrays.asList("reset", "toggle");
    private final List<String> chatFeatures;
    private final List<String> toggleCommands = Arrays.asList("off", "on");

    public ChatToggleCommandCompleter(UnitedChat plugin) {
        this.plugin = plugin;
        chatFeatures = plugin.getConfig().getStringList("features");
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> options = null;
        String input = args[args.length - 1];

        if (args.length == 0)
            return null;

        switch (args.length) {
            case 1:
                options = chatCommands;
                break;
            case 2:
                if (!args[0].equalsIgnoreCase("reset"))
                    options = chatFeatures;
                break;
            case 3:
                options = toggleCommands;
                break;
        }

        List<String> completions = null;
        if (options != null) {
            completions = options.stream().filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                    .collect(Collectors.toList());
            Collections.sort(completions);
        }
        return completions;
    }

}
