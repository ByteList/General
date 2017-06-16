package com.voxelboxstudios.resilent;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

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
            GCPatron localGCPatron = new GCPatron(this.server, localSocket);
            Iterator localIterator = this.server.getListeners().iterator();
            while (localIterator.hasNext()) {
                GCJsonServerListener localGCJsonServerListener = (GCJsonServerListener) localIterator.next();
                localGCJsonServerListener.connected(localGCPatron);
            }
        }
    }
}
