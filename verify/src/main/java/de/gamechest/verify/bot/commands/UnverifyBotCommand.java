package de.gamechest.verify.bot.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import de.gamechest.verify.bot.BotCommand;

/**
 * Created by ByteList on 23.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class UnverifyBotCommand extends BotCommand {

    public UnverifyBotCommand(TS3ApiAsync apiAsync) {
        super(apiAsync, "unverify", "Unverify nur auf dem MC-Server");
    }

    @Override
    public void execute(String invokerUniqueId, Integer invokerId, String[] args) {
        apiAsync.sendPrivateMessage(invokerId, "Um die Verbindung zum Minecraft-Account aufzulösen, musst du auf dem Verify-Server [B]/unverify[/B] ausführen.");
    }
}
