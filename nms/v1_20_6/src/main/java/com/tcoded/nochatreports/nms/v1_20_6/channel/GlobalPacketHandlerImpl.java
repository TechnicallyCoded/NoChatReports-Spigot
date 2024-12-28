package com.tcoded.nochatreports.nms.v1_20_6.channel;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;

import java.util.ArrayList;

public class GlobalPacketHandlerImpl extends GlobalPacketHandler {

    public GlobalPacketHandlerImpl(NmsProvider<?> nms) {
        super(nms);
    }

    public boolean write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if (packet instanceof ClientboundBundlePacket bundlePacket) {
            // Un-bundle the packet
            handleBundlePacket(ctx.channel(), bundlePacket);

            // If the bundle packet is empty, return false
            return bundlePacket.subPackets().iterator().hasNext();
        }
        else {
            return handleAnyPacket(ctx.channel(), packet);
        }
    }

    private void handleBundlePacket(Channel channel, ClientboundBundlePacket bundlePacket) {
//        System.out.println("(Bundle packet)");
        ArrayList<Packet<?>> toRemove = new ArrayList<>();

        for (Packet<?> subPacket : bundlePacket.subPackets()) {
//            System.out.println("  - " + subPacket.getClass().getSimpleName());
            if (!handleAnyPacket(channel, subPacket)) {
                toRemove.add(subPacket);
            }
        }

        // Prevent ConcurrentModificationException
        if (bundlePacket.subPackets() instanceof ArrayList<?> list) {
            //noinspection SuspiciousMethodCalls
            list.removeAll(toRemove);
        }
    }

}
