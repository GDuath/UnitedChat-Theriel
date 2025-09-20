package org.unitedlands.unitedchat.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.unitedchat.UnitedChat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChatToggleCommandCompleter implements TabCompleter {

    private final List<String> baseCommands = new ArrayList<>();
    private final List<String> chatFeatures;
    private final List<String> toggleOptions = List.of("on", "off");

    public ChatToggleCommandCompleter(UnitedChat plugin) {
        this.chatFeatures = plugin.getConfig().getStringList("features");

        baseCommands.add("reset");
        baseCommands.add("toggle");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> options = null;
        String input = args[args.length - 1];

        switch (args.length) {
            case 1 -> {
                options = new ArrayList<>(baseCommands);
                if (sender.hasPermission("united.chat.admin"))
                    options.add("reload");
            }
            case 2 -> {
                if (!args[0].equalsIgnoreCase("reset") && !args[0].equalsIgnoreCase("reload")) {
                    options = chatFeatures;
                }
            }
            case 3 -> options = toggleOptions;
        }

        if (options != null) {
            return options.stream()
                    .filter(opt -> opt.toLowerCase().startsWith(input.toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}