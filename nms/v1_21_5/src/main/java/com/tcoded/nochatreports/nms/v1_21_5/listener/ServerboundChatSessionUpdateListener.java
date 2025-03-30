package com.tcoded.nochatreports.nms.v1_21_5.listener;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.event.ChatSessionUpdateEvent;
import com.tcoded.nochatreports.nms.listener.PacketListener;
import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import org.bukkit.entity.Player;

public class ServerboundChatSessionUpdateListener implements PacketListener<ServerboundChatSessionUpdatePacket> {

    private final NmsProvider<?> nms;

    public ServerboundChatSessionUpdateListener(NmsProvider<?> plugin) {
        this.nms = plugin;
    }

    @Override
    public PacketWriteResult<ServerboundChatSessionUpdatePacket> onPacketSend(Player player, ServerboundChatSessionUpdatePacket packet) {
        ChatSessionUpdateEvent event = new ChatSessionUpdateEvent();
        event.callEvent();

        return new PacketWriteResult<>(!event.isCancelled(), packet);
    }

    @Override
    public Class<ServerboundChatSessionUpdatePacket> getPacketClass() {
        return ServerboundChatSessionUpdatePacket.class;
    }

}
