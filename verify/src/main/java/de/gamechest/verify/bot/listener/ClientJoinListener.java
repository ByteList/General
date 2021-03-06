package de.gamechest.verify.bot.listener;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotListener;

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
                "  Willkommen auf dem Game-ChestPrefix.de Teamspeak.\n" +
                "  Nutze [COLOR=red]!help[/COLOR], um alle Befehle zu sehen.\n" +
                "[B][/B]");

        Verify.getInstance().getTeamspeakBot().checkSupport();
    }
}
