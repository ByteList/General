package de.gamechest.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ByteList on 30.11.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage().substring(1).split(" ")[0];

        if(command.contains(":")) {
            e.setMessage(command.split(":")[1]);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent e) {
        if(!e.getBuffer().startsWith("/")) {
            return;
        }

        List<String> retur = new ArrayList<>();
        List<String> list = e.getCompletions();
        list.stream()
                .filter(cmd -> !cmd.contains(":"))
                .filter(cmd -> !cmd.contains("viaver"))
                .filter(cmd -> !cmd.contains("vvbukkit"))
                .filter(cmd -> !cmd.contains("fakeplugins"))
                .filter(cmd -> !cmd.contains("trigger"))
                .filter(cmd -> !cmd.contains("map"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/p"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/plot"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/ps"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/plotsquared"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/p2"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/2"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/plotme"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//pos2"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//pos1"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//1"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//2"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/posa"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/posb"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//posa"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//posb"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/brush"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/sp"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/superpickaxe"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//superpickaxe"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//gui"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//help"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//schematic"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//anvil"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//toggleplace"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/targetmask"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/tarmask"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/tool"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/transforms"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/tm"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/to"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/targetoffset"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/snap"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/snapshot"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/primary"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/vis"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/visual"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/visualize"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/tool"))
                .filter(cmd -> !cmd.equalsIgnoreCase("/fawe"))
                .filter(cmd -> !cmd.equalsIgnoreCase("//fawe"))
                .forEach(retur::add);

        e.setCompletions(retur);
    }
}
