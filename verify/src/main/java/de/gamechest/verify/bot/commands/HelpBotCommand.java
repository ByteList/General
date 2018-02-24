package de.gamechest.verify.bot.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotCommand;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class HelpBotCommand extends BotCommand {


    public HelpBotCommand(TS3ApiAsync apiAsync) {
        super(apiAsync, "help", "Zeigt dir alle Befehl an");
    }

    @Override
    public void execute(Integer invokerId, String[] args) {
        StringBuilder message = new StringBuilder();
        message.append("Alle Befehle:\n\n");

        for (BotCommand botCommand : Verify.getInstance().getTeamspeakBot().getCommandManager().getCommands().values()) {
            message.append("[COLOR=red]!").append(botCommand.getName()).append("[/COLOR] - ").append(botCommand.getDescription()).append("\n");
        }

        apiAsync.sendPrivateMessage(invokerId, message.toString());
    }
}
