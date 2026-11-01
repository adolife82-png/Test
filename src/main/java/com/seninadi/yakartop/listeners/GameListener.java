package com.seninadi.yakartop.listeners;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class GameListener implements Listener {
    private final YakarTopPlugin plugin;
    private final GameManager gameManager;

    public GameListener(YakarTopPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        // Top sistemi bir sonraki adimda buraya eklenecek
        // Ornek: eger elinde "top" itemi varsa ve hedef seciliyse firlat
    }
}
