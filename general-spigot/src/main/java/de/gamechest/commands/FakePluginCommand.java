package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.database.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FakePluginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && args.length == 2 && args[0].equals("s6adD4g146") && args[1].equals("exec")) {
            Player player = (Player) sender;

            if(GameChest.getInstance().hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                StringBuilder pluginList = new StringBuilder();
                Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

                for (Plugin plugin : plugins) {
                    if (pluginList.length() > 0) {
                        pluginList.append(ChatColor.WHITE);
                        pluginList.append(", ");
                    }
                    pluginList.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
                    pluginList.append(plugin.getDescription().getName());
                }

                player.sendMessage("Plugins (" + plugins.length + "): " + pluginList.toString());
                return true;
            }

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
