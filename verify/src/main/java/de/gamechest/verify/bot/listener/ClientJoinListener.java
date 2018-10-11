package de.gamechest.verify.bot.listener;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.gamechest.AsyncTasks;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotListener;
import de.gamechest.verify.bot.TeamspeakBot;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ClientJoinListener extends BotListener {

    private final TS3ApiAsync apiAsync;

    public ClientJoinListener(TS3ApiAsync apiAsync) {
        this.apiAsync = apiAsync;
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        final int clientId = e.getClientId();

        apiAsync.sendPrivateMessage(clientId, "[B][/B]\n[B][/B]\n" +
                "  Willkommen auf dem Game-Chest.de Teamspeak.\n" +
                "  Nutze [COLOR=red]!help[/COLOR], um alle Befehle zu sehen.\n" +
                "[B][/B]");

        Verify.getInstance().getTeamspeakBot().checkSupport();
    }
}
