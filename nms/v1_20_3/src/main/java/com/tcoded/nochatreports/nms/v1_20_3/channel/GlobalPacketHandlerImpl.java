package com.tcoded.nochatreports.nms.v1_20_3.channel;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;

import java.util.ArrayList;
import java.util.Iterator;

public class GlobalPacketHandlerImpl extends GlobalPacketHandler {

    public GlobalPacketHandlerImpl(NmsProvider<?> nms) {
        super(nms);
    }

    public PacketWriteResult<?> write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if (packet instanceof ClientboundBundlePacket bundlePacket) {
            // Un-bundle the packet
            handleBundlePacket(ctx.channel(), bundlePacket);

            boolean keep = bundlePacket.subPackets().iterator().hasNext();
            return new PacketWriteResult<>(keep, bundlePacket);
        }
        else {
            return handleAnyPacket(ctx.channel(), packet);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handleBundlePacket(Channel channel, ClientboundBundlePacket bundlePacket) {
        Iterable<Packet<ClientGamePacketListener>> list = bundlePacket.subPackets();
        Iterator<Packet<ClientGamePacketListener>> iterator = list.iterator();

        int index = 0;
        while (iterator.hasNext()) {
            Packet<?> subPacket = iterator.next();

            PacketWriteResult<?> result = handleAnyPacket(channel, subPacket);

            if (list instanceof ArrayList arrList) {
                Object resultPacket = result.packet();
                if (!result.keep()) iterator.remove();
                else if (!resultPacket.equals(subPacket)) arrList.set(index, resultPacket);
            }

            index++;
        }
    }

}
