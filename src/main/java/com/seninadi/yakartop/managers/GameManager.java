package com.seninadi.yakartop.managers;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.listeners.LobbyListener;
import com.seninadi.yakartop.models.Arena;
import com.seninadi.yakartop.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GameManager {
    private final YakarTopPlugin plugin;
    private final ArenaManager arenaManager;
    private LobbyListener lobbyListener;

    private final Map<String, List<Player>> lobbyPlayers = new HashMap<>();
    private final Map<String, Boolean> activeGames = new HashMap<>();
    private final Map<String, List<Player>> gamePlayers = new HashMap<>();

    public GameManager(YakarTopPlugin plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    public void setLobbyListener(LobbyListener listener) {
        this.lobbyListener = listener;
    }

    public void joinLobby(Player player, Arena arena) {
        if (arena.getLobbyLocation() == null) {
            player.sendMessage(Messages.error("Bu arenanın lobi konumu ayarlanmamış!"));
            return;
        }
        if (arena.isActive()) {
            player.sendMessage(Messages.error("Bu arenada zaten bir oyun var."));
            return;
        }
        // Ayni oyuncu zaten bir lobide mi?
        for (List<Player> list : lobbyPlayers.values()) {
            if (list.contains(player)) {
                player.sendMessage(Messages.error("Zaten bir arenanın lobisindesin!"));
                return;
            }
        }

        player.teleport(arena.getLobbyLocation());
        lobbyPlayers.computeIfAbsent(arena.getName(), k -> new ArrayList<>()).add(player);
        if (lobbyListener != null) lobbyListener.addLobbyPlayer(player);

        // Lobi itemi: 9. slotta oak_door
        ItemStack door = new ItemStack(Material.OAK_DOOR);
        ItemMeta meta = door.getItemMeta();
        meta.displayName(Messages.raw("<red><bold>Lobiden Ayrıl</bold>"));
        meta.lore(List.of(Messages.raw("<gray>Sağ tıklayarak lobiden ayrıl.")));
        door.setItemMeta(meta);
        player.getInventory().setItem(8, door);
        // Oyuncunun diger itemlerini temizle (lobi envanteri)
        // player.getInventory().clear(); // Iskelet asamasinda yorum satiri
        player.sendMessage(Messages.success("Lobiye katıldınız: <white>" + arena.getName()));
    }

    public void removeFromLobby(Player player, Arena arena) {
        List<Player> list = lobbyPlayers.get(arena.getName());
        if (list != null) list.remove(player);
        if (lobbyListener != null) lobbyListener.removeLobbyPlayer(player);
        player.getInventory().setItem(8, null);
    }

    public void startGame(Arena arena) {
        List<Player> players = lobbyPlayers.get(arena.getName());
        if (players == null || players.size() < 2) {
            return;
        }

        int half = players.size() / 2;
        List<Player> team1 = new ArrayList<>(players.subList(0, half));
        List<Player> team2 = new ArrayList<>(players.subList(half, players.size()));

        for (Player p : team1) p.teleport(arena.getArea1());
        for (Player p : team2) p.teleport(arena.getArea2());

        // Envanter temizligi
        for (Player p : players) {
            p.getInventory().clear();
            p.setHealth(20);
            p.setFoodLevel(20);
        }

        arena.setActive(true);
        activeGames.put(arena.getName(), true);
        gamePlayers.put(arena.getName(), new ArrayList<>(players));

        // 5 dakika oyun suresi
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (activeGames.getOrDefault(arena.getName(), false)) {
                stopGame(arena);
            }
        }, 20 * 60 * 5);

        // Top sistemi ornegi
        if (!team1.isEmpty()) {
            Player thrower = team1.get(0);
            thrower.sendMessage(Messages.info("Topu fırlatmak için hazır ol! <yellow>(Sol tık)"));
            thrower.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 10, 1));
        }

        for (Player p : players) {
            p.sendMessage(Messages.success("Oyun başladı! <yellow>5 dakika"));
        }
    }

    public void stopGame(Arena arena) {
        arena.setActive(false);
        activeGames.put(arena.getName(), false);

        List<Player> players = gamePlayers.remove(arena.getName());
        if (players == null) players = lobbyPlayers.remove(arena.getName());
        else lobbyPlayers.remove(arena.getName());

        if (players != null) {
            for (Player p : players) {
                p.sendMessage(Messages.info("Oyun sona erdi!"));
                if (lobbyListener != null) lobbyListener.removeLobbyPlayer(p);
            }
        }
    }

    public boolean isGameActive(String arenaName) {
        return activeGames.getOrDefault(arenaName, false);
    }

    public List<Player> getLobbyPlayers(String arenaName) {
        return lobbyPlayers.getOrDefault(arenaName, Collections.emptyList());
    }
}
