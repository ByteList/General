package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.rank.Rank;
import de.gamechest.nick.Nick;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class NickCommands implements CommandExecutor {

    private GameChest gameChest = GameChest.getInstance();
    private Nick nick = gameChest.getNick();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("nick")) {
            if (!(sender instanceof Player))
                return true;

            Player p = (Player) sender;

            if (!gameChest.hasRank(p.getUniqueId(), Rank.VIP)) {
                sender.sendMessage(gameChest.prefix + "§cDu hast keine Berechtigung für diesen Befehl!");
                return true;
            }

            if (args.length == 1) {
                if (gameChest.hasRank(p.getUniqueId(), Rank.DEVELOPER)) {
                    if (args[0].equalsIgnoreCase("help")) {
                        p.sendMessage(gameChest.prefix + "§c/nick <Spieler>");
                        p.sendMessage(gameChest.prefix + "§c/nick <Spieler> <Nickname>");
                        return true;
                    }
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t != null) {
                        if (nick.isNicked(t.getUniqueId())) {
                            t.sendMessage(gameChest.prefix + "§eDein Nickname wird entfernt...");
                            nick.unNick(t);
                            p.sendMessage(gameChest.prefix + "§eDu hast " + t.getName() + " entnickt!");
                        } else {
                            p.sendMessage(gameChest.prefix + "§eDu hast " + t.getName() + " genickt!");
                            t.sendMessage(gameChest.prefix + "§eDu wurdest genickt!");
                            nick.nick(t);
                        }
                        return true;
                    }
                    p.sendMessage(gameChest.prefix + "§cDer Spieler muss sich auf dem selben Server befinden!");
                    return true;
                }
            }
            if (args.length == 2) {
                if (gameChest.hasRank(p.getUniqueId(), Rank.DEVELOPER)) {
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t == null) {
                        p.sendMessage(gameChest.prefix + "§cDer Spieler muss sich auf dem selben Server befinden!");
                        return true;
                    }
                    String nickname = args[1];

                    if (nickname.length() < 3) {
                        p.sendMessage(gameChest.prefix + "§cEin Minecraftname muss min. 3 Zeichen lang sein!");
                        return true;
                    }

                    if (nickname.length() > 16) {
                        p.sendMessage(gameChest.prefix + "§cEin Minecraftname darf max. 16 Zeichen lang sein!");
                        return true;
                    }

                    if (!nick.isNicked(t.getUniqueId())) {
                        p.sendMessage(gameChest.prefix + "§eDu hast " + args[0] + " genickt!");
                        t.sendMessage(gameChest.prefix + "§eDu wurdest genickt!");
                        nick.nick(t, nickname);
                    } else {
                        p.sendMessage(gameChest.prefix + "§eDu hast den Nickname von " + args[0] + " geändert!");
                        t.sendMessage(gameChest.prefix + "§eDein Nickname wurde geändert!");
                        gameChest.getDatabaseManager().getDatabaseOnlinePlayer(p.getUniqueId()).setDatabaseObject(DatabaseOnlinePlayerObject.NICKNAME, null);
                        nick.nick(p, nickname);
                    }
                    return true;
                }
            }

            if (nick.isNicked(p.getUniqueId())) nick.unNick(p);
            else nick.nick(p);
        }
        return true;
    }
}
