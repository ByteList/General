package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Created by ByteList on 29.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ChatListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();
    private final String[] plot = { "/party",
            "/p","/plot","/ps","/plotsquared","/p2","/2","/plotme","/plots",
            "//pos2", "//pos1", "//1", "//2", "/posa", "/posb", "//posa", "//posb", "/brush",
            "/sp", "/superpickaxe", "//superpickaxe", "//gui", "//help", "//schematic", "//anvil",
            "//toggleplace", "/targetmask", "/tarmask", "/tool", "/transforms", "/tm", "/to", "/targetoffset",
            "/snap", "/snapshot", "/primary", "/vis", "/visual", "/visualize", "/tool", "/fawe", "//fawe", "/we",
            "/worldedit", "//worldedit", "/toggleplace", "/patterns"
    };

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent e) {
        if(e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) e.getSender();
            String message = e.getMessage();
            if(message.startsWith("/")) {
                String command = message.split(" ")[0];

                if(command.startsWith("/minecraft:")) {
                    e.setMessage(message.replace("/minecraft:", "/"));
                    command = command.replace("/minecraft:", "/");
                }

                if(command.startsWith("/bukkit:")) {
                    e.setMessage(message.replace("/bukkit:", "/"));
                    command = command.replace("/bukkit:", "/");
                }

                if(command.startsWith("/spigot:")) {
                    e.setMessage(message.replace("/spigot:", "/"));
                    command = command.replace("/spigot:", "/");
                }

                if(command.equalsIgnoreCase("/pl") || command.equalsIgnoreCase("/plugins")) {
                    if(!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
//                        e.setMessage("/fakeplugins s6adD4g146 exec");
                        e.setCancelled(true);
                        player.sendMessage("§fPlugins (6): §aAAC§f, §aPlotMe§f, §aWorldGuard§f, §cAuthMe§f, §aEssentials§f, §aPermissionsEx§f, §eCloud");
                        return;
                    }
                }

                if(player.getServer().getInfo().getName().equalsIgnoreCase("BauEvent")) {
                    if(command.startsWith("/mv")) {
                        if (!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                            e.setCancelled(true);
                            gameChest.sendNoPermissionMessage(player);
                            return;
                        }
                    }
                    for (String plotCmd : plot) {
                        if (command.equalsIgnoreCase(plotCmd)) {
                            if (!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                                e.setCancelled(true);
                                gameChest.sendNoPermissionMessage(player);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
