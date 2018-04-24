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

            gameChest.getDatabaseManager().getAsync().getWebRegister(database -> {
                if(!database.existsUser(player.getUniqueId())) {
                    if(!address.contains("@") || !address.contains(".")) {
                        sender.sendMessage(gameChest.prefix+"§cUngültige Email-Adresse!");
                        return;
                    }

                    String verifyCode = gameChest.random(50);
                    database.register(player.getUniqueId(), address, verifyCode);
                    sender.sendMessage(gameChest.prefix+"§aÜberprüfe nun dein Email-Postfach (auch den Spam-Ordner) und fahre mit der Registrierung fort.");
                } else {
                    sender.sendMessage(gameChest.prefix+"§7Du hast bereits ein Account.");
                }
            });
            return true;
        }

        sender.sendMessage(gameChest.prefix+"§c/website <Email-Adresse>");
        return true;
    }
}
