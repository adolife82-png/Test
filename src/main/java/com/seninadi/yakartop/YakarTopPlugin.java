package com.seninadi.yakartop;

import com.seninadi.yakartop.commands.DbCommand;
import com.seninadi.yakartop.commands.DbTabCompleter;
import com.seninadi.yakartop.gui.ArenaMenu;
import com.seninadi.yakartop.listeners.GameListener;
import com.seninadi.yakartop.listeners.LobbyListener;
import com.seninadi.yakartop.managers.ArenaManager;
import com.seninadi.yakartop.managers.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class YakarTopPlugin extends JavaPlugin {

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private ArenaMenu arenaMenu;

    @Override
    public void onEnable() {
        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this, arenaManager);
        this.arenaMenu = new ArenaMenu(this, arenaManager);

        LobbyListener lobbyListener = new LobbyListener(this, arenaManager, gameManager);
        gameManager.setLobbyListener(lobbyListener);

        DbCommand dbCommand = new DbCommand(this, arenaManager, gameManager, arenaMenu);
        var cmd = this.getCommand("db");
        if (cmd != null) {
            cmd.setExecutor(dbCommand);
            cmd.setTabCompleter(new DbTabCompleter(arenaManager));
        }

        getServer().getPluginManager().registerEvents(lobbyListener, this);
        getServer().getPluginManager().registerEvents(new GameListener(this, gameManager), this);

        getLogger().info("YakarTop başarıyla etkinleştirildi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("YakarTop kapatıldı.");
    }

    public ArenaManager getArenaManager() { return arenaManager; }
    public GameManager getGameManager() { return gameManager; }
    public ArenaMenu getArenaMenu() { return arenaMenu; }
}
