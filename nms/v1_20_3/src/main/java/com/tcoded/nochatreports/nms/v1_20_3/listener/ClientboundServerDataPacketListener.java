package com.tcoded.nochatreports.nms.v1_20_3.listener;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.event.SecureChatNotificationPacketEvent;
import com.tcoded.nochatreports.nms.listener.PacketListener;
import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import org.bukkit.entity.Player;

public class ClientboundServerDataPacketListener implements PacketListener<ClientboundServerDataPacket> {

    private final NmsProvider<?> nms;

    public ClientboundServerDataPacketListener(NmsProvider<?> plugin) {
        this.nms = plugin;
    }

    @Override
    public PacketWriteResult<ClientboundServerDataPacket> onPacketSend(Player player, ClientboundServerDataPacket packet) {
        SecureChatNotificationPacketEvent event = new SecureChatNotificationPacketEvent(packet.enforcesSecureChat());
        Bukkit.getPluginManager().callEvent(event);

        boolean rebuild = false;
        if (event.isEnforcesSecureChat() != packet.enforcesSecureChat()) rebuild = true;

        if (rebuild) {
            packet = new ClientboundServerDataPacket(
                    packet.getMotd(),
                    packet.getIconBytes(),
                    event.isEnforcesSecureChat()
            );
        }

        return new PacketWriteResult<>(!event.isCancelled(), packet);
    }

    @Override
    public Class<ClientboundServerDataPacket> getPacketClass() {
        return ClientboundServerDataPacket.class;
    }

}
