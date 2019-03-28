package de.gamechest.party;

import com.google.gson.JsonObject;
import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.common.bungee.BungeeCloud;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.gamechest.GameChest;
import de.gamechest.common.ChestPrefix;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ByteList on 08.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Party {

    @Getter
    private String partyId;
    @Getter
    private ProxiedPlayer leader;
    @Getter
    private ArrayList<ProxiedPlayer> member;
    @Getter
    private HashMap<String, Long> requests;

    private final GameChest gameChest = GameChest.getInstance();

    public Party(ProxiedPlayer leader) {
        this.partyId = leader.getUniqueId().toString().replace("-", "");
        this.leader = leader;
        this.member = new ArrayList<>();
        this.requests = new HashMap<>();
        gameChest.getDatabaseManager().getDatabaseParty().createParty(partyId, leader.getName());
        gameChest.getDatabaseManager().getAsync().getOnlinePlayer(leader.getUniqueId(), dbPlayer ->
                dbPlayer.setDatabaseObject(DatabaseOnlinePlayerObject.PARTY_ID, this.partyId), DatabaseOnlinePlayerObject.PARTY_ID);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("packet", "PartyJoin");
        jsonObject.addProperty("partyId", partyId);
        jsonObject.addProperty("player", leader.getName());
        gameChest.getPacketHandler().sendPacket(leader.getServer().getInfo().getName(), jsonObject);
    }


    public void sendRequest(ProxiedPlayer player) {
        if(this.member.contains(player))
            return;
        if(!this.requests.containsKey(player.getName())) {
            this.requests.put(player.getName(), System.currentTimeMillis()/1000);

            for(ProxiedPlayer p : member) {
                p.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+player.getName()+"§a wurde in die Party eingeladen!");
            }
            leader.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+player.getName()+"§a wurde in die Party eingeladen!");


            TextComponent start = new TextComponent("§8\u00BB ");

            TextComponent accept = new TextComponent("§7[§aAkzeptieren§7]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept "+leader.getName()));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aKlicke, um die Party Anfrage zu akzeptieren.").create()));

            TextComponent middle = new TextComponent(" ");

            TextComponent deny = new TextComponent("§7[§cAblehnen§7]");
            deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny "+leader.getName()));
            deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cKlicke, um die Party Anfrage abzulehnen.").create()));

            player.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+leader.getName()+"§a hat dich in seine Party eingeladen!");
            player.sendMessage(start, accept, middle, deny);
        }
    }

    void acceptRequest(ProxiedPlayer player) {
        if(!this.requests.containsKey(player.getName())) {
            return;
        }
        if(System.currentTimeMillis()/1000 >= this.requests.get(player.getName()) + 60*3) {
            this.requests.remove(player.getName());
            return;
        }
        this.requests.remove(player.getName());
        this.member.add(player);
        gameChest.getDatabaseManager().getDatabaseParty().addMember(partyId, player.getName());
        gameChest.getDatabaseManager().getAsync().getOnlinePlayer(player.getUniqueId(), dbPlayer ->
                dbPlayer.setDatabaseObject(DatabaseOnlinePlayerObject.PARTY_ID, this.partyId), DatabaseOnlinePlayerObject.PARTY_ID);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("packet", "PartyJoin");
        jsonObject.addProperty("partyId", partyId);
        jsonObject.addProperty("player", player.getName());
        gameChest.getPacketHandler().sendPacket(player.getServer().getInfo().getName(), jsonObject);

        for(ProxiedPlayer p : member) {
            p.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+player.getName()+"§a ist der Party beigetreten.");
        }
        leader.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+player.getName()+"§a ist der Party beigetreten.");
    }

    public boolean promoteLeader(ProxiedPlayer player) {
        if(this.leader == player) return false;
        this.member.add(leader);
        gameChest.getDatabaseManager().getDatabaseParty().addMember(partyId, leader.getName());
        this.leader = player;
        gameChest.getDatabaseManager().getDatabaseParty().setLeader(partyId, leader.getName());
        this.member.remove(player);
        gameChest.getDatabaseManager().getDatabaseParty().removeMember(partyId, player.getName());
        for(ProxiedPlayer p : this.member) {
            p.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+this.leader+"§a wurde zum Party Leader ernannt.");
        }
        this.leader.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+this.leader+"§a wurde zum Party Leader ernannt.");
        return true;
    }

    void removeMember(ProxiedPlayer player) {
        if(this.member.contains(player)) {
            this.member.remove(player);
            for(ProxiedPlayer p : this.member) {
                p.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+player.getName()+"§a hat die Party verlassen.");
            }
            this.leader.sendMessage(ChestPrefix.PREFIX_PARTY+"§6"+player.getName()+"§a hat die Party verlassen.");
            gameChest.getDatabaseManager().getDatabaseParty().removeMember(partyId, player.getName());
            gameChest.getDatabaseManager().getAsync().getOnlinePlayer(player.getUniqueId(), dbPlayer ->
                    dbPlayer.setDatabaseObject(DatabaseOnlinePlayerObject.PARTY_ID, null), DatabaseOnlinePlayerObject.PARTY_ID);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("packet", "PartyLeave");
            jsonObject.addProperty("partyId", partyId);
            jsonObject.addProperty("player", player.getName());
            gameChest.getPacketHandler().sendPacket(player.getServer().getInfo().getName(), jsonObject);
        }
    }

    void deleteParty(boolean sendPacket) {
        this.member = null;
        this.leader = null;
        this.requests = null;
        if(gameChest.isCloudEnabled() && sendPacket) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("packet", "PartyDelete");
            jsonObject.addProperty("partyId", partyId);
            for (CloudServer lobbyServer : BungeeCloud.getInstance().getCloudAPI().getServerGroup("Lobby").getServers()) {
                gameChest.getPacketHandler().sendPacket(lobbyServer.getServerId(), jsonObject);
            }
        }
        this.partyId = null;
    }
}
