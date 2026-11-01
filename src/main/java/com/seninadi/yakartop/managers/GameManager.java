package com.seninadi.yakartop.managers;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.models.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GameManager {
    private final YakarTopPlugin plugin;
    private final ArenaManager arenaManager;
    // Oyun durumlarini tutmak icin basit bir map (arenaName -> oyuncu listesi)
    private final Map<String, List<Player>> lobbyPlayers = new HashMap<>();
    private final Map<String, Boolean> activeGames = new HashMap<>();

    public GameManager(YakarTopPlugin plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    public void joinLobby(Player player, Arena arena) {
        // Lobi konumu ayarlanmamissa uyar
        if (arena.getLobbyLocation() == null) {
            player.sendMessage("§cBu arenanin lobi konumu ayarlanmamis!");
            return;
        }
        player.teleport(arena.getLobbyLocation());
        lobbyPlayers.computeIfAbsent(arena.getName(), k -> new ArrayList<>()).add(player);
        player.sendMessage("§aLobiye katildiniz: " + arena.getName());
        // Burada GUI acilabilir veya geri sayim baslatilabilir
    }

    public void startGame(Arena arena) {
        List<Player> players = lobbyPlayers.get(arena.getName());
        if (players == null || players.size() < 2) {
            return; // Yetersiz oyuncu
        }

        // Oyunculari yariya bol (Alan1 ve Alan2)
        int half = players.size() / 2;
        List<Player> team1 = players.subList(0, half);
        List<Player> team2 = players.subList(half, players.size());

        // Isinla
        for (Player p : team1) {
            p.teleport(arena.getArea1());
        }
        for (Player p : team2) {
            p.teleport(arena.getArea2());
        }

        activeGames.put(arena.getName(), true);

        // 5 dakika oyun suresi
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (activeGames.getOrDefault(arena.getName(), false)) {
                endGame(arena);
            }
        }, 20 * 60 * 5); // 5 dakika

        // Firlatma sistemi icin ornek: bir oyuncuya top ver
        // (Detayli top mekanigi bir sonraki adimda eklenecek)
        Player firstThrower = team1.get(0);
        firstThrower.sendMessage("§eTopu firlatmak icin hazir ol! (Sol tik ile al)");
        firstThrower.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 10, 1));
    }

    public void endGame(Arena arena) {
        activeGames.put(arena.getName(), false);
        List<Player> players = lobbyPlayers.remove(arena.getName());
        if (players != null) {
            for (Player p : players) {
                p.sendMessage("§6Oyun bitti!");
            }
        }
    }

    public boolean isGameActive(String arenaName) {
        return activeGames.getOrDefault(arenaName, false);
    }
}
