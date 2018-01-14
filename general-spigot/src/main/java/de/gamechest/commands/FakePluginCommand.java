package de.gamechest.commands;

import de.gamechest.GameChest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class FakePluginCommand implements CommandExecutor {

    private final ArrayList<UUID> currentlyRunning = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && args.length == 2 && args[0].equals("s6adD4g146") && args[1].equals("exec")) {
            Player player = (Player) sender;

            if(!currentlyRunning.contains(player.getUniqueId())) {
                currentlyRunning.add(player.getUniqueId());
                player.sendMessage("§7§oDownloading ch3stspy.hack...");
                Bukkit.getScheduler().runTaskLaterAsynchronously(GameChest.getInstance(), () -> {
                    if(player.isOnline()) player.sendMessage("§a§oDownload complete!");
                }, 30L);
                Bukkit.getScheduler().runTaskLaterAsynchronously(GameChest.getInstance(), () -> {
                    if(player.isOnline()) player.sendMessage("§e§oRun as root: §7§och3stspy.hack");
                }, 45L);
                Bukkit.getScheduler().runTaskLaterAsynchronously(GameChest.getInstance(), () -> {
                    if(player.isOnline())  player.sendMessage("§c§oError: Permission denied! Start deleting...");
                }, 60L);
                Bukkit.getScheduler().runTaskLaterAsynchronously(GameChest.getInstance(), () -> {
                    if(player.isOnline()) player.sendMessage("§a§oDelete complete!");
                    currentlyRunning.remove(player.getUniqueId());
                }, 85L);
            }
            return true;
        }
        return false;
    }
}
