package com.tcoded.nochatreports.nms.v26_3;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.types.NmsProviderConfig;
import com.tcoded.nochatreports.nms.v26_3.wrapper.PlayerChatPacketImpl;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ChatTypeDecoration;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChatPacketTest {
    @BeforeAll
    static void initializeRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void keepsUnsignedContentAndChatDecoration() {
        var unsigned = Component.literal("formatted message").withStyle(ChatFormatting.GOLD);
        var packet = packet(Optional.of(unsigned));
        var result = (ClientboundSystemChatPacket) new PlayerChatPacketImpl(packet).toSystem().toNmsPacket();
        assertEquals(packet.chatType().decorate(unsigned), result.content());
        assertFalse(result.overlay());
    }

    @Test
    void fallsBackToSignedBodyWhenUnsignedContentIsAbsent() {
        var packet = packet(Optional.empty());
        var result = (ClientboundSystemChatPacket) new PlayerChatPacketImpl(packet).toSystem().toNmsPacket();
        assertEquals(packet.chatType().decorate(Component.literal("original message")), result.content());
        assertFalse(result.overlay());
    }

    @Test
    void unsupportedVersionsDoNotConstructAnyProvider() {
        var errors = new ByteArrayOutputStream();
        PrintStream previous = System.err;
        try (var capture = new PrintStream(errors, true, StandardCharsets.UTF_8)) {
            System.setErr(capture);
            assertNull(NmsProvider.getNmsProvider("26.30", new NmsProviderConfig(true, () -> false)));
        } finally {
            System.setErr(previous);
        }
        assertEquals("", errors.toString(StandardCharsets.UTF_8));
    }

    private static ClientboundPlayerChatPacket packet(Optional<Component> unsigned) {
        var body = new SignedMessageBody.Packed("original message", Instant.EPOCH, 1L, LastSeenMessages.Packed.EMPTY);
        var type = new ChatType(ChatTypeDecoration.withSender("chat.type.text"),
                ChatTypeDecoration.withSender("chat.type.text.narrate"));
        var bound = new ChatType.Bound(Holder.direct(type), Component.literal("Alex"), Optional.empty());
        return new ClientboundPlayerChatPacket(0, new UUID(1L, 2L), 1, Optional.empty(), body,
                unsigned, FilterMask.PASS_THROUGH, bound);
    }
}
