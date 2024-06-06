package com.tcoded.nochatreports.nms.v1_20_3;

import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

public class SystemChatPacketImpl implements SystemChatPacket {

    private final ClientboundSystemChatPacket packet;

    public SystemChatPacketImpl(ClientboundSystemChatPacket packet) {
        this.packet = packet;
    }

    @Override
    public Object toNmsPacket() {
        return packet;
    }

}
