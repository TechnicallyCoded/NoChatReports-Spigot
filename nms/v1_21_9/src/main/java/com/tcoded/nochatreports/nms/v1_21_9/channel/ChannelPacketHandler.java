package com.tcoded.nochatreports.nms.v1_21_9.channel;

import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;

public class ChannelPacketHandler extends ChannelDuplexHandler {

    private final GlobalPacketHandlerImpl globalPacketHandler;

    public ChannelPacketHandler(GlobalPacketHandler globalPacketHandler) {
        this.globalPacketHandler = (GlobalPacketHandlerImpl) globalPacketHandler;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        // Cancel the packet?
        PacketWriteResult<?> result = globalPacketHandler.write(ctx, packet, promise);
        if (!result.keep()) {
            return;
        }

        // Else, write the packet
        super.write(ctx, result.packet(), promise);
    }

}
