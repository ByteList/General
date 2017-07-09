package de.gamechest.packet;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.GCJsonClientListener;
import de.gamechest.party.event.PartyDeleteEvent;
import de.gamechest.party.event.PartyJoinEvent;
import de.gamechest.party.event.PartyLeaveEvent;
import org.bukkit.Bukkit;

/**
 * Created by ByteList on 14.02.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketListener extends GCJsonClientListener {
    @Override
    public void jsonReceived(JsonObject jsonObject) {
        if(jsonObject.has("packet")) {
            String packet = jsonObject.get("packet").getAsString();

            if(packet.equals("PartyJoin")) {
                String player = jsonObject.get("player").getAsString();

                if(Bukkit.getPlayer(player) != null) {
                    String partyId = jsonObject.get("partyId").getAsString();
                    PartyJoinEvent partyJoinEvent = new PartyJoinEvent(partyId, Bukkit.getPlayer(player));

                    Bukkit.getPluginManager().callEvent(partyJoinEvent);
                }
            }

            if(packet.equals("PartyLeave")) {
                String player = jsonObject.get("player").getAsString();

                if(Bukkit.getPlayer(player) != null) {
                    String partyId = jsonObject.get("partyId").getAsString();
                    PartyLeaveEvent partyLeaveEvent = new PartyLeaveEvent(partyId, Bukkit.getPlayer(player));

                    Bukkit.getPluginManager().callEvent(partyLeaveEvent);
                }
            }

            if(packet.equals("PartyDelete")) {
                String partyId = jsonObject.get("partyId").getAsString();
                PartyDeleteEvent partyDeleteEvent = new PartyDeleteEvent(partyId);

                Bukkit.getPluginManager().callEvent(partyDeleteEvent);
            }
        }
    }

    @Override
    public void disconnected() {}

    @Override
    public void connected() {
        Bukkit.getLogger().info("[GC-PacketServer] Connected as client!");
    }
}
