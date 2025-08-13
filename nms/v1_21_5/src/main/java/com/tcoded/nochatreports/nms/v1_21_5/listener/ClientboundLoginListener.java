package com.tcoded.nochatreports.nms.v1_21_5.listener;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.event.SecureChatNotificationPacketEvent;
import com.tcoded.nochatreports.nms.listener.PacketListener;
import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.bukkit.entity.Player;

public class ClientboundLoginListener implements PacketListener<ClientboundLoginPacket> {

    private final NmsProvider<?> nms;

    public ClientboundLoginListener(NmsProvider<?> plugin) {
        this.nms = plugin;
    }

    @Override
    public PacketWriteResult<ClientboundLoginPacket> onPacketSend(Player player, ClientboundLoginPacket packet) {

        SecureChatNotificationPacketEvent event = new SecureChatNotificationPacketEvent(packet.enforcesSecureChat());
        Bukkit.getPluginManager().callEvent(event);

        boolean rebuild = false;
        if (event.isEnforcesSecureChat() != packet.enforcesSecureChat()) rebuild = true;

        if (rebuild) {
            packet = new ClientboundLoginPacket(
                    packet.playerId(),
                    packet.hardcore(),
                    packet.levels(),
                    packet.maxPlayers(),
                    packet.chunkRadius(),
                    packet.simulationDistance(),
                    packet.reducedDebugInfo(),
                    packet.showDeathScreen(),
                    packet.doLimitedCrafting(),
                    packet.commonPlayerSpawnInfo(),
                    event.isEnforcesSecureChat()
            );
        }

        return new PacketWriteResult<>(!event.isCancelled(), packet);
    }

    @Override
    public Class<ClientboundLoginPacket> getPacketClass() {
        return ClientboundLoginPacket.class;
    }

}
