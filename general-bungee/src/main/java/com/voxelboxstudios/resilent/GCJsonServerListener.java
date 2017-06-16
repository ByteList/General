package com.voxelboxstudios.resilent;

import com.google.gson.JsonObject;

public abstract class GCJsonServerListener {
    public abstract void jsonReceived(GCPatron paramGCPatron, JsonObject paramJsonObject);

    public abstract void connected(GCPatron paramGCPatron);

    public abstract void disconnected(GCPatron paramGCPatron);
}
