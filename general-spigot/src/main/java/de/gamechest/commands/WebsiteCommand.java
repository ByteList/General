package de.gamechest.commands;

import de.gamechest.GameChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 15.04.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class WebsiteCommand implements CommandExecutor {

    private final GameChest gameChest = GameChest.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Nur als Spieler nutzbar!");
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 1) {
            String address = args[0];

            gameChest.getDatabaseManager().getAsync().getPlayer(player.getUniqueId(), dbPlayer -> {
            });

            if(!address.contains("@") || !address.contains(".")) {
                sender.sendMessage(gameChest.prefix+"§cUngültige Email-Adresse!");
                return true;
            }
        }

        sender.sendMessage(gameChest.prefix+"§c/website <Email-Adresse> §7- §eRegistriere dich auf unserer Website");
        return true;
    }
}
