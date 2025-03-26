//package com.tcoded.nochatreports.nms.v1_21_4.listener;
//
//import com.tcoded.nochatreports.nms.NmsProvider;
//import com.tcoded.nochatreports.nms.event.SecureChatNotificationPacketEvent;
//import com.tcoded.nochatreports.nms.listener.PacketListener;
//import com.tcoded.nochatreports.nms.types.PacketWriteResult;
//import net.minecraft.network.protocol.game.ClientboundLoginPacket;
//import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
//import org.bukkit.entity.Player;
//
//public class ClientboundServerMetadataListener implements PacketListener<ClientboundLoginPacket> {
//
//    private final NmsProvider<?> nms;
//
//    public ClientboundServerMetadataListener(NmsProvider<?> plugin) {
//        this.nms = plugin;
//        System.out.println("ClientboundServerMetadataListener.<init> -> " + ClientboundStatusResponsePacket.class.getName());
//    }
//
//    @Override
//    public PacketWriteResult<ClientboundLoginPacket> onPacketSend(Player player, ClientboundLoginPacket packet) {
//        System.out.println("ClientboundServerMetadataListener.onPacketSend");
//
//        SecureChatNotificationPacketEvent event = new SecureChatNotificationPacketEvent(packet.enforcesSecureChat());
//        event.callEvent();
//
//        boolean rebuild = false;
//        if (event.isEnforcesSecureChat() != packet.enforcesSecureChat()) rebuild = true;
//
//        if (rebuild) {
//            packet = new ClientboundLoginPacket(
//                    packet.playerId(),
//                    packet.hardcore(),
//                    packet.levels(),
//                    packet.maxPlayers(),
//                    packet.chunkRadius(),
//                    packet.simulationDistance(),
//                    packet.reducedDebugInfo(),
//                    packet.showDeathScreen(),
//                    packet.doLimitedCrafting(),
//                    packet.commonPlayerSpawnInfo(),
//                    event.isEnforcesSecureChat()
//            );
//        }
//
//        return new PacketWriteResult<>(!event.isCancelled(), packet);
//    }
//
//    @Override
//    public Class<ClientboundLoginPacket> getPacketClass() {
//        return ClientboundLoginPacket.class;
//    }
//
//}
