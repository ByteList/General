package de.gamechest.verify.bot.listener;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import de.gamechest.common.AsyncTasks;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotListener;
import de.gamechest.verify.bot.TeamspeakBot;

/**
 * Created by ByteList on 27.10.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ClientMoveListener extends BotListener {

    private final TeamspeakBot teamspeakBot = Verify.getInstance().getTeamspeakBot();
    private final TS3ApiAsync apiAsync;

    public ClientMoveListener(TS3ApiAsync apiAsync) {
        this.apiAsync = apiAsync;
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        int invokerId = e.getClientId();

        AsyncTasks.getInstance().runTaskAsync(()-> {
            if(teamspeakBot.getSupportMemberIds().contains(invokerId)) {
                teamspeakBot.getSupportMemberIds().remove(invokerId);
            }
        });
    }
}
