package de.gamechest.commands.party;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.party.Party;
import de.gamechest.party.PartyManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 08.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PartyCommand extends GCCommand {

    public PartyCommand() {
        super("party");
    }

    private final GameChest gameChest = GameChest.getInstance();
    private final PartyManager partyManager = gameChest.getPartyManager();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cNur für Spieler!");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("leave")) {
                if(!partyManager.isPlayerInAParty(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cDu bist in keiner Party!");
                    return;
                }

                Party party = partyManager.getParty(player.getUniqueId());

                partyManager.leaveParty(party.getPartyId(), player);
                return;
            }

            if(args[0].equalsIgnoreCase("delete")) {
                if(!partyManager.isPlayerInAParty(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cDu bist in keiner Party!");
                    return;
                }

                Party party = partyManager.getParty(player.getUniqueId());

                if(!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cNur der Party Leader kann die Party auflösen!");
                    return;
                }

                partyManager.deleteParty(party, true);
                return;
            }

            if(args[0].equalsIgnoreCase("list")) {
                if(!partyManager.isPlayerInAParty(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cDu bist in keiner Party!");
                    return;
                }

                Party party = partyManager.getParty(player.getUniqueId());

                player.sendMessage(gameChest.pr_party+"§6Aktuelle Party:");
                player.sendMessage("§8\u00BB §7Party-Leader: §c"+party.getLeader().getName());
                player.sendMessage("§8\u00BB §7Member: ");
                for(ProxiedPlayer member : party.getMember()) {
                    player.sendMessage("    §a\u00BB §e"+member.getName());
                }
                player.sendMessage(" ");
                return;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("accept")) {
                if(partyManager.isPlayerInAParty(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cDu bist schon in einer Party!");
                    return;
                }

                String user = args[1];
                ProxiedPlayer tplayer = gameChest.getProxy().getPlayer(user);
                if(tplayer == null) {
                    player.sendMessage(gameChest.pr_party+"§cDie Party von "+user+" konnte nicht gefunden werden!");
                    return;
                }
                Party party = partyManager.getParty(tplayer.getUniqueId());

                if(!party.getRequests().containsKey(player.getName())) {
                    player.sendMessage(gameChest.pr_party+"§cDu wurdest nicht in diese Party eingeladen!");
                    return;
                }

                if(System.currentTimeMillis()/1000 >= party.getRequests().get(player.getName()) + 60*3) {
                    player.sendMessage(gameChest.pr_party+"§cDeine Einladung ist abgelaufen!");
                    party.getRequests().remove(player.getName());
                    return;
                }

                partyManager.acceptRequest(party.getPartyId(), player);
                return;
            }

            if(args[0].equalsIgnoreCase("deny")) {
                String user = args[1];
                ProxiedPlayer tplayer = gameChest.getProxy().getPlayer(user);
                if(tplayer == null) {
                    player.sendMessage(gameChest.pr_party+"§cDie Party von "+user+" konnte nicht gefunden werden!");
                    return;
                }
                Party party = partyManager.getParty(tplayer.getUniqueId());

                if(!party.getRequests().containsKey(player.getName())) {
                    player.sendMessage(gameChest.pr_party+"§cDu wurdest nicht in diese Party eingeladen!");
                    return;
                }

                if(System.currentTimeMillis()/1000 >= party.getRequests().get(player.getName()) + 60*3) {
                    player.sendMessage(gameChest.pr_party+"§cDeine Einladung ist abgelaufen!");
                    party.getRequests().remove(player.getName());
                    return;
                }

                party.getRequests().remove(player.getName());
                player.sendMessage(gameChest.pr_party+"§cDu hast die Party Anfrage abgelenht.");
                for(ProxiedPlayer p : party.getMember()) {
                    p.sendMessage(gameChest.pr_party+"§6"+player.getName()+"§c hat die Party Anfrage abgelehnt.");
                }
                tplayer.sendMessage(gameChest.pr_party+"§6"+player.getName()+"§c hat die Party Anfrage abgelehnt.");
                return;
            }

            if(args[0].equalsIgnoreCase("invite")) {
                String user = args[1];
                ProxiedPlayer tplayer = gameChest.getProxy().getPlayer(user);

                if(tplayer == null) {
                    player.sendMessage(gameChest.pr_party+"§cDer Spieler ist nicht online!");
                    return;
                }

                Party party;
                if (!partyManager.isPlayerInAParty(player.getUniqueId())) {
                    party = partyManager.createParty(player);
                    player.sendMessage(gameChest.pr_party+"§7Es wurde eine Party erstellt.");
                } else {
                    party = partyManager.getParty(player.getUniqueId());
                    if(!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(gameChest.pr_party+"§cNur der Party Leader kann Spieler einladen!");
                        return;
                    }
                }
                // TODO: 09.07.2017 settings if

                if(party.getMember().contains(tplayer)) {
                    player.sendMessage(gameChest.pr_party+"§7Der Spieler ist bereits in deiner Party.");
                    return;
                }

                if(party.getRequests().containsKey(tplayer.getName())) {
                    player.sendMessage(gameChest.pr_party+"§7Der Spieler wurde bereits eingeladen.");
                    return;
                }

                party.sendRequest(tplayer);
                return;
            }

            if(args[0].equalsIgnoreCase("kick")) {
                if(!partyManager.isPlayerInAParty(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cDu bist in keiner Party!");
                    return;
                }

                Party party = partyManager.getParty(player.getUniqueId());

                if(!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cNur der Party Leader kann Mitglieder kicken!");
                    return;
                }

                String user = args[1];
                ProxiedPlayer tplayer = gameChest.getProxy().getPlayer(user);

                if(tplayer == null) {
                    player.sendMessage(gameChest.pr_party+"§cDer Spieler ist nicht online!");
                    return;
                }

                if(!party.getMember().contains(tplayer)) {
                    player.sendMessage(gameChest.pr_party+"§cDer Spieler ist nicht in deiner Party!");
                    return;
                }

                if(tplayer.getUniqueId().equals(party.getLeader().getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cDu kannst dich nicht selbst kicken!");
                    return;
                }

                partyManager.removeMember(party.getPartyId(), player);
                for(ProxiedPlayer p : party.getMember()) {
                    p.sendMessage(gameChest.pr_party+"§6"+player.getName()+"§c wurde aus der Party gekickt.");
                }
                party.getLeader().sendMessage(gameChest.pr_party+"§6"+player.getName()+"§c wurde aus der Party gekickt.");

                return;
            }

            if(args[0].equalsIgnoreCase("promote")) {
                if(!partyManager.isPlayerInAParty(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cDu bist in keiner Party!");
                    return;
                }

                Party party = partyManager.getParty(player.getUniqueId());

                if(!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cNur der Party Leader kann jemanden zum Leader ernennen!");
                    return;
                }

                String user = args[1];
                ProxiedPlayer tplayer = gameChest.getProxy().getPlayer(user);

                if(tplayer == null) {
                    player.sendMessage(gameChest.pr_party+"§cDer Spieler ist nicht online!");
                    return;
                }

                if(tplayer.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(gameChest.pr_party+"§cDu bist bereits der Party Leader!");
                    return;
                }

                if(!party.getMember().contains(tplayer)) {
                    player.sendMessage(gameChest.pr_party+"§cDer Spieler ist nicht in deiner Party!");
                    return;
                }

                party.promoteLeader(tplayer);
                return;
            }
        }

        player.sendMessage(gameChest.pr_party+"§6Alle Party Befehle:");
        player.sendMessage("§8\u00BB §c/party accept|deny <Spieler>");
        player.sendMessage("§8\u00BB §c/party leave");
        player.sendMessage("§8\u00BB §c/party invite <Spieler>");
        player.sendMessage("§8\u00BB §c/party delete");
        player.sendMessage("§8\u00BB §c/party kick <Spieler>");
        player.sendMessage("§8\u00BB §c/party promote <Spieler>");
        player.sendMessage("§8\u00BB §c/party list");
        player.sendMessage("§8\u00BB §c/p <Nachricht>");
    }
}
