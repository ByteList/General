package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.database.DatabasePlayerObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpmeCommand implements CommandExecutor {

	GameChest gameChest = GameChest.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("opme")) {
			if(!(sender instanceof Player)) return true;
			
			Player p = (Player) sender;
			
			if(!gameChest.getDatabaseManager().getDatabasePlayer(p.getUniqueId()).getDatabaseElement(DatabasePlayerObject.OPERATOR).getAsBoolean()) {
				p.sendMessage(gameChest.prefix + "§cDu hast keine Berechtigung für diesen Befehl!");
				return true;
			}

			p.setOp(true);
			p.sendMessage(gameChest.prefix + "§7§oDu bist nun Operator!");
			return true;
		}
		return false;
	}
}
