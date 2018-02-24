package de.gamechest.verify.bot;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import de.gamechest.verify.Verify;
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


    public BotCommand(String name, String description) {
        this.name = name;
        this.description = description;

        this.apiAsync = Verify.getInstance().getTeamspeakBot().getApiAsync();
    }

    public abstract void execute(Integer invokerId, String[] args);
}
