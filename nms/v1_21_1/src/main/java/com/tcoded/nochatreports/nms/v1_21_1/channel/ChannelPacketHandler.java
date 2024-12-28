package com.tcoded.nochatreports.nms.v1_21_1.channel;

import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ChannelPacketHandler extends ChannelDuplexHandler {

    private final GlobalPacketHandlerImpl globalPacketHandler;

    public ChannelPacketHandler(GlobalPacketHandler globalPacketHandler) {
        this.globalPacketHandler = (GlobalPacketHandlerImpl) globalPacketHandler;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        // Cancel the packet?
        if (!globalPacketHandler.write(ctx, packet, promise)) {
            return;
        }

        // Else, write the packet
        super.write(ctx, packet, promise);
    }

}
