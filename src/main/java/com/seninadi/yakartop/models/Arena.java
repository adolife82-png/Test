package com.seninadi.yakartop.models;

import org.bukkit.Location;

public class Arena {
    private final String name;
    private int maxPlayers;
    private Location area1;
    private Location area2;
    private Location lobbyLocation;

    public Arena(String name, int maxPlayers, Location area1, Location area2) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.area1 = area1;
        this.area2 = area2;
    }

    public String getName() { return name; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public Location getArea1() { return area1; }
    public void setArea1(Location area1) { this.area1 = area1; }
    public Location getArea2() { return area2; }
    public void setArea2(Location area2) { this.area2 = area2; }
    public Location getLobbyLocation() { return lobbyLocation; }
    public void setLobbyLocation(Location lobbyLocation) { this.lobbyLocation = lobbyLocation; }
}
