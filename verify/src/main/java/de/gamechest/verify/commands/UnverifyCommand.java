package de.gamechest.verify.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.gamechest.GameChest;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.verify.Verify;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    ClientInfo clientInfo = verify.getTeamspeakBot().getApiAsync().getClientByUId(uId).get();
                    TS3ApiAsync apiAsync = verify.getTeamspeakBot().getApiAsync();
                    apiAsync.removeClientFromServerGroup(verify.getTeamspeakBot().verifyServerGroupId, clientInfo.getDatabaseId());
                    if (clientInfo.isInServerGroup(verify.getTeamspeakBot().noMessageServerGroupId))
                        apiAsync.removeClientFromServerGroup(verify.getTeamspeakBot().noMessageServerGroupId, clientInfo.getDatabaseId());
                    if (clientInfo.isInServerGroup(verify.getTeamspeakBot().noPokeServerGroupId))
                        apiAsync.removeClientFromServerGroup(verify.getTeamspeakBot().noPokeServerGroupId, clientInfo.getDatabaseId());

                    apiAsync.sendPrivateMessage(clientInfo.getId(), "Die Verbindung zu deinem Minecraft-Account wurde aufgelöst.");
                    player.sendMessage(verify.prefix + "§aDie Verbidung wurde erfolgreich getrennt.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    player.sendMessage(verify.prefix + "§cError: " + e.getMessage());
                }
            });
        }
        return true;
    }
}
