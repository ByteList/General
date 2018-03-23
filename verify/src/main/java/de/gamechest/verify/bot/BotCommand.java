package de.gamechest.verify.bot;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import lombok.Getter;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class BotCommand {

    protected final TS3ApiAsync apiAsync;

    @Getter
    private final String name;
    @Getter
    private final String description;


    public BotCommand(TS3ApiAsync apiAsync, String name, String description) {
        this.apiAsync = apiAsync;
        this.name = name;
        this.description = description;
    }

    public abstract void execute(String invokerUniqueId, Integer invokerId, String[] args);
}
