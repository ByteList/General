package de.gamechest.verify.bot.commands;

import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotCommand;
import de.gamechest.verify.bot.TeamspeakBot;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class NoPokeBotCommand extends BotCommand {

    private final TeamspeakBot teamspeakBot = Verify.getInstance().getTeamspeakBot();

    public NoPokeBotCommand() {
        super("nopoke", "Aktiviere/Deaktiviere Anstupser");
    }

    @Override
    public void execute(Integer invokerId, String[] args) {
        if (!teamspeakBot.hasSpecialGroup(invokerId)) {
            apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
            return;
        }
        teamspeakBot.getClientInfoAsync(invokerId, clientInfo -> {
            if(!clientInfo.isInServerGroup(teamspeakBot.noPokeServerGroupId)) {
                apiAsync.addClientToServerGroup(teamspeakBot.noPokeServerGroupId, clientInfo.getDatabaseId());
                apiAsync.sendPrivateMessage(invokerId, "[COLOR=yellow]Du kannst nun nicht mehr angestupst werden![/COLOR]");
            } else {
                apiAsync.removeClientFromServerGroup(teamspeakBot.noPokeServerGroupId, clientInfo.getDatabaseId());
                apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun wieder angestupst werden![/COLOR]");
            }
        }, e -> {
            e.printStackTrace();
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
        });
    }
}
