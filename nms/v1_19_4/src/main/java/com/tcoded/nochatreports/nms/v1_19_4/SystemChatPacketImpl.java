package com.tcoded.nochatreports.nms.v1_19_4;

import com.tcoded.nochatreports.nms.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

public class SystemChatPacketImpl implements SystemChatPacket {

    private final ClientboundSystemChatPacket packet;

    public SystemChatPacketImpl(ClientboundSystemChatPacket packet) {
        this.packet = packet;
    }

    @Override
    public ByteBuf toByteBuf() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(buf);

        return buf;
    }
}
