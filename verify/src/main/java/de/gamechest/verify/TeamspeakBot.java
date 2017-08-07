package de.gamechest.verify;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.database.DatabasePlayerObject;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Logger;

import static de.gamechest.util.Java15Compat.Arrays_copyOfRange;

/**
 * Created by ByteList on 01.05.2017.
 */
public class TeamspeakBot {

    private final GameChest gameChest = GameChest.getInstance();
    private final Logger logger = Verify.getInstance().getLogger();

    public final int noPokeServerGroupId = 53;
    public final int noMessageServerGroupId = 54;
    private final int notifyServerGroupId = 46;
    public final int verifyServerGroupId = 55;
    private final int[] ids = {11, 12, 49, 13, 14, 29, 16, 55};

    private int queryId;

    @Getter
    private TS3Query query;
    @Getter
    private TS3Api api;
    @Getter
    private TS3ApiAsync apiAsync;

    TeamspeakBot() {
        logger.info("[Teamspeak] Try to connect...");

        TS3Config config = new TS3Config();
        config.setHost("127.0.0.1");
        config.setQueryPort(10011);
        config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
        query = new TS3Query();
        query.connect();

        if(!query.isConnected()) {
            logger.info("[Teamspeak] Query can not connect!");
            return;
        } else {
            logger.info("[Teamspeak] Query connected!");
        }

        api = query.getApi();
        apiAsync = query.getAsyncApi();
        api.login("teamspeakQueryBot", "hg63Afdp");


        api.selectVirtualServerById(1);
        api.setNickname("ChestBot");
        queryId = api.whoAmI().getId();
        api.registerEvents(TS3EventType.SERVER, TS3EventType.TEXT_PRIVATE);

        api.addTS3Listeners(new TS3EventAdapter() {

            @Override
            public void onClientJoin(ClientJoinEvent e) {
                final int clientId = e.getClientId();

                apiAsync.sendPrivateMessage(clientId, "[B][/B]\n[B][/B]\n" +
                        "  Willkommen auf dem Game-Chest.de Teamspeak.\n" +
                        "  Nutze [COLOR=red]!help[/COLOR], um alle Befehle zu sehen.\n" +
                        "[B][/B]");
            }

            @Override
            public void onTextMessage(TextMessageEvent e) {
                if (e.getTargetMode() == TextMessageTargetMode.CLIENT && e.getInvokerId() != queryId) {
                    int invokerId = e.getInvokerId();
                    ClientInfo clientInfo;
                    if (!e.getMessage().startsWith("!")) {
                        apiAsync.sendPrivateMessage(invokerId, "Deine Eingabe ist kein Befehl!");
                        return;
                    }

                    String[] msg;
                    String cmd;
                    String[] args;
                    if (e.getMessage().contains(" ")) {
                        msg = e.getMessage().split(" ");

                        cmd = msg[0];

                        args = Arrays_copyOfRange(msg, 1, msg.length);
                    } else {
                        cmd = e.getMessage();
                        args = new String[0];
                    }

                    boolean letPass = false;

                    switch (cmd) {
                        case "!help":
                            apiAsync.sendPrivateMessage(invokerId, "[B][/B]\n[B][/B]\n" +
                                    "  Mit [COLOR=red]!nopoke[/COLOR] kannst du NoPoke aktivieren | deaktivieren.\n" +
                                    "  Mit [COLOR=red]!nomsg[/COLOR] kannst du NoMessage aktivieren | deaktivieren.\n" +
                                    "  Mit [COLOR=red]!verify[/COLOR] kannst du dich mit deinem Minecraft-Account verbinden.\n" +
                                    "[B][/B]");
                            break;
                        case "!nopoke":
                            clientInfo = getClientInfo(invokerId);
                            if (clientInfo == null) {
                                logger.warning("[Teamspeak] ClientInfo is null! id: " + invokerId);
                                return;
                            }
                            for (int serverGroupId : ids) {
                                if (clientInfo.isInServerGroup(serverGroupId)) {
                                    letPass = true;
                                    break;
                                }
                            }
                            if (!letPass) {
                                apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
                                return;
                            }

                            if (!clientInfo.isInServerGroup(noPokeServerGroupId)) {
                                apiAsync.addClientToServerGroup(noPokeServerGroupId, clientInfo.getDatabaseId());
                                apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun nicht mehr angestupst werden![/COLOR]");
                            } else {
                                apiAsync.removeClientFromServerGroup(noPokeServerGroupId, clientInfo.getDatabaseId());
                                apiAsync.sendPrivateMessage(invokerId, "[COLOR=GREEN]Du kannst nun wieder angestupst werden![/COLOR]");
                            }
                            break;
                        case "!nomsg":
                            clientInfo = getClientInfo(invokerId);
                            if (clientInfo == null) {
                                logger.warning("[Teamspeak] ClientInfo is null! id: " + invokerId);
                                return;
                            }
                            for (int serverGroupId : ids) {
                                if (clientInfo.isInServerGroup(serverGroupId)) {
                                    letPass = true;
                                    break;
                                }
                            }
                            if (!letPass) {
                                apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
                                return;
                            }

                            if (!clientInfo.isInServerGroup(noMessageServerGroupId)) {
                                apiAsync.addClientToServerGroup(noMessageServerGroupId, clientInfo.getDatabaseId());
                                apiAsync.sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun nicht mehr angeschrieben werden![/COLOR]");
                            } else {
                                apiAsync.removeClientFromServerGroup(noMessageServerGroupId, clientInfo.getDatabaseId());
                                apiAsync.sendPrivateMessage(invokerId, "[COLOR=GREEN]Du kannst nun wieder angeschrieben werden![/COLOR]");
                            }
                            break;
                        case "!verify":
                            if (args.length != 1) {
                                apiAsync.sendPrivateMessage(invokerId, "Benutzung: [B]!verify <Minecraft-Name>[/B]");
                                return;
                            }
                            String name = args[0];

                            UUID uuid = UUIDFetcher.getUUID(name);

                            if (uuid == null) {
                                apiAsync.sendPrivateMessage(invokerId, "Konnte den User nicht in der Datenbank finden!");
                                return;
                            }

                            Player player = Verify.getInstance().getServer().getPlayer(uuid);

                            if(player == null) {
                                apiAsync.sendPrivateMessage(invokerId, "Du musst auf dem Verify-Server online sein! Verbinde dich dazu auf [B]Game-Chest.de[/B] und führe den [B]/verify[/B] Befehl aus.");
                                return;
                            }

                            String ipPlayer = player.getAddress().getHostString();
                            clientInfo = getClientInfo(invokerId);
                            if (clientInfo == null) {
                                logger.warning("[Teamspeak] ClientInfo is null! id: " + invokerId);
                                return;
                            }
                            String ipTs = clientInfo.getIp();

                            if(!ipPlayer.equals(ipTs)) {
                                apiAsync.sendPrivateMessage(invokerId, "Der Minecraft Account ist nicht über dem gleichen PC online!");
                                return;
                            }

                            gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                                if (dbPlayer.getDatabaseElement(DatabasePlayerObject.TS_UID).getObject() != null) {
                                    apiAsync.sendPrivateMessage(invokerId, "Du bist bereits mit einer Identität verbunden!");
                                    return;
                                }

                                String uid = clientInfo.getUniqueIdentifier();

                                dbPlayer.setDatabaseObject(DatabasePlayerObject.TS_UID, uid);
                                apiAsync.addClientToServerGroup(verifyServerGroupId, clientInfo.getDatabaseId());
                                apiAsync.sendPrivateMessage(invokerId, "Du bist nun mit dem Minecraft-Account [COLOR=GREEN]" + name + "[/COLOR] verbunden!");
                                player.sendMessage(Verify.getInstance().prefix+"§aDu bist nun mit deinem Teamspeak-Account verbunden!");

                            }, DatabasePlayerObject.TS_UID);
                            break;
                        default:
                            apiAsync.sendPrivateMessage(invokerId, "Unbekannter Befehl!");
                            break;
                    }
                }
            }
        });
    }


    private ClientInfo getClientInfo(int clientId) {
        try {
            return apiAsync.getClientInfo(clientId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
