package com.tcoded.nochatreports.nms.v26_3.channel;

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

public class GlobalPacketHandlerImpl extends GlobalPacketHandler {

    public GlobalPacketHandlerImpl(NmsProvider<?> nms) {
        super(nms);
    }

    public PacketWriteResult<?> write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if (packet instanceof ClientboundBundlePacket bundlePacket) {
            ArrayList<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();
            for (Packet<? super ClientGamePacketListener> subPacket : bundlePacket.subPackets()) {
                PacketWriteResult<?> result = handleAnyPacket(ctx.channel(), subPacket);
                if (result.keep()) {
                    @SuppressWarnings("unchecked")
                    Packet<? super ClientGamePacketListener> rewritten =
                            (Packet<? super ClientGamePacketListener>) result.packet();
                    packets.add(rewritten);
                }
            }
            return new PacketWriteResult<>(!packets.isEmpty(), new ClientboundBundlePacket(packets));
        }
        else {
            return handleAnyPacket(ctx.channel(), packet);
        }
    }

}
