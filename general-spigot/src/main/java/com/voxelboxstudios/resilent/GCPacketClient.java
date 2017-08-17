package com.voxelboxstudios.resilent;

import com.google.gson.JsonObject;
import de.gamechest.packet.PacketListener;
import org.bukkit.Bukkit;

import java.io.IOException;

public class GCPacketClient {
    private static GCResilentClient client;

    public GCPacketClient() {
        String address = "127.0.0.1";
        int port = 4234;

        client = new GCResilentClient();
        client.addListener(new PacketListener());

        try {
            client.connect(address, port);
        } catch (IOException e) {
            if(!Bukkit.getServerName().contains("nonBungee")) {
                System.err.println("Can't connect to bungee!\n" +
                        "If you haven't a bungee online you can add \"nonBungee\" to your server-name in " +
                        "your server.properties file.\n" +
                        "If you can't find server-name in your server.properties add the following line:\n\n"+
                        "server-name=nonBungee-server\n\n"+
                        "You wouldn't get this exception with this in your server-name.\n");
            }
        }

    }

    public static GCResilentClient getClient() {
        return client;
    }

    public static void sendPacket(JsonObject paramJsonObject) {
        try {
            client.sendPacket(paramJsonObject);
        } catch (Exception e) {
            if(!Bukkit.getServerName().contains("nonBungee")) {
                System.err.println("Error while sending packet to bungee: "+e.getMessage());
            }
        }
    }

    public static void start() {
        new GCPacketClient();
    }
}
