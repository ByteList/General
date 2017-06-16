package de.gamechest.commands.base;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by ByteList on 21.03.2017.
 */
public abstract class GCCommand extends Command {

    public GCCommand(String name) {
        super(name);
    }

    public GCCommand(String name, String... aliases) {
        super(name, null, aliases);
    }

    public abstract void execute(CommandSender sender, String[] args);
}
