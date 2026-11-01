package com.seninadi.yakartop.commands;

import com.seninadi.yakartop.managers.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbTabCompleter implements TabCompleter {
    private final ArenaManager arenaManager;

    public DbTabCompleter(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("yakartop.admin")) completions.add("admin");
            completions.addAll(arenaManager.getArenaNames());
            return filter(completions, args[0]);
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("admin")) {
            if (!sender.hasPermission("yakartop.admin")) return completions;

            if (args.length == 2) {
                completions.addAll(Arrays.asList("alan1", "alan2", "start", "stop", "lobby"));
                return filter(completions, args[1]);
            }

            if (args.length == 3 && (args[1].equalsIgnoreCase("alan1") || args[1].equalsIgnoreCase("alan2"))) {
                completions.addAll(Arrays.asList("8", "16", "24", "32"));
                return filter(completions, args[2]);
            }

            if (args.length == 4 && (args[1].equalsIgnoreCase("alan1") || args[1].equalsIgnoreCase("alan2"))) {
                return filter(arenaManager.getArenaNames(), args[3]);
            }

            if (args.length == 3 && (args[1].equalsIgnoreCase("start") || args[1].equalsIgnoreCase("stop")
                    || args[1].equalsIgnoreCase("lobby"))) {
                return filter(arenaManager.getArenaNames(), args[2]);
            }
        }

        return completions;
    }

    private List<String> filter(List<String> list, String prefix) {
        String lower = prefix.toLowerCase();
        List<String> result = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(lower)) result.add(s);
        }
        return result;
    }
}
