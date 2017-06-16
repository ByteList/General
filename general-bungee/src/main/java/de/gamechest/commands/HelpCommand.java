package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.rank.Rank;
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
            sender.sendMessage("§8\u00BB §e/onlinetime §7- Zeige dir die Onlinezeit von Usern an");
            sender.sendMessage("§8\u00BB §e/report §7- Melde einen Spieler");
            sender.sendMessage("§8\u00BB §e/chatlog §7- Erstelle einen Chatlog");
            if (gameChest.equalsRank(uuid, Rank.PREMIUM)) {
                sender.sendMessage("§8\u00BB §6/premium §7- Erfahre deine aktuelle Premium-Laufzeit");
            }
            if (gameChest.hasRank(uuid, Rank.PREMIUM)) {
                sender.sendMessage("§8\u00BB §6/togglerank §7- Verstecke deinen Rang in den Minigames");
            }
            if (gameChest.hasRank(uuid, Rank.VIP)) {
                sender.sendMessage("§8\u00BB §5/nick §7- Erhalte eine neue Identität zum Spielen");
            }
            if (gameChest.hasRank(uuid, Rank.BUILDER)) {
                sender.sendMessage("§8\u00BB §c/faq §7- Helfe Usern mit vorgegebenen Antworten");
                sender.sendMessage("§8\u00BB §c/list §7- Liste mit allen online Usern + Server");
                sender.sendMessage("§8\u00BB §c/kick §7- Werfe Regelbrecker vom Netzwerk");
                sender.sendMessage("§8\u00BB §b/join §7- Verbinde dich zu einem Server");
                sender.sendMessage("§8\u00BB §b/goto §7- Springe einem User hinterher");
            }
            if (gameChest.hasRank(uuid, Rank.SUPPORTER)) {
                sender.sendMessage("§8\u00BB §b/server §7- Server-Cloud Befehl");
                sender.sendMessage("§8\u00BB §c/ban §7- Sperre Regelbrechern den Netzwergzugang");
                sender.sendMessage("§8\u00BB §c/nicklist §7- Liste von genickten Usern + Rang & Nick");
                sender.sendMessage("§8\u00BB §c/playerinfo §7- Detaillierte Informationen eines Users");
            }
            if (gameChest.hasRank(uuid, Rank.MODERATOR)) {
                sender.sendMessage("§8\u00BB §c/unban §7- Entsperre User");
            }
            if (gameChest.hasRank(uuid, Rank.DEVELOPER)) {
                sender.sendMessage("§8\u00BB §4/gcg §7- Konfiguriere Netzwerk-Einstellungen");
                sender.sendMessage("§8\u00BB §4/rank §7- Setze einem User einen Rang");
                sender.sendMessage("§8\u00BB §4/premium §7- Setze einem User den Premium-Rang");
                sender.sendMessage("§8\u00BB §4/coins §7- Setze einem User Coins");
                sender.sendMessage("§8\u00BB §4/cloud §7- Cloud-Befehle");
            }
        }
    }
}
