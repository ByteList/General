package de.gamechest.reflector;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PacketInjector {
    public HashMap<PacketHandler, Player> PACKET_HANLER = new HashMap<>();
    public HashMap<Player, PacketHandler> PLAYER_PACKET_HANDLER = new HashMap<>();
    private List<PacketHandleListener> listeners = new ArrayList<>();
    private Field EntityPlayer_playerConnection;
    private Class<?> PlayerConnection;
    private Field PlayerConnection_networkManager;
    private Class<?> NetworkManager;
    private Field k;
    private Field m;

    public PacketInjector() {
        try {
            this.EntityPlayer_playerConnection = Reflection.getField(Reflection.getClass("{nms}.EntityPlayer"), "playerConnection");

            this.PlayerConnection = Reflection.getClass("{nms}.PlayerConnection");
            this.PlayerConnection_networkManager = Reflection.getField(this.PlayerConnection, "networkManager");

            this.NetworkManager = Reflection.getClass("{nms}.NetworkManager");
            for (Field fields : this.NetworkManager.getFields()) {
                if (fields.getType().equals(Channel.class)) {
                    this.k = fields;
                }
            }
            if (this.k == null) {
                this.k = Reflection.getField(this.NetworkManager, "i");
            }
            this.m = Reflection.getField(this.NetworkManager, "m");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public void addPlayer(Player p) {
        try {
            Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
            if (ch.pipeline().get("PacketInjector") == null) {
                PacketHandler h = new PacketHandler(p);
                ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
                this.PLAYER_PACKET_HANDLER.put(p, h);
                this.PACKET_HANLER.put(h, p);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void removePlayer(Player p) {
        if (this.PLAYER_PACKET_HANDLER.containsKey(p)) {
            try {
                Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
                if (ch.pipeline().get("PacketInjector") != null) {
                    ch.pipeline().remove("PacketInjector");
                    this.PACKET_HANLER.remove(this.PLAYER_PACKET_HANDLER.get(p));
                    this.PLAYER_PACKET_HANDLER.remove(p);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void z(Player p) {
        this.PACKET_HANLER.remove(this.PLAYER_PACKET_HANDLER.get(p));
        this.PLAYER_PACKET_HANDLER.remove(p);
    }


    private Object getNetworkManager(Object ep) {
        try {
            return Reflection.getFieldValue(this.PlayerConnection_networkManager, (Object) Reflection.getFieldValue(this.EntityPlayer_playerConnection, ep));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Channel getChannel(Object networkManager) {
        Channel ch;
        try {
            ch = Reflection.getFieldValue(this.k, networkManager);
        } catch (Exception e) {
            ch = Reflection.getFieldValue(this.m, networkManager);
        }
        return ch;
    }

    public List<PacketHandleListener> getListeners() {
        return this.listeners;
    }

    public boolean registerListener(PacketHandleListener packetHandleListener) {
        return (!this.listeners.contains(packetHandleListener)) && (this.listeners.add(packetHandleListener));
    }

    public boolean unregisterListener(PacketHandleListener packetHandleListener) {
        return (this.listeners.contains(packetHandleListener)) && (this.listeners.remove(packetHandleListener));
    }

    public void unregisterListeners() {
        List<PacketHandleListener> local = new ArrayList<>();
        local.addAll(this.listeners);
        for (PacketHandleListener packetHandleListener : local) {
            unregisterListener(packetHandleListener);
        }
    }
}
