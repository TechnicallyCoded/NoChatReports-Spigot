package com.tcoded.nochatreports.nms.v1_20_4;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.PlayerChatPacket;
import io.netty.buffer.ByteBuf;

public class NMS_v1_20_4 extends NmsProvider {

    @Override
    public PlayerChatPacket wrapChatPacket(ByteBuf byteBuf) {
        return new PlayerChatPacketImpl(byteBuf);
    }

}