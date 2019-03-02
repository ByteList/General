package de.gamechest.commands.party;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.party.Party;
import de.gamechest.party.PartyManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 09.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PartyChatCommand extends GCCommand {

    public PartyChatCommand() {
        super("p");
    }

    private final GameChest gameChest = GameChest.getInstance();
    private final PartyManager partyManager = gameChest.getPartyManager();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if(!partyManager.isPlayerInAParty(player.getUniqueId())) {
            player.sendMessage(ChestPrefix.PREFIX_MSG_PARTY+"§cDu bist in keiner Party!");
            return;
        }
        Party party = partyManager.getParty(player.getUniqueId());

        if(args.length == 0) {
            player.sendMessage(ChestPrefix.PREFIX_MSG_PARTY+"§c/p <Nachricht>");
            return;
        }

        StringBuilder message = new StringBuilder();

        for(String arg : args) {
            message.append(arg);
        }

        String prefix = ChestPrefix.PREFIX_MSG_PARTY+(party.getLeader().getUniqueId().equals(player.getUniqueId()) ? "§6" : "§a") + player.getName()+ " §8\u00BB §7";

        for(ProxiedPlayer p : party.getMember()) {
            p.sendMessage(prefix+message);
        }
        party.getLeader().sendMessage(prefix+message);

    }
}
