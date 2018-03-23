package de.gamechest.verify.bot;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import static de.gamechest.util.Java15Compat.Arrays_copyOfRange;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CommandManager {
    private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);

    @Getter
    private HashMap<String, BotCommand> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
    }

    public void registerCommand(BotCommand command) {
        this.commands.put(command.getName(), command);
    }

    public void registerCommands(BotCommand... commands) {
        Arrays.stream(commands).forEach(botCommand -> this.commands.put(botCommand.getName(), botCommand));
    }

    public BotCommand getCommand(String command) {
        if(command.contains(" "))
            command = command.split(" ")[0];
        return commands.getOrDefault(command, null);
    }

    public boolean existsCommand(String command) {
        if(command.contains(" "))
            command = command.split(" ")[0];
        return commands.containsKey(command);
    }

    public boolean dispatchCommand(String invokerUniqueId, int invokerId, String commandLine) {
        String[] args = PATTERN_ON_SPACE.split(commandLine);

        if (args.length == 0) {
            return false;
        }

        String sentCommandLabel = args[0].toLowerCase();
        BotCommand target = getCommand(sentCommandLabel);

        if (target == null) {
            return false;
        }
        System.out.println("dispatchCommand()->"+invokerId);
        target.execute(invokerUniqueId, invokerId, Arrays_copyOfRange(args, 1, args.length));

        return true;
    }
}
