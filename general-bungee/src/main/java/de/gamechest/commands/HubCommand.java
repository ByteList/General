package de.gamechest.commands;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 20.02.2017.
 */
public class HubCommand extends GCCommand {

    public HubCommand() {
        super("hub", "lobby", "l", "lb");
    }
    
    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cDu bist kein Spieler!");
            return;
        }

        ProxiedPlayer pp = (ProxiedPlayer) sender;

        if(pp.getServer().getInfo().getName().equals("BauEvent") && !gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER)) {
            pp.sendMessage("§cAuf dem BauEvent-Server ist der Lobby-Befehl deaktiviert.");
            return;
        }

        int i = ByteCloudMaster.getInstance().getCloudHandler().connect(
                ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId(), pp);

        if(i == 0) {
            pp.sendMessage(gameChest.prefix+"§eVerbinde zur Lobby...");
            return;
        }
        if(i == 1) {
            pp.sendMessage(gameChest.prefix+"§6Du befindest dich bereits auf der Lobby!");
            return;
        }
        if(i == 2) {
            pp.sendMessage(gameChest.prefix+"§cKonnte keinen Lobby-Server finden! Bitte melde dies dem Support!");
            return;
        }
        pp.sendMessage(gameChest.prefix+"§cError: Konnte keine passende Aktion ausführen!");
    }
}
