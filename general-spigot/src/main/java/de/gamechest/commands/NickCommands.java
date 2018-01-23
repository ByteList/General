package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.database.rank.Rank;
import de.gamechest.nick.Nick;
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
                gameChest.sendNoPermissionMessage(sender);
                return true;
            }

            if (nick.isNicked(p.getUniqueId())) nick.unNick(p);
            else nick.nick(p);
        }
        return true;
    }
}
