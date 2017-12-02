package de.gamechest.listener;

import org.bukkit.event.EventHandler;
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

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage().substring(1).split(" ")[0];

        if(command.contains(":")) {
            e.setMessage(command.split(":")[1]);
        }
    }

    @EventHandler
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
                .filter(cmd ->!cmd.contains("fakeplugins"))
                .filter(cmd ->!cmd.contains("trigger"))
                .forEach(retur::add);

        e.setCompletions(retur);
    }
}
