package com.voxelboxstudios.resilent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Iterator;

public class GCResilentPatronRunnable
        implements Runnable {
    private GCPatron GCPatron;

    public GCResilentPatronRunnable(GCPatron paramGCPatron) {
        this.GCPatron = paramGCPatron;
    }

    public void run() {
        while (this.GCPatron.getReader() != null) {
            String str = null;
            try {
                str = this.GCPatron.getReader().readLine();
            } catch (IOException localIOException1) {
                this.GCPatron.disconnect();
            }
            if (str != null) {
                JsonParser localJsonParser = new JsonParser();
                JsonObject localJsonObject = null;
                try {
                    if ((localJsonParser.parse(str) instanceof JsonObject)) {
                        localJsonObject = (JsonObject) localJsonParser.parse(str);
                    }
                } catch (JsonSyntaxException localJsonSyntaxException) {
                    continue;
                }
                if (localJsonObject != null) {
                    Iterator localIterator = this.GCPatron.getServer().getListeners().iterator();
                    while (localIterator.hasNext()) {
                        GCJsonServerListener localGCJsonServerListener = (GCJsonServerListener) localIterator.next();
                        localGCJsonServerListener.jsonReceived(this.GCPatron, localJsonObject);
                    }
                }
            } else {
                this.GCPatron.disconnect();
            }
        }
    }
}
