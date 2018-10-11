package de.gamechest.verify.bot.listener;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotListener;

/**
 * Created by nemmerich on 11.10.2018.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class ClientLeaveListener extends BotListener {

    private final TS3ApiAsync apiAsync;

    public ClientLeaveListener(TS3ApiAsync apiAsync) {
        this.apiAsync = apiAsync;
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        Verify.getInstance().getTeamspeakBot().checkSupport();
    }
}
