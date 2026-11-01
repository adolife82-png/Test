package com.seninadi.yakartop;

import com.seninadi.yakartop.commands.DbCommand;
import com.seninadi.yakartop.listeners.GameListener;
import com.seninadi.yakartop.managers.ArenaManager;
import com.seninadi.yakartop.managers.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class YakarTopPlugin extends JavaPlugin {

    private ArenaManager arenaManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        // Manager'lari baslat
        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this, arenaManager);

        // Komutlari kaydet
        this.getCommand("db").setExecutor(new DbCommand(this, arenaManager, gameManager));

        // Eventleri kaydet
        getServer().getPluginManager().registerEvents(new GameListener(this, gameManager), this);

        getLogger().info("YakarTop basariyla etkinlestirildi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("YakarTop kapatildi.");
    }

    public ArenaManager getArenaManager() { return arenaManager; }
    public GameManager getGameManager() { return gameManager; }
}
