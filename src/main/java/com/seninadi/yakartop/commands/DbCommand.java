package com.seninadi.yakartop.commands;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.gui.ArenaMenu;
import com.seninadi.yakartop.managers.ArenaManager;
import com.seninadi.yakartop.managers.GameManager;
import com.seninadi.yakartop.models.Arena;
import com.seninadi.yakartop.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DbCommand implements CommandExecutor {

    private final YakarTopPlugin plugin;
    private final ArenaManager arenaManager;
    private final GameManager gameManager;
    private final ArenaMenu arenaMenu;

    public DbCommand(YakarTopPlugin plugin, ArenaManager arenaManager, GameManager gameManager, ArenaMenu arenaMenu) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.gameManager = gameManager;
        this.arenaMenu = arenaMenu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Bu komut sadece oyuncular tarafından kullanılabilir.");
            return true;
        }

        // /db -> Modern GUI ac
        if (args.length == 0) {
            arenaMenu.open(player);
            return true;
        }

        // /db <arenaismi> -> Lobiye isinla
        if (args.length == 1) {
            Arena arena = arenaManager.getArena(args[0]);
            if (arena == null) {
                player.sendMessage(Messages.error("Arena bulunamadı: <white>" + args[0]));
                return true;
            }
            gameManager.joinLobby(player, arena);
            return true;
        }

        // /db admin ...
        if (args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("yakartop.admin")) {
                player.sendMessage(Messages.error("Bu komutu kullanma yetkiniz yok."));
                return true;
            }

            // /db admin alan1 <sayi> <arenaismi>
            if (args.length >= 4 && args[1].equalsIgnoreCase("alan1")) {
                int maxPlayers = parseIntOrDefault(player, args[2], -1);
                if (maxPlayers <= 0) return true;
                String arenaName = args[3];
                arenaManager.createOrUpdateArena(arenaName, maxPlayers, player.getLocation(), null);
                player.sendMessage(Messages.success("Arena <white>" + arenaName + "</white> için <yellow>Alan1</yellow> kaydedildi. (<white>" + maxPlayers + "</white> kişilik)"));
                return true;
            }

            // /db admin alan2 <sayi> <arenaismi>
            if (args.length >= 4 && args[1].equalsIgnoreCase("alan2")) {
                int maxPlayers = parseIntOrDefault(player, args[2], -1);
                if (maxPlayers <= 0) return true;
                String arenaName = args[3];
                arenaManager.createOrUpdateArena(arenaName, maxPlayers, null, player.getLocation());
                player.sendMessage(Messages.success("Arena <white>" + arenaName + "</white> için <yellow>Alan2</yellow> kaydedildi. (<white>" + maxPlayers + "</white> kişilik)"));
                return true;
            }

            // /db admin start <arenaismi>
            if (args.length == 3 && args[1].equalsIgnoreCase("start")) {
                Arena arena = arenaManager.getArena(args[2]);
                if (arena == null) {
                    player.sendMessage(Messages.error("Arena bulunamadı: <white>" + args[2]));
                    return true;
                }
                if (!arena.isReady()) {
                    player.sendMessage(Messages.error("Arena hazır değil! Alan1, Alan2 ve Lobi ayarlanmalı."));
                    return true;
                }
                if (arena.isActive()) {
                    player.sendMessage(Messages.error("Bu arenada zaten bir oyun var."));
                    return true;
                }
                if (gameManager.getLobbyPlayers(arena.getName()).size() < 2) {
                    player.sendMessage(Messages.error("Başlatmak için en az 2 oyuncu gerekli."));
                    return true;
                }
                gameManager.startGame(arena);
                player.sendMessage(Messages.success("Oyun başlatıldı: <white>" + arena.getName()));
                return true;
            }

            // /db admin stop <arenaismi>
            if (args.length == 3 && args[1].equalsIgnoreCase("stop")) {
                Arena arena = arenaManager.getArena(args[2]);
                if (arena == null) {
                    player.sendMessage(Messages.error("Arena bulunamadı: <white>" + args[2]));
                    return true;
                }
                if (!arena.isActive()) {
                    player.sendMessage(Messages.error("Bu arenada aktif bir oyun yok."));
                    return true;
                }
                gameManager.stopGame(arena);
                player.sendMessage(Messages.success("Oyun durduruldu: <white>" + arena.getName()));
                return true;
            }

            // /db admin <arenaismi> lobby
            if (args.length == 3 && args[2].equalsIgnoreCase("lobby")) {
                Arena arena = arenaManager.getArena(args[1]);
                if (arena == null) {
                    player.sendMessage(Messages.error("Arena bulunamadı: <white>" + args[1]));
                    return true;
                }
                arenaManager.setLobbyLocation(arena.getName(), player.getLocation());
                player.sendMessage(Messages.success("Arena <white>" + arena.getName() + "</white> için lobi konumu ayarlandı."));
                return true;
            }
        }

        player.sendMessage(Messages.error("Geçersiz komut kullanımı. <yellow>/db</yellow> yazarak menüyü açabilirsin."));
        return true;
    }

    private int parseIntOrDefault(Player player, String input, int def) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.error("Geçersiz sayı: <white>" + input));
            return def;
        }
    }
}
