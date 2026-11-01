package com.seninadi.yakartop.gui;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.managers.ArenaManager;
import com.seninadi.yakartop.models.Arena;
import com.seninadi.yakartop.utils.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArenaMenu {
    private final YakarTopPlugin plugin;
    private final ArenaManager arenaManager;

    public ArenaMenu(YakarTopPlugin plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    public void open(Player player) {
        // 6 satır = 54 slot
        Inventory inv = Bukkit.createInventory(null, 54, Messages.title("Yakar Top | Arena Listesi"));

        // Border cam panel
        ItemStack border = createItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty());
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

        // Arena butonlari ic alanda
        int slot = 10;
        for (Arena arena : arenaManager.getArenas().values()) {
            if (slot > 43) break;

            // Border'in oldugu slotlari atla
            while (slot < 44 && (slot % 9 == 0 || slot % 9 == 8)) slot++;

            ItemStack item = createItem(
                    arena.isActive() ? Material.RED_CONCRETE : Material.ENDER_PEARL,
                    Component.text(arena.getName())
                            .color(arena.isActive() ? net.kyori.adventure.text.format.NamedTextColor.RED
                                    : net.kyori.adventure.text.format.NamedTextColor.AQUA)
                            .decoration(TextDecoration.ITALIC, false)
            );

            List<Component> lore = new ArrayList<>();
            lore.add(Messages.raw("<dark_gray>▪ <gray>Maksimum Oyuncu: <white>" + arena.getMaxPlayers()));
            lore.add(Messages.raw("<dark_gray>▪ <gray>Durum: " + (arena.isActive()
                    ? "<red>Oyun Devam Ediyor"
                    : "<green>Müsait")));
            lore.add(Component.empty());
            lore.add(Messages.raw("<yellow>➤ <gray>Katılmak için <white>tıkla!"));

            ItemMeta meta = item.getItemMeta();
            meta.lore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
            slot++;
        }

        // Alt bilgi butonu (slot 49 = book)
        ItemStack info = createItem(Material.BOOK,
                Component.text("Bilgi").color(net.kyori.adventure.text.format.NamedTextColor.YELLOW));
        ItemMeta infoMeta = info.getItemMeta();
        List<Component> infoLore = new ArrayList<>();
        infoLore.add(Messages.raw("<gray>Yakar Top, iki takım arasında"));
        infoLore.add(Messages.raw("<gray>oynanan bir top yakalama oyunudur."));
        infoLore.add(Component.empty());
        infoLore.add(Messages.raw("<gray>Topu fırlat, karşı takıma yakalat!"));
        infoMeta.lore(infoLore);
        info.setItemMeta(infoMeta);
        inv.setItem(49, info);

        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, Component name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }
}
