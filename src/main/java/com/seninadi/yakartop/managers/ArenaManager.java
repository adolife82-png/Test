package com.seninadi.yakartop.managers;

import com.seninadi.yakartop.YakarTopPlugin;
import com.seninadi.yakartop.models.Arena;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class ArenaManager {
    private final YakarTopPlugin plugin;
    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager(YakarTopPlugin plugin) {
        this.plugin = plugin;
    }

    public void createOrUpdateArena(String name, int maxPlayers, Location area1, Location area2) {
        Arena arena = arenas.get(name.toLowerCase());
        if (arena == null) {
            arena = new Arena(name, maxPlayers, area1, area2);
            arenas.put(name.toLowerCase(), arena);
        } else {
            arena.setMaxPlayers(maxPlayers);
            if (area1 != null) arena.setArea1(area1);
            if (area2 != null) arena.setArea2(area2);
        }
    }

    public void setLobbyLocation(String name, Location location) {
        Arena arena = arenas.get(name.toLowerCase());
        if (arena != null) {
            arena.setLobbyLocation(location);
        }
    }

    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }
}
