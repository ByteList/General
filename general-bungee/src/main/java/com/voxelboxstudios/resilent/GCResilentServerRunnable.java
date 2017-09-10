package com.voxelboxstudios.resilent;

import java.io.IOException;
import java.net.Socket;

public class GCResilentServerRunnable
        implements Runnable {
    private GCResilentServer server;

    public GCResilentServerRunnable(GCResilentServer paramGCResilentServer) {
        this.server = paramGCResilentServer;
    }

    public void run() {
        while (this.server.getSocket() != null) {
            Socket localSocket = null;
            try {
                localSocket = this.server.getSocket().accept();
            } catch (IOException localIOException) {
                continue;
            }
            GCPatron localPatron = new GCPatron(this.server, localSocket);
            for (GCJsonServerListener jsonServerListener : this.server.getListeners()) {
                jsonServerListener.connected(localPatron);
            }
        }
    }
}
