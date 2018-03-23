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
public class NoPokeBotCommand extends BotCommand {

    private final TeamspeakBot teamspeakBot;

    public NoPokeBotCommand(TS3ApiAsync apiAsync) {
        super(apiAsync, "nopoke", "Aktiviere/Deaktiviere Anstupser");
        teamspeakBot = Verify.getInstance().getTeamspeakBot();
    }

    @Override
    public void execute(String invokerUniqueId, Integer invokerId, String[] args) {
        System.out.println("NoPokeBotCommand#execute()->"+invokerId);
        System.out.println(teamspeakBot.toString());
        System.out.println("Bla");

        ClientInfo clientInfo = teamspeakBot.getClientInfo(invokerId);

        if(clientInfo == null) {
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
            return;
        }

        if (!teamspeakBot.hasSpecialGroup(clientInfo)) {
            apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
            return;
        }
        if(!clientInfo.isInServerGroup(teamspeakBot.noPokeServerGroupId)) {
            apiAsync.addClientToServerGroup(teamspeakBot.noPokeServerGroupId, clientInfo.getDatabaseId());
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=yellow]Du kannst nun nicht mehr angestupst werden![/COLOR]");
        } else {
            apiAsync.removeClientFromServerGroup(teamspeakBot.noPokeServerGroupId, clientInfo.getDatabaseId());
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun wieder angestupst werden![/COLOR]");
        }

//        teamspeakBot.getClientInfoAsync(invokerId, clientInfo -> {
//            if(!clientInfo.isInServerGroup(teamspeakBot.noPokeServerGroupId)) {
//                apiAsync.addClientToServerGroup(teamspeakBot.noPokeServerGroupId, clientInfo.getDatabaseId());
//                apiAsync.sendPrivateMessage(invokerId, "[COLOR=yellow]Du kannst nun nicht mehr angestupst werden![/COLOR]");
//            } else {
//                apiAsync.removeClientFromServerGroup(teamspeakBot.noPokeServerGroupId, clientInfo.getDatabaseId());
//                apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun wieder angestupst werden![/COLOR]");
//            }
//        }, e -> {
//            e.printStackTrace();
//            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
//        });
    }
}
