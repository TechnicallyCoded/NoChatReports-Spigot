package com.tcoded.nochatreports.nms.v1_20_4.listener;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.event.SecureChatNotificationPacketEvent;
import com.tcoded.nochatreports.nms.listener.PacketListener;
import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import org.bukkit.entity.Player;

public class ClientboundServerStatusListener implements PacketListener<ClientboundStatusResponsePacket> {

    private final NmsProvider<?> nms;

    public ClientboundServerStatusListener(NmsProvider<?> plugin) {
        this.nms = plugin;
    }

    @Override
    public PacketWriteResult<ClientboundStatusResponsePacket> onPacketSend(Player player, ClientboundStatusResponsePacket packet) {
        ServerStatus prevStatus = packet.status();
        SecureChatNotificationPacketEvent event = new SecureChatNotificationPacketEvent(prevStatus.enforcesSecureChat());
        event.callEvent();

        boolean rebuild = false;
        if (event.isEnforcesSecureChat() != prevStatus.enforcesSecureChat()) rebuild = true;

        if (rebuild) {
            ServerStatus status = new ServerStatus(prevStatus.description(), prevStatus.players(), prevStatus.version(), prevStatus.favicon(), event.isEnforcesSecureChat());
            packet = new ClientboundStatusResponsePacket(status);
        }

        return new PacketWriteResult<>(!event.isCancelled(), packet);
    }

    @Override
    public Class<ClientboundStatusResponsePacket> getPacketClass() {
        return ClientboundStatusResponsePacket.class;
    }

}
