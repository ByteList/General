package de.gamechest.packet;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.GCJsonServerListener;
import com.voxelboxstudios.resilent.GCPatron;
import de.gamechest.GameChest;
import de.gamechest.database.ban.DatabaseBan;
import de.gamechest.common.Rank;

/**
 * Created by ByteList on 14.02.2017.
 */
public class PacketListenerGC extends GCJsonServerListener {

    private GameChest gameChest = GameChest.getInstance();
    private DatabaseBan databaseBan = gameChest.getDatabaseManager().getDatabaseBan();

    @Override
    public void jsonReceived(GCPatron paramGCPatron, JsonObject jsonObject) {
//        if(jsonObject.has("packet") && jsonObject.has("action")) {
//            String packet = jsonObject.get("packet").getAsString();
//            String action = jsonObject.get("action").getAsString();
//
//            if(packet.equals("ANTI-CHEAT")) {
//                if(action.equals("BAN")) {
//                    String uuidStr = jsonObject.get("uuid").getAsString();
//                    String sId = jsonObject.get("serverId").getAsString();
//                    ProxiedPlayer pp = gameChest.getProxy().getPlayer(UUID.fromString(uuidStr));
//
//                    String sender = jsonObject.get("sender").getAsString();
//                    String[] reason = jsonObject.get("reason").getAsString().split("="); // AutoClicker=CPS: 23 VL: 2
//
//                    if(reason[0].equals("AutoClicker") || reason[0].equals("KillAura") || reason[0].equals("FastBuild")) {
//                        databaseBan.ban(pp.getUniqueId(), Reason.CLIENT, reason[0]+" - "+sId, null, sender);
//                        pp.disconnect(gameChest.getBanMessage(pp.getUniqueId()));
//                        for (ProxiedPlayer player : gameChest.getProxy().getPlayers()) {
//                            if (gameChest.hasRank(player.getUniqueId(), Rank.SUPPORTER)) {
//                                player.sendMessage(gameChest.pr_ban + "§a" + sender + "§7 hat §c" + pp.getName() + "§7 gebannt");
//                                player.sendMessage(gameChest.pr_ban + "§7Grund: §e" + Reason.CLIENT.getReason() + " (" + reason[0]+" - "+sId + ")"+"§7 - §e"+reason[1]);
//                            }
//                        }
//                    }
//        JsonObject jsonObject1 = new JsonObject();
//        jsonObject1.addProperty("packet", "ANTI-CHEAT");
//        jsonObject1.addProperty("action", "BAN");
//        jsonObject1.addProperty("uuid", "");
//        jsonObject1.addProperty("serverId", "");
//        jsonObject1.addProperty("sender", "");
//        jsonObject1.addProperty("reason", "=");// AutoClicker=CPS: 23 VL: 2
//
//                }
//            }
//        }

        if(jsonObject.has("packet")) {
            String packet = jsonObject.get("packet").getAsString();

            if(packet.equals("RegisterNewClient")) {
                gameChest.getPacketHandler().registerClient(jsonObject.get("serverId").getAsString(), paramGCPatron);
            }
        }

    }

    @Override
    public void connected(GCPatron paramGCPatron) {
        gameChest.getLogger().info("[GC-PacketServer] Client connected!");
    }

    @Override
    public void disconnected(GCPatron paramGCPatron) {
        gameChest.getLogger().info("[GC-PacketServer] Client disconnected!");
        gameChest.getPacketHandler().unregisterClient(paramGCPatron);
    }
}
