package de.gamechest.packet;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.GCPatron;
import de.gamechest.GameChest;

import java.util.HashMap;

/**
 * Created by ByteList on 09.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketHandlerGC {

    private HashMap<Integer, String> ids = new HashMap<>();
    private HashMap<String, Integer> clients = new HashMap<>();
    private HashMap<Integer, GCPatron> patrons = new HashMap<>();

    private final GameChest gameChest = GameChest.getInstance();

    public GCPatron getPatron(String client) {
        if(this.clients.containsKey(client)) {
            return this.patrons.get(this.clients.get(client));
        }
        return null;
    }

    public String getServerId(int clientId) {
        if(this.ids.containsKey(clientId)) {
            return this.ids.get(clientId);
        }
        return null;
    }

    void registerClient(String client, GCPatron patron) {
        int clientId = patron.getID();
        if((!this.clients.containsKey(client)) && (!this.clients.containsValue(clientId))) {
            this.clients.put(client, clientId);
            this.ids.put(clientId, client);
            if(!this.patrons.containsKey(clientId)) {
                this.patrons.put(clientId, patron);
                gameChest.getLogger().info("Client "+patron.getID()+"("+client+") registered!");
                return;
            }
        }
        gameChest.getLogger().warning("Client "+patron.getID()+"("+client+") can't registered!");
    }

    void unregisterClient(GCPatron patron) {
        int clientId = patron.getID();
        String client = this.ids.get(clientId);
        if(this.patrons.containsKey(clientId))
            this.patrons.remove(clientId);
        if(this.clients.containsKey(client))
            this.clients.remove(client);
        if(this.ids.containsKey(clientId))
            this.ids.remove(clientId);
        gameChest.getLogger().info("Client "+patron.getID()+"("+client+") unregistered!");
    }

    public void sendPacket(GCPatron patron, JsonObject jsonObject) {
        patron.sendPacket(jsonObject);
    }

    public void sendPacket(String serverId, JsonObject jsonObject) {
        if(clients.containsKey(serverId)) {
            patrons.get(clients.get(serverId)).sendPacket(jsonObject);
        }
        else throw new NullPointerException(serverId+" hasn't a client id.");
    }
}
