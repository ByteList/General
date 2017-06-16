package de.gamechest.packet;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.GCJsonClientListener;
import org.bukkit.Bukkit;

/**
 * Created by ByteList on 14.02.2017.
 */
public class PacketListener extends GCJsonClientListener {
    @Override
    public void jsonReceived(JsonObject paramJsonObject) {}

    @Override
    public void disconnected() {}

    @Override
    public void connected() {
        Bukkit.getLogger().info("[GC-PacketServer] Connected as client!");
    }
}
