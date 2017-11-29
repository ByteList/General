package de.gamechest.verify.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClientInfo;
import de.gamechest.GameChest;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.verify.Verify;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by ByteList on 29.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class UnverifyCommand implements CommandExecutor {

    private final GameChest gameChest = GameChest.getInstance();
    private final Verify verify = Verify.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            gameChest.getDatabaseManager().getAsync().getPlayer(player.getUniqueId(), dbPlayer -> {
                if (dbPlayer.getDatabaseElement(DatabasePlayerObject.TS_UID).getObject() == null) {
                    player.sendMessage(verify.prefix + "§cDu bist mit keinem Teamspeak-Account verbunden!");
                    return;
                }
                player.sendMessage(verify.prefix + "§7Verbindung wird getrennt...");

                String uId = dbPlayer.getDatabaseElement(DatabasePlayerObject.TS_UID).getAsString();

                dbPlayer.setDatabaseObject(DatabasePlayerObject.TS_UID, null);
                try {
                    TS3ApiAsync apiAsync = verify.getTeamspeakBot().getApiAsync();
                    DatabaseClientInfo databaseClientInfo = apiAsync.getDatabaseClientByUId(uId).get();

                    apiAsync.removeClientFromServerGroup(verify.getTeamspeakBot().verifyServerGroupId, databaseClientInfo.getDatabaseId());
                    apiAsync.removeClientFromServerGroup(verify.getTeamspeakBot().noMessageServerGroupId, databaseClientInfo.getDatabaseId());
                    apiAsync.removeClientFromServerGroup(verify.getTeamspeakBot().noPokeServerGroupId, databaseClientInfo.getDatabaseId());

                    player.sendMessage(verify.prefix + "§aDie Verbindung wurde erfolgreich getrennt.");
                    ClientInfo clientInfo = verify.getTeamspeakBot().getApiAsync().getClientByUId(uId).get();
                    if(clientInfo != null) {
                        apiAsync.sendPrivateMessage(clientInfo.getId(), "Die Verbindung zu deinem Minecraft-Account wurde aufgelöst.");
                        String description = clientInfo.getDescription();
                        if(description.startsWith("Minecraft: ")) {
                            description = description.replace("Minecraft: ", "");
                            String[] desArgs = description.split(" ");
                            if(desArgs.length > 1) {
                                description = description.replace(desArgs[0]+" |", "");
                                if(description.startsWith(" ")) {
                                    description = description.replaceFirst(" ", "");
                                }
                                apiAsync.editClient(clientInfo.getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, description));
                            } else {
                                apiAsync.editClient(clientInfo.getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, ""));
                            }

                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    player.sendMessage(verify.prefix + "§cError: " + e.getMessage());
                }
            });
        }
        return true;
    }
}
