package com.tcoded.nochatreports.nms.v1_21_3;

import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.MinecraftServer;

public class PlayerChatPacketImpl implements PlayerChatPacket {

    private final ClientboundPlayerChatPacket packet;

    public PlayerChatPacketImpl(ClientboundPlayerChatPacket packet) {
        this.packet = packet;
    }

    public PlayerChatPacketImpl(ByteBuf byteBuf) {
        this(ClientboundPlayerChatPacket.STREAM_CODEC.decode(new RegistryFriendlyByteBuf(byteBuf, MinecraftServer.getServer().registryAccess())));
    }

    public SystemChatPacket toSystem() {
        try {
            ChatType.Bound chatTypeWithBound = packet.chatType();

            // Get the content of the message
            Component content = packet.unsignedContent();
            if (content == null) content = Component.literal(packet.body().content());

            // Apply formatting to the content
            Component formattedContent = chatTypeWithBound.decorate(content);

            // Create a new system chat packet
            ClientboundSystemChatPacket systemChatPacket = new ClientboundSystemChatPacket(formattedContent, false);
            return new SystemChatPacketImpl(systemChatPacket);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
