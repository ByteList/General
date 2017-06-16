package com.voxelboxstudios.resilent;

import com.google.gson.JsonObject;

public abstract class GCJsonClientListener {
    public abstract void jsonReceived(JsonObject paramJsonObject);

    public abstract void disconnected();

    public abstract void connected();
}
