package de.gamechest.verify.bot.listener;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import de.gamechest.verify.bot.BotListener;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ClientJoinListener extends BotListener {

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        final int clientId = e.getClientId();

        apiAsync.sendPrivateMessage(clientId, "[B][/B]\n[B][/B]\n" +
                "  Willkommen auf dem Game-Chest.de Teamspeak.\n" +
                "  Nutze [COLOR=red]!help[/COLOR], um alle Befehle zu sehen.\n" +
                "[B][/B]");
    }
}
