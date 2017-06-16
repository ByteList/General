package com.voxelboxstudios.resilent;

import de.gamechest.packet.PacketListenerGC;

import java.io.IOException;

public class GCPacketServer {

    private GCPacketServer(int paramInt) {
        GCResilentServer GCResilentServer = new GCResilentServer();
        GCResilentServer.addListener(new PacketListenerGC());
        try {
            GCResilentServer.start(paramInt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void start() {
        new GCPacketServer(4234);
    }
}
