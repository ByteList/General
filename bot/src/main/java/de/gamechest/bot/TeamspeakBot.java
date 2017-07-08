package de.gamechest.bot;

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
import de.gamechest.bot.launcher.BotLauncher;
import de.gamechest.bot.launcher.console.BotLogger;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import lombok.Getter;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by ByteList on 01.05.2017.
 */
public class TeamspeakBot {

    private Logger logger;

    private final int noPokeServerGroupId = 53;
    private final int noMessageServerGroupId = 54;
    private final int notifyServerGroupId = 46;
    private final int[] ids = {11, 12, 49, 13, 14, 29, 16, 55};

    @Getter
    private TS3Query query;
    @Getter
    private TS3Api api;

    @Getter
    private Thread thread;

    public TeamspeakBot() {
        final int[] connectState = {0};
        thread = new Thread(() -> {
            logger = BotLogger.getLogger();
            logger.info("[Teamspeak] Try to connect...");

            final TS3Config config = new TS3Config();
            config.setHost("79.133.45.202");
            config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
            try {
                query = new TS3Query();
                query.connect();
                connectState[0] = 1;
            } catch (Throwable throwable) {
                connectState[0] = 2;
                query = null;
            }
        }, "TeamspeakBot-Thread");
        thread.start();

        int millis = 0;

        while (connectState[0] == 0 && thread.isAlive()) {
            millis++;
        }


        if(connectState[0] == 2 || query == null) {
            logger.info("[Teamspeak] Query can not connect ("+ connectState[0] +"::"+millis+")!");
            return;
        } else {
            logger.info("[Teamspeak] Query connected!");
        }

        api = query.getApi();
        api.login("teamspeakQueryBot", "Fg7WRb85");


        api.selectVirtualServerById(1);
        api.setNickname("ChestBot");
        api.registerEvent(TS3EventType.SERVER);
        api.registerEvent(TS3EventType.TEXT_PRIVATE);

        api.addTS3Listeners(new TS3EventAdapter() {

            @Override
            public void onClientJoin(ClientJoinEvent e) {
                final int clientId = e.getClientId();

                final TS3ApiAsync apiAsync = query.getAsyncApi();
                ClientInfo clientInfo = null;
                if (getClientInfo(clientId) != null)
                    clientInfo = getClientInfo(clientId);

                if (clientInfo == null) return;

                if (!clientInfo.isInServerGroup(12))
                    return;

                apiAsync.sendPrivateMessage(clientId, "[B][/B]");
                apiAsync.sendPrivateMessage(clientId, "Willkommen auf dem Game-Chest.de Teamspeak.");
                apiAsync.sendPrivateMessage(clientId, "Nutze [COLOR=red]!help[/COLOR], um alle Befehle zu sehen.");
                apiAsync.sendPrivateMessage(clientId, "[B][/B]");
            }

            @Override
            public void onTextMessage(TextMessageEvent e) {
                final int invokerId = e.getInvokerId();
                if (e.getTargetMode() == TextMessageTargetMode.CLIENT) {
                    final ClientInfo clientInfo = getClientInfo(invokerId);
                    if (clientInfo == null) {
                        logger.warning("[Teamspeak] ClientInfo is null! id: " + invokerId);
                        return;
                    }

                    if (!e.getMessage().startsWith("!")) {
                        query.getAsyncApi().sendPrivateMessage(invokerId, "Deine Eingabe ist kein Befehl!");
                        return;
                    }

                    String[] msg;
                    String cmd;
                    String[] args;
                    if (e.getMessage().contains(" ")) {
                        msg = e.getMessage().split(" ");

                        cmd = msg[0];

                        args = new String[msg.length - 1];
                        System.arraycopy(msg, 0, args, msg.length - 1, msg.length);
                    } else {
                        cmd = e.getMessage();
                        args = new String[0];
                    }

                    if (cmd.equalsIgnoreCase("!help")) {
                        query.getAsyncApi().sendPrivateMessage(invokerId, "Mit [COLOR=red]!nopoke[/COLOR] kannst du NoPoke aktivieren | deaktivieren.");
                        query.getAsyncApi().sendPrivateMessage(invokerId, "Mit [COLOR=red]!nomsg[/COLOR] kannst du NoMessage aktivieren | deaktivieren.");
                        query.getAsyncApi().sendPrivateMessage(invokerId, "Mit [COLOR=red]!verify[/COLOR] kannst du dich mit deinem Minecraft-Account verbinden.");
                        return;
                    }

                    if (cmd.equalsIgnoreCase("!nopoke")) {
                        boolean letPass = false;
                        for (int serverGroupId : ids) {
                            if (clientInfo.isInServerGroup(serverGroupId)) {
                                letPass = true;
                                break;
                            }
                        }
                        if (!letPass) {
                            query.getAsyncApi().sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
                            return;
                        }

                        if (clientInfo.isInServerGroup(noPokeServerGroupId)) {
                            query.getAsyncApi().addClientToServerGroup(noPokeServerGroupId, invokerId);
                            query.getAsyncApi().sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun nicht mehr angestupst werden![/COLOR]");
                        } else {
                            query.getAsyncApi().removeClientFromServerGroup(noPokeServerGroupId, invokerId);
                            query.getAsyncApi().sendPrivateMessage(invokerId, "[COLOR=yellow]Du kannst nun wieder angestupst werden![/COLOR]");
                        }

                        return;
                    }

                    if (cmd.equalsIgnoreCase("!nomsg")) {
                        boolean letPass = false;
                        for (int serverGroupId : ids) {
                            if (clientInfo.isInServerGroup(serverGroupId)) {
                                letPass = true;
                                break;
                            }
                        }
                        if (!letPass) {
                            query.getAsyncApi().sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
                            return;
                        }

                        if (clientInfo.isInServerGroup(noMessageServerGroupId)) {
                            query.getAsyncApi().addClientToServerGroup(noMessageServerGroupId, invokerId);
                            query.getAsyncApi().sendPrivateMessage(invokerId, "[COLOR=green]Du kannst nun nicht mehr angeschrieben werden![/COLOR]");
                        } else {
                            query.getAsyncApi().removeClientFromServerGroup(noMessageServerGroupId, invokerId);
                            query.getAsyncApi().sendPrivateMessage(invokerId, "[COLOR=yellow]Du kannst nun wieder angeschrieben werden![/COLOR]");
                        }
                        return;
                    }

                    if (cmd.equalsIgnoreCase("!verify")) {
                        if (args.length != 1) {
                            query.getAsyncApi().sendPrivateMessage(invokerId, "Benutzung: [B]!verify <Minecraft-Name>[/B]");
                            return;
                        }
                        String name = args[0];
                        final DatabaseManager databaseManager = BotLauncher.getDatabaseManager();

                        UUID uuid = databaseManager.getDatabaseUuidBuffer().getUUID(name);

                        if (uuid == null) {
                            query.getAsyncApi().sendPrivateMessage(invokerId, "Konnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        DatabasePlayer databasePlayer = new DatabasePlayer(BotLauncher.getDatabaseManager(), uuid);

                        if (databasePlayer.getDatabaseElement(DatabasePlayerObject.TS_UID).getObject() != null) {
                            query.getAsyncApi().sendPrivateMessage(invokerId, "Der User ist bereits mit einer Identität verbunden!");
                            return;
                        }

                        String uid = clientInfo.getUniqueIdentifier();

                        databasePlayer.setDatabaseObject(DatabasePlayerObject.TS_UID, uid);
                        query.getAsyncApi().sendPrivateMessage(invokerId, "Du hast dich erfolgreich mit dem Minecraft-Account [COLOR=yellow]" + name + "[/COLOR] verbunden!");
                        return;

                    }

                    query.getAsyncApi().sendPrivateMessage(invokerId, "Unbekannter Befehl!");
                }
            }
        });
    }

    private ClientInfo getClientInfo(int id) {
        return query.getApi().getClientInfo(id);
    }

    public boolean logout() {
        return true;
//        try {
//            boolean b = this.api.logout();
//            if (b) this.query.exit();
//            return b;
//        } catch (Exception ignored) {
//            return false;
//        }
    }
}
