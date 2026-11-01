package com.seninadi.yakartop.listeners;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.managers.GameManager;
import org.bukkit.ChatColor;
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
        Player player = event.getPlayer();

        // Sadece sol tik (firlatma/yakalama)
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Burada top itemini kontrol et
            // Eger oyuncunun elinde "top" itemi varsa ve hedef seciliyse firlat
            // Ornek: hedefe kirmizi glow verme (Paper Glowing API veya Team)
            // player.sendMessage("Sol tik algilandi. Firlatma mekanigi buraya gelecek.");
        }
    }
}
