package de.gamechest.reflector;

        import io.netty.channel.ChannelHandlerContext;
        import io.netty.channel.ChannelPromise;
        import org.bukkit.entity.Player;

public abstract class PacketHandleListener {
    public abstract void write(Player paramPlayer, ChannelHandlerContext paramChannelHandlerContext, Object paramObject, ChannelPromise paramChannelPromise)
            throws Exception;

    public abstract void channelRead(Player paramPlayer, ChannelHandlerContext paramChannelHandlerContext, Object paramObject)
            throws Exception;
}
