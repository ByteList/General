package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 08.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TeamchatCommand extends GCCommand {

    private final GameChest gameChest = GameChest.getInstance();

    public TeamchatCommand() {
        super("teamchat", "tc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        String prefix;

        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(!gameChest.onlineTeam.contains(pp)) {
                pp.sendMessage(gameChest.prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
                return;
            }
            prefix = gameChest.getRank(pp.getUniqueId()).getColor()+pp.getName();
        } else {
            prefix = "§c"+sender.getName();
        }

        if(args.length == 0) {
            sender.sendMessage(gameChest.prefix+"§c/teamchat <Nachricht>");
            return;
        }

        StringBuilder msg = new StringBuilder();

        for (String arg : args) {
            msg.append(arg).append(" ");
        }

        msg = new StringBuilder(msg.toString());

        for(ProxiedPlayer pp : gameChest.onlineTeam) {
            pp.sendMessage(gameChest.pr_msg_team +prefix+" §8\u00BB§e "+msg);
        }
    }
}
