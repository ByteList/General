package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import net.md_5.bungee.api.CommandSender;

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
        if(args.length == 1) {
            try {
                sendHelp(sender, Integer.valueOf(args[0]));
                return;
            } catch (NumberFormatException ignored) {}
        }

        sendHelp(sender, 1);
    }


    private void sendHelp(CommandSender sender, int page) {
        switch (page) {
            case 2 :
                sender.sendMessage(gameChest.prefix + "§6Befehle des Game-ChestPrefix.de Netzwerkes: §7(2/2)");
                sender.sendMessage("§8\u00BB §e/speed §7- Ändere deine Fluggeschwindigkeit");
                sender.sendMessage("§8\u00BB §e//wand §7- WorldEdit-Wand Tool");
                sender.sendMessage("§8\u00BB §e/claim §7- Plot bekommen");
                sender.sendMessage("§8\u00BB §e/delete §7- Plot löschen");
                sender.sendMessage("§8\u00BB §e/clear §7- Plot resetten");
                sender.sendMessage("§8\u00BB §e/submit §7- Plot einsenden");
                sender.sendMessage("§8\u00BB §e/barrier §7- Erhalte eine Barriere");
                break;
            default :
                sender.sendMessage(gameChest.prefix + "§6Befehle des Game-ChestPrefix.de Netzwerkes: §7(1/2)");
//              sender.sendMessage("§8\u00BB §e/lobby §7- Kehre zur Lobby zurück");
//              sender.sendMessage("§8\u00BB §e/stats §7- Erfahre deine aktuellen Statistiken");
                sender.sendMessage("§8\u00BB §e/msg §7- Schreibe mit anderen Spielern");
                sender.sendMessage("§8\u00BB §e/fix §7- Resette deine aktuelle Location");
//              sender.sendMessage("§8\u00BB §e/onlinetime §7- Zeige dir die Onlinezeit von Usern an");
                sender.sendMessage("§8\u00BB §e/report §7- Melde einen Spieler");
//              sender.sendMessage("§8\u00BB §e/chatlog §7- Erstelle einen Chatlog");
                break;
        }
    }

}
