package de.gamechest.party;

import de.gamechest.GameChest;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    }


    public void sendRequest(ProxiedPlayer player) {
        if(!this.requests.containsKey(player.getName())) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 3);
            long expire = calendar.getTime().getTime();
            this.requests.put(player.getName(), expire);

            for(ProxiedPlayer p : member) {
                p.sendMessage(gameChest.pr_party+"§a"+player.getName()+"§7 wurde in die Party eingeladen!");
            }
            leader.sendMessage(gameChest.pr_party+"§a"+player.getName()+"§7 wurde in die Party eingeladen!");


            TextComponent start = new TextComponent("§8\u00BB ");

            TextComponent accept = new TextComponent("§7[§aAkzeptieren§7]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept "+leader.getName()));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aKlicke, um die Party Anfrage zu akzeptieren.").create()));

            TextComponent middle = new TextComponent(" ");

            TextComponent deny = new TextComponent("§7[§cAblehnen§7]");
            deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny "+leader.getName()));
            deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aKlicke, um die Party Anfrage abzulehnen.").create()));

            player.sendMessage(gameChest.pr_party+"§6"+leader.getName()+"§7 hat dich in seine Party eingeladen!");
            player.sendMessage(start, accept, middle, deny);
        }
    }

    void acceptRequest(ProxiedPlayer player) {
        if(!this.requests.containsKey(player.getName())) {
            return;
        }
        if(new Date(this.requests.get(player.getName())).after(new Date())) {
            this.requests.remove(player.getName());
            return;
        }
        this.requests.remove(player.getName());
        this.member.add(player);
        gameChest.getDatabaseManager().getDatabaseParty().addMember(partyId, player.getName());

        for(ProxiedPlayer p : member) {
            p.sendMessage(gameChest.pr_party+"§a"+player.getName()+"§7 ist der Party beigetreten.");
        }
        leader.sendMessage(gameChest.pr_party+"§a"+player.getName()+"§7 ist der  Party beigetreten.");
    }

    public boolean promoteLeader(ProxiedPlayer player) {
        if(this.leader == player) return false;
        this.member.add(leader);
        gameChest.getDatabaseManager().getDatabaseParty().addMember(partyId, leader.getName());
        this.leader = player;
        this.member.remove(player);
        gameChest.getDatabaseManager().getDatabaseParty().removeMember(partyId, player.getName());
        return true;
    }

    void removeMember(ProxiedPlayer player) {
        if(this.member.contains(player)) {
            this.member.remove(player);
            gameChest.getDatabaseManager().getDatabaseParty().removeMember(partyId, player.getName());
        }
    }

    void deleteParty() {
        this.member = null;
        this.leader = null;
        this.requests = null;
        gameChest.getDatabaseManager().getDatabaseParty().deleteParty(this.partyId);
        this.partyId = null;
    }
}
