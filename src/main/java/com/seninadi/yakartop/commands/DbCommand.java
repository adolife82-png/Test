package com.seninadi.yakartop.commands;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.managers.ArenaManager;
import com.seninadi.yakartop.managers.GameManager;
import com.seninadi.yakartop.models.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DbCommand implements CommandExecutor {

    private final YakarTopPlugin plugin;
    private final ArenaManager arenaManager;
    private final GameManager gameManager;

    public DbCommand(YakarTopPlugin plugin, ArenaManager arenaManager, GameManager gameManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Bu komut sadece oyuncular tarafindan kullanilabilir.");
            return true;
        }

        // /db (ana menu)
        if (args.length == 0) {
            player.sendMessage("§6--- Yakar Top ---");
            player.sendMessage("§eKayitli arenalar:");
            for (Arena arena : arenaManager.getArenas().values()) {
                player.sendMessage("§a- " + arena.getName() + " §7(" + arena.getMaxPlayers() + " kisilik)");
            }
            player.sendMessage("§7Katilmak icin: /db <arenaismi>");
            return true;
        }

        // /db <arenaismi> -> Lobiye isinla
        if (args.length == 1) {
            String arenaName = args[0];
            Arena arena = arenaManager.getArena(arenaName);
            if (arena == null) {
                player.sendMessage("§cArena bulunamadi: " + arenaName);
                return true;
            }
            gameManager.joinLobby(player, arena);
            return true;
        }

        // /db admin ...
        if (args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("yakartop.admin")) {
                player.sendMessage("§cBu komutu kullanma yetkiniz yok.");
                return true;
            }

            // /db admin alan1 <sayi> <arenaismi>
            if (args.length >= 4 && args[1].equalsIgnoreCase("alan1")) {
                int maxPlayers;
                try {
                    maxPlayers = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cGecersiz sayi: " + args[2]);
                    return true;
                }
                String arenaName = args[3];
                arenaManager.createOrUpdateArena(arenaName, maxPlayers, player.getLocation(), null);
                player.sendMessage("§aArena '" + arenaName + "' icin Alan1 kaydedildi. (" + maxPlayers + " kisilik)");
                return true;
            }

            // /db admin alan2 <sayi> <arenaismi>
            if (args.length >= 4 && args[1].equalsIgnoreCase("alan2")) {
                int maxPlayers;
                try {
                    maxPlayers = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cGecersiz sayi: " + args[2]);
                    return true;
                }
                String arenaName = args[3];
                arenaManager.createOrUpdateArena(arenaName, maxPlayers, null, player.getLocation());
                player.sendMessage("§aArena '" + arenaName + "' icin Alan2 kaydedildi. (" + maxPlayers + " kisilik)");
                return true;
            }

            // /db admin <arenaismi> lobby (lobi konumu ayarlama)
            if (args.length == 3 && args[2].equalsIgnoreCase("lobby")) {
                String arenaName = args[1];
                arenaManager.setLobbyLocation(arenaName, player.getLocation());
                player.sendMessage("§aArena '" + arenaName + "' icin lobi konumu ayarlandi.");
                return true;
            }
        }

        player.sendMessage("§cGecersiz komut kullanimi.");
        return true;
    }
}
