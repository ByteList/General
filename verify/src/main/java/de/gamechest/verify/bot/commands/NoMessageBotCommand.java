package de.gamechest.verify.bot.commands;

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

    public NoMessageBotCommand() {
        super("nomsg", "Aktiviere/Deaktiviere die private Nachrichten");
    }

    @Override
    public void execute(Integer invokerId, String[] args) {
        if (!teamspeakBot.hasSpecialGroup(invokerId)) {
            apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
            return;
        }
        teamspeakBot.getClientInfoAsync(invokerId, clientInfo -> {
            if(!clientInfo.isInServerGroup(teamspeakBot.noMessageServerGroupId)) {
                apiAsync.addClientToServerGroup(teamspeakBot.noMessageServerGroupId, clientInfo.getDatabaseId());
                apiAsync.sendPrivateMessage(invokerId, "[COLOR=yellow]Du kannst nun nicht mehr angeschrieben werden![/COLOR]");
            } else {
                apiAsync.removeClientFromServerGroup(teamspeakBot.noMessageServerGroupId, clientInfo.getDatabaseId());
                apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun wieder angeschrieben werden![/COLOR]");
            }
        }, e -> {
            e.printStackTrace();
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
        });
    }
}
