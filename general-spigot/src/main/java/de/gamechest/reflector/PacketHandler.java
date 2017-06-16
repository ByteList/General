package de.gamechest.reflector;

import de.gamechest.GameChest;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

public class PacketHandler
        extends ChannelDuplexHandler {
    private Player p;

    public PacketHandler(Player p) {
        this.p = p;
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
            throws Exception {
        for (PacketHandleListener packetHandleListener : GameChest.getInstance().getPacketInjector().getListeners()) {
            packetHandleListener.write(this.p, ctx, msg, promise);
        }
//        System.out.println(msg.getClass().getName());
        super.write(ctx, msg, promise);
    }

    public void channelRead(ChannelHandlerContext c, Object m)
            throws Exception {
        for (PacketHandleListener packetHandleListener : GameChest.getInstance().getPacketInjector().getListeners()) {
            packetHandleListener.channelRead(this.p, c, m);
        }
//        if(m.getClass().getSimpleName().contains("PacketPlayInPositionLook")) {
//            PacketPlayInFlying.PacketPlayInPositionLook packetPlayInPositionLook = (PacketPlayInFlying.PacketPlayInPositionLook) m;
//            System.out.println(Reflection.getField(packetPlayInPositionLook));
//        }
        super.channelRead(c, m);
    }
}
