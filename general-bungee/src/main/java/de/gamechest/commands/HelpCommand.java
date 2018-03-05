package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 28.02.2017.
 */
public class HelpCommand extends GCCommand {

    public HelpCommand() {
        super("help", "?", "gamechest", "about");
    }
    
    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {
            UUID uuid = ((ProxiedPlayer) sender).getUniqueId();
            sender.sendMessage(gameChest.prefix + "§6Befehle des Game-Chest.de Netzwerkes:");
            sender.sendMessage("§8\u00BB §e/lobby §7- Kehre zur Lobby zurück");
            sender.sendMessage("§8\u00BB §e/stats §7- Erfahre deine aktuellen Statistiken");
            sender.sendMessage("§8\u00BB §e/msg §7- Schreibe mit anderen Spielern");
            sender.sendMessage("§8\u00BB §e/fix §7- Resette deine aktuelle Location");
//            sender.sendMessage("§8\u00BB §e/onlinetime §7- Zeige dir die Onlinezeit von Usern an");
            sender.sendMessage("§8\u00BB §e/report §7- Melde einen Spieler");
//            sender.sendMessage("§8\u00BB §e/chatlog §7- Erstelle einen Chatlog");
        }
    }
}
