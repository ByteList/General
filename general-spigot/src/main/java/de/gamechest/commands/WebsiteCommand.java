package de.gamechest.commands;

import de.gamechest.GameChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 15.04.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class WebsiteCommand implements CommandExecutor {

    private final GameChest gameChest = GameChest.getInstance();

    private final HashMap<UUID, String> cache = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Nur als Spieler nutzbar!");
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 2 && cache.containsKey(player.getUniqueId())) {
            if(args[0].equalsIgnoreCase("accept")) {
                String code = args[1];

                if(code.equals("06TEbT84TLvlMZb")) {
                    gameChest.getDatabaseManager().getAsync().getWebRegister(database -> {
                        if(!database.existsUser(player.getUniqueId())) {
                            String[] str = cache.get(player.getUniqueId()).split(";");
                            database.register(player.getUniqueId(), str[1], str[0]);
                            sender.sendMessage(gameChest.prefix+"§aÜberprüfe nun dein Email-Postfach (auch den Spam-Ordner) und fahre mit der Registrierung fort.");
                        } else {
                            sender.sendMessage(gameChest.prefix+"§7Du hast bereits ein Account.");
                        }
                    });
                    return true;
                }
                sender.sendMessage(gameChest.prefix+"§cUngültiger Code!");
                return true;
            }
        }

        if(args.length == 1) {
            if(cache.containsKey(player.getUniqueId())) {
                sender.sendMessage("§8\u00BB §7Zum Bestätigen: §6/website accept <Code>");
                sender.sendMessage("§8\u00BB §cZum Ablehnen: /website deny");
                return true;
            }
            String address = args[0];

            gameChest.getDatabaseManager().getAsync().getWebRegister(database -> {
                if(!database.existsUser(player.getUniqueId())) {

                    if(!address.contains("@") || !address.contains(".")) {
                        sender.sendMessage(gameChest.prefix+"§cUngültige Email-Adresse!");
                        return;
                    }

                    String verifyCode = gameChest.random(20);
                    cache.put(player.getUniqueId(), verifyCode+";"+address);
                    sender.sendMessage("§8\u00BB §7Damit du die Registrierung fortsetzen kannst, musst du unsere §eDatenschutzerklärung §7und §eAGB §7lesen und bestätigen.");
                    sender.sendMessage("§8\u00BB §7Zum Bestätigen: §6/website accept <Code>");
                    sender.sendMessage("§8\u00BB §cZum Ablehnen: /website deny");
                } else {
                    sender.sendMessage(gameChest.prefix+"§7Du hast bereits ein Account.");
                }
            });
            return true;
        }

        sender.sendMessage(gameChest.prefix+"§c/website <Email-Adresse> §7- §eRegistriere dich auf unserer Website");
        return true;
    }
}
