package com.tcoded.nochatreports.nms.v1_21_4;

import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
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
