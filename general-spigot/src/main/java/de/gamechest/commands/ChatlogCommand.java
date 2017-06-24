package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.chatlog.ChatLog;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatlogCommand implements CommandExecutor {

	private ChatLog chatLog = GameChest.getInstance().getChatLog();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("chatlog")) {
			if(!(sender instanceof Player)) {
				String report = args[0];
				Player r = Bukkit.getPlayer(report);
				
				if(r == null) {
					sender.sendMessage(chatLog.prefix + "§cDieser Spieler ist nicht online!");
					return true;
				}

				chatLog.createLog(sender, r.getName());
				return true;
			}
			Player p = (Player) sender;

			if(args.length != 1) {
				sender.sendMessage(chatLog.prefix+"§c/de.gamechest.chatlog <Spieler>");
				return true;
			}
			
			String report = args[0];
			Player r = Bukkit.getPlayer(report);
			
			if(r == p) {
				sender.sendMessage(chatLog.prefix + "§cDu kannst dich nicht selber loggen!");
				return true;
			}
			
			if(r == null) {
				sender.sendMessage(chatLog.prefix + "§cDieser Spieler ist nicht online!");
				return true;
			}

			chatLog.createLog(p, r.getName());
			return true;
		}
		return true;
	}
}
