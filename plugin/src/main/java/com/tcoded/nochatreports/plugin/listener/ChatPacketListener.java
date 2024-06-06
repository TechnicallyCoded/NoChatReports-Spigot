package com.tcoded.nochatreports.plugin.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.util.crypto.MessageSignData;
import com.github.retrooper.packetevents.util.crypto.SaltSignature;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerData;
import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import com.tcoded.nochatreports.plugin.NoChatReports;
import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;

import java.time.Instant;

public class ChatPacketListener implements PacketListener {

    private final NoChatReports plugin;

    public ChatPacketListener(NoChatReports plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketTypeCommon type = event.getPacketType();

        if (type == PacketType.Play.Server.CHAT_MESSAGE) {
            handlePlayerChatMessagePacket(event);
        }
        else if (type == PacketType.Play.Server.PLAYER_CHAT_HEADER) {
            handlePlayerChatHeaderPacket(event);
        }
        else if (type == PacketType.Play.Server.SERVER_DATA) {
            handleServerDataPacket(event);
        }
    }

    private void handlePlayerChatHeaderPacket(PacketSendEvent event) {
        // Config check
        if (!plugin.getConfig().getBoolean("strip-server-chat-signatures", true)) return;
        event.setCancelled(true);
    }

    private void handleServerDataPacket(PacketSendEvent event) {
        // Config check
        if (!plugin.getConfig().getBoolean("hide-scary-popup", true)) return;

        WrapperPlayServerServerData wrapper = new WrapperPlayServerServerData(event);
        wrapper.setEnforceSecureChat(true);
    }

    private void handlePlayerChatMessagePacket(PacketSendEvent event) {

        event.setCancelled(true);

        // Config check
        if (!plugin.getConfig().getBoolean("strip-server-chat-signatures", true)) return;

        NmsProvider<?> nmsProvider = this.plugin.getNmsProvider();

        PlayerChatPacket wrappedChatPacket = nmsProvider.wrapChatPacket((ByteBuf) event.getByteBuf());
        SystemChatPacket systemPacket = wrappedChatPacket.toSystem();

        Player player = (Player) event.getPlayer();
        nmsProvider.sendSystemPacket(player, systemPacket);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        PacketTypeCommon type = event.getPacketType();

        if (type == PacketType.Play.Client.CHAT_MESSAGE) {

            // Config check
            if (!plugin.getConfig().getBoolean("strip-client-chat-signatures", true)) return;

            WrapperPlayClientChatMessage wrapper = new WrapperPlayClientChatMessage(event);
            wrapper.setMessageSignData(new MessageSignData(new SaltSignature(0L, new byte[0]), Instant.ofEpochMilli(0L), false));
        }

    }
}
