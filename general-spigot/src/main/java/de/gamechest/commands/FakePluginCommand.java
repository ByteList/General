package de.gamechest.commands;

import de.gamechest.GameChest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FakePluginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && args.length == 2 && args[0].equals("s6adD4g146") && args[1].equals("exec")) {
            sender.sendMessage("§7§oDownloading ch3stspy.hack...");
            Bukkit.getScheduler().runTaskLaterAsynchronously(GameChest.getInstance(), () -> sender.sendMessage("§a§oDownload complete!"), 30L);
            Bukkit.getScheduler().runTaskLaterAsynchronously(GameChest.getInstance(), () -> sender.sendMessage("§e§oRun as root: §7§och3stspy.hack"), 45L);
            Bukkit.getScheduler().runTaskLaterAsynchronously(GameChest.getInstance(), () -> sender.sendMessage("§c§oError: Permission denied! Start deleting..."), 60L);
            Bukkit.getScheduler().runTaskLaterAsynchronously(GameChest.getInstance(), () -> sender.sendMessage("§a§oDelete complete!"), 85L);
            return true;
        }
        return false;
    }
}
