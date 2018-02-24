package de.gamechest.verify.bot.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
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
    public void execute(Integer invokerId, String[] args) {
        if (!teamspeakBot.hasSpecialGroup(invokerId)) {
            apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
            return;
        }
        ClientInfo clientInfo;
        try {
            clientInfo = apiAsync.getClientInfo(invokerId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
            return;
        }
        if(!clientInfo.isInServerGroup(teamspeakBot.noMessageServerGroupId)) {
            apiAsync.addClientToServerGroup(teamspeakBot.noMessageServerGroupId, clientInfo.getDatabaseId());
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=yellow]Du kannst nun nicht mehr angeschrieben werden![/COLOR]");
        } else {
            apiAsync.removeClientFromServerGroup(teamspeakBot.noMessageServerGroupId, clientInfo.getDatabaseId());
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun wieder angeschrieben werden![/COLOR]");
        }
//        teamspeakBot.getClientInfoAsync(invokerId, clientInfo -> {
//            if(!clientInfo.isInServerGroup(teamspeakBot.noMessageServerGroupId)) {
//                apiAsync.addClientToServerGroup(teamspeakBot.noMessageServerGroupId, clientInfo.getDatabaseId());
//                apiAsync.sendPrivateMessage(invokerId, "[COLOR=yellow]Du kannst nun nicht mehr angeschrieben werden![/COLOR]");
//            } else {
//                apiAsync.removeClientFromServerGroup(teamspeakBot.noMessageServerGroupId, clientInfo.getDatabaseId());
//                apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun wieder angeschrieben werden![/COLOR]");
//            }
//        }, e -> {
//            e.printStackTrace();
//            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
//        });
    }
}
