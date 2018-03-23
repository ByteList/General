package de.gamechest.verify.bot.commands;

import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotCommand;
import de.gamechest.verify.bot.TeamspeakBot;

/**
 * Created by ByteList on 23.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class UnverifyBotCommand extends BotCommand {

    private final TeamspeakBot teamspeakBot = Verify.getInstance().getTeamspeakBot();

    public UnverifyBotCommand() {
        super(null, "unverify", "Unverify nur auf dem MC-Server");
    }

    @Override
    public void execute(String invokerUniqueId, Integer invokerId, String[] args) {
        apiAsync.sendPrivateMessage(invokerId, "Um die Verbindung zum Minecraft-Account aufzulösen, musst du auf dem Verify-Server [B]/unverify[/B] ausführen.");
    }
}
