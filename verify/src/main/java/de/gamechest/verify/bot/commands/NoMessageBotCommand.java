package de.gamechest.verify.bot.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotCommand;
import de.gamechest.verify.bot.TeamspeakBot;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class NoMessageBotCommand extends BotCommand {

    private final TeamspeakBot teamspeakBot = Verify.getInstance().getTeamspeakBot();

    public NoMessageBotCommand(TS3ApiAsync apiAsync) {
        super(apiAsync, "nomsg", "Aktiviere/Deaktiviere die private Nachrichten");
    }

    @Override
    public void execute(String invokerUniqueId, Integer invokerId, String[] args) {
        teamspeakBot.getClientInfoAsync(invokerId, clientInfo -> {
            if (!teamspeakBot.hasSpecialGroup(clientInfo)) {
                apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
                return;
            }
            if(!clientInfo.isInServerGroup(teamspeakBot.noMessageServerGroupId)) {
                apiAsync.addClientToServerGroup(teamspeakBot.noMessageServerGroupId, clientInfo.getDatabaseId());
                apiAsync.sendPrivateMessage(invokerId, "[COLOR=#FF005F]Du kannst nun nicht mehr angeschrieben werden![/COLOR]");
            } else {
                apiAsync.removeClientFromServerGroup(teamspeakBot.noMessageServerGroupId, clientInfo.getDatabaseId());
                apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun wieder angeschrieben werden![/COLOR]");
            }
        }, ex -> {
            ex.printStackTrace();
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
        });
    }
}
