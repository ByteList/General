package de.gamechest.verify.bot.listener;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotListener;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TextMessageListener extends BotListener {

    private final TS3ApiAsync apiAsync;
    private final int queryId;

    public TextMessageListener(TS3ApiAsync apiAsync, int queryId) {
        this.apiAsync = apiAsync;
        this.queryId = queryId;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        if (e.getTargetMode() == TextMessageTargetMode.CLIENT && e.getInvokerId() != queryId) {
            int invokerId = e.getInvokerId();
            if (e.getMessage().startsWith("!")) {
                if(!Verify.getInstance().getTeamspeakBot().getCommandManager().dispatchCommand(invokerId, e.getMessage().replaceFirst("!", ""))) {
                    apiAsync.sendPrivateMessage(invokerId, "Unbekannter Befehl! Liste dir alle Befehle mit [B]!help[/B] auf.");
                }
                return;
            }

            apiAsync.sendPrivateMessage(invokerId, "Deine Eingabe ist kein Befehl!");
        }
    }
}
