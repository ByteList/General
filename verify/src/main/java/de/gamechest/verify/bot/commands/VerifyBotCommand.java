package de.gamechest.verify.bot.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import de.gamechest.GameChest;
import de.gamechest.common.UUIDFetcher;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotCommand;
import de.gamechest.verify.bot.TeamspeakBot;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class VerifyBotCommand extends BotCommand {

    private final TeamspeakBot teamspeakBot = Verify.getInstance().getTeamspeakBot();
    private final GameChest gameChest = GameChest.getInstance();

    public VerifyBotCommand(TS3ApiAsync apiAsync) {
        super(apiAsync, "verify", "Verbinde dich mit deinem Minecraft-Account");
    }

    @Override
    public void execute(String invokerUniqueId, Integer invokerId, String[] args) {
        if (args.length != 1) {
            apiAsync.sendPrivateMessage(invokerId, "Benutzung: [B]!verify <Minecraft-Name>[/B]");
            return;
        }
        String name = args[0];
        teamspeakBot.getClientInfoAsync(invokerId, clientInfo -> {
            UUID uuid = UUIDFetcher.getUUID(name);

            if (uuid == null) {
                apiAsync.sendPrivateMessage(invokerId, "Konnte den User nicht in der Datenbank finden!");
                return;
            }

            Player player = Verify.getInstance().getServer().getPlayer(uuid);

            if(player == null) {
                apiAsync.sendPrivateMessage(invokerId, "Du musst auf dem Verify-Server online sein! Verbinde dich dazu auf [B]Game-ChestPrefix.de[/B] und führe den [B]/verify[/B] Befehl aus.");
                return;
            }

            String ipPlayer = player.getAddress().getHostString();
            String ipTs = clientInfo.getIp();

            if(!ipPlayer.equals(ipTs)) {
                apiAsync.sendPrivateMessage(invokerId, "Der Minecraft-Account ist nicht über dem gleichen PC online!");
                return;
            }

            gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                if (dbPlayer.getDatabaseElement(DatabasePlayerObject.TS_UID).getObject() != null) {
                    apiAsync.sendPrivateMessage(invokerId, "Du bist bereits mit einer Identität verbunden!");
                    return;
                }

                String uid = clientInfo.getUniqueIdentifier();

                dbPlayer.setDatabaseObject(DatabasePlayerObject.TS_UID, uid);
                apiAsync.addClientToServerGroup(teamspeakBot.verifyServerGroupId, clientInfo.getDatabaseId());
                String description = "Minecraft: "+name + (clientInfo.getDescription().equals("") ? "" : " | " + clientInfo.getDescription());
                apiAsync.editClient(invokerId, Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, description));

                apiAsync.sendPrivateMessage(invokerId, "Du bist nun mit dem Minecraft-Account [COLOR=GREEN]" + name + "[/COLOR] verbunden!");
                player.sendMessage(Verify.getInstance().prefix+"§aDu bist nun mit deinem Teamspeak-Account verbunden!");

            }, DatabasePlayerObject.TS_UID);
        }, e -> {
            e.printStackTrace();
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
        });
    }
}
