package de.gamechest.fakeplayer;

import de.gamechest.GameChest;
import de.gamechest.reflector.PacketHandleListener;
import de.gamechest.reflector.Reflection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_9_R2.CancelledPacketHandleException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by ByteList on 02.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class FakePlayerPacketHandleListener extends PacketHandleListener {
    @Override
    public void write(Player player, ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) {
    }

    @Override
    public void channelRead(Player player, ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        String packet = object.getClass().getSimpleName();
        if (packet.equalsIgnoreCase("PacketPlayInUseEntity")) {
            int id = (int) Reflection.getFieldValue(object, "a");
            try {
                ArrayList<FakePlayer> fakePlayers = GameChest.getInstance().getFakePlayerManager().getFakePlayers(player.getUniqueId());
                fakePlayers.forEach(fakePlayer -> {
                    if(fakePlayer.getEntityId() == id && !fakePlayer.isInteracted()) {
                        try {
                            FakePlayerInteractEvent fakePlayerInteractEvent = new FakePlayerInteractEvent(fakePlayer, player,
                                    FakePlayerInteractEvent.Action.valueOf(Reflection.getFieldValue(object, "action").toString()));
                            Bukkit.getPluginManager().callEvent(fakePlayerInteractEvent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (CancelledPacketHandleException ex) {
                System.err.println("["+this.getClass().getName()+"] CancelledPacketHandleException caught:");
                ex.getCause().printStackTrace();
            } catch (Exception ex) {
                System.err.println("["+this.getClass().getName()+"] Exception caught:");
                ex.printStackTrace();
            }
        }
    }
}
