package com.seninadi.yakartop.listeners;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.managers.ArenaManager;
import com.seninadi.yakartop.managers.GameManager;
import com.seninadi.yakartop.models.Arena;
import com.seninadi.yakartop.utils.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyListener implements Listener {
    private final YakarTopPlugin plugin;
    private final ArenaManager arenaManager;
    private final GameManager gameManager;
    // Hangi oyuncu hangi arenanin lobisinde
    private final Set<UUID> lobbyPlayers = ConcurrentHashMap.newKeySet();

    public LobbyListener(YakarTopPlugin plugin, ArenaManager arenaManager, GameManager gameManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.gameManager = gameManager;
    }

    // GUI tiklamasi
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getType() != InventoryType.CHEST) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Arena menüsü olup olmadığını title'dan anlamıyoruz; bunun yerine holder kullanmak daha iyi.
        // Basit yaklasim: envanterin 49. slotunda book varsa bu bizim menumuzdur.
        var inv = event.getInventory();
        var infoItem = inv.getItem(49);
        if (infoItem == null || infoItem.getType() != Material.BOOK) return;

        event.setCancelled(true);

        var clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        if (clicked.getType() != Material.ENDER_PEARL && clicked.getType() != Material.RED_CONCRETE) return;

        // Item displayName'den arena ismini al
        if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;
        String arenaName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(clicked.getItemMeta().displayName());

        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) return;

        player.closeInventory();
        player.performCommand("db " + arena.getName());
    }

    // Lobi icinde oak_door sag tik -> /spawn
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!lobbyPlayers.contains(player.getUniqueId())) return;

        var item = player.getInventory().getItem(8); // 9. slot (index 8)
        if (item == null || item.getType() != Material.OAK_DOOR) return;

        event.setCancelled(true);
        player.performCommand("spawn");
        lobbyPlayers.remove(player.getUniqueId());
        player.sendMessage(Messages.info("Lobiden ayrıldınız."));
    }

    public void addLobbyPlayer(Player player) {
        lobbyPlayers.add(player.getUniqueId());
    }

    public void removeLobbyPlayer(Player player) {
        lobbyPlayers.remove(player.getUniqueId());
    }
}
