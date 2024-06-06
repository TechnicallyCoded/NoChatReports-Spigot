package com.tcoded.nochatreports.nms.v1_20_4;

import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;

import java.util.Optional;

public class PlayerChatPacketImpl implements PlayerChatPacket {

    private final ClientboundPlayerChatPacket packet;

    public PlayerChatPacketImpl(ClientboundPlayerChatPacket packet) {
        this.packet = packet;
    }

    public PlayerChatPacketImpl(ByteBuf byteBuf) {
        this(new ClientboundPlayerChatPacket(new FriendlyByteBuf(byteBuf)));
    }

    public SystemChatPacket toSystem() {
        try {
            // Resolve the chat type
            DedicatedServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

            RegistryAccess.Frozen registryAccess = nmsServer.registryAccess();
            Optional<ChatType.Bound> chatType = packet.chatType().resolve(registryAccess);

            // Get the content of the message
            Component content = packet.unsignedContent();
            if (content == null) content = Component.literal(packet.body().content());

            // Apply formatting to the content
            Component formattedContent = chatType.orElseThrow().decorate(content);

            // Create a new system chat packet
            ClientboundSystemChatPacket systemChatPacket = new ClientboundSystemChatPacket(formattedContent, false);
            return new SystemChatPacketImpl(systemChatPacket);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
