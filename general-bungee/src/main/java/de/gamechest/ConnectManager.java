package de.gamechest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 09.02.2017.
 */
public class ConnectManager {

    private final GameChest gameChest = GameChest.getInstance();

    public void setConnectState(ConnectState connectState) {
        gameChest.getConfiguration().set("connection.state", connectState.toString());
        gameChest.saveConfig();
    }

    public ConnectState getConnectState() {
        return ConnectState.valueOf(gameChest.getConfiguration().getString("connection.state").toUpperCase());
    }

    public void setPlayerLimit(int limit) {
        gameChest.getConfiguration().set("connection.player-limit", limit);
        gameChest.saveConfig();
    }

    public int getPlayerLimit() {
        return gameChest.getConfiguration().getInt("connection.player-limit");
    }

    public void setMotd(String mode, String motd) {
        gameChest.getConfiguration().set("motd."+mode.toLowerCase(), motd);
        gameChest.saveConfig();
    }

    public String getMotd(String mode) {
        return gameChest.getConfiguration().getString("motd."+mode.toLowerCase());
    }

    public void addUuidToWhiteList(UUID uuid) {
        if(!getWhiteList().contains(uuid)) {
            List<String> whitelist = new ArrayList<>(gameChest.getConfiguration().getStringList("whitelist"));
            whitelist.add(uuid.toString());
            gameChest.getConfiguration().set("whitelist", whitelist);
            gameChest.saveConfig();
        }
    }

    public void removeUuidFromWhiteList(UUID uuid) {
        if(getWhiteList().contains(uuid)) {
            List<String> whitelist = new ArrayList<>(gameChest.getConfiguration().getStringList("whitelist"));
            whitelist.remove(uuid.toString());
            gameChest.getConfiguration().set("whitelist", whitelist);
            gameChest.saveConfig();
        }
    }

    public Collection<UUID> getWhiteList() {
        Collection<UUID> whitelist = new ArrayList<>();
        for(String uuid : gameChest.getConfiguration().getStringList("whitelist"))
            whitelist.add(UUID.fromString(uuid));

        return whitelist;
    }

    public String getEventServer() {
        return  gameChest.getConfiguration().getString("connection.event-server");
    }

    public enum ConnectState {
        OPEN, WHITELIST, MAINTENANCE, DEVELOPMENT
    }
}
