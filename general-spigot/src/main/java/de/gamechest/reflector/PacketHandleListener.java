package de.gamechest.reflector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

public abstract class PacketHandleListener {
    public abstract void write(Player player, ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) throws Exception;

    public abstract void channelRead(Player player, ChannelHandlerContext channelHandlerContext, Object object) throws Exception;
}
