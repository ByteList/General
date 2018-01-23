package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.database.DatabasePlayerObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpmeCommand implements CommandExecutor {

	private GameChest gameChest = GameChest.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player p = (Player) sender;

		gameChest.getDatabaseManager().getAsync().getPlayer(p.getUniqueId(), dbPlayer-> {
			if(!dbPlayer.getDatabaseElement(DatabasePlayerObject.OPERATOR).getAsBoolean()) {
				gameChest.sendNoPermissionMessage(sender);
				return;
			}
			p.setOp(true);
			p.sendMessage(gameChest.prefix + "§7§oDu bist nun Operator!");
        }, DatabasePlayerObject.OPERATOR);
		return true;
	}
}
