package de.gamechest.verify.bot.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.gamechest.AsyncTasks;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotCommand;
import de.gamechest.verify.bot.TeamspeakBot;

/**
 * Created by ByteList on 27.10.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class SupportBotCommand extends BotCommand {

    private final TeamspeakBot teamspeakBot = Verify.getInstance().getTeamspeakBot();

    public SupportBotCommand(TS3ApiAsync apiAsync) {
        super(apiAsync, "verify", "Verbinde dich mit deinem Minecraft-Account");
    }

    @Override
    public void execute(String invokerUniqueId, Integer invokerId, String[] args) {
        AsyncTasks.getInstance().runTaskAsync(()-> {
            ClientInfo client = teamspeakBot.getClientInfo(invokerId);
            if(!teamspeakBot.hasSupportNotifyGroup(client)) {
                apiAsync.sendPrivateMessage(invokerId, "Du hast keine Berechtigung für diesen Befehl!");
                return;
            }

            if(teamspeakBot.getSupportMemberIds().contains(invokerId)) {
                teamspeakBot.getSupportMemberIds().remove(invokerId);
                apiAsync.sendPrivateMessage(invokerId, "Du hast dich als supportendes Teammitglied ausgetragen!");
                return;
            }
            teamspeakBot.getSupportMemberIds().add(invokerId);
            apiAsync.sendPrivateMessage(invokerId, "Du hast dich als supportendes Teammitglied registriert!");
        });
    }
}
