package com.voxelboxstudios.resilent;

import com.google.gson.JsonObject;
import de.gamechest.packet.PacketListener;

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
            e.printStackTrace();
        }

    }

    public static GCResilentClient getClient() {
        return client;
    }

    public static void sendPacket(JsonObject paramJsonObject) {
        try {
            client.sendPacket(paramJsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void start() {
        new GCPacketClient();
    }
}
