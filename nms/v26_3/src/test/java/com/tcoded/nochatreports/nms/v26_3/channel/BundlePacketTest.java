package com.tcoded.nochatreports.nms.v26_3.channel;

import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BundlePacketTest {
    @Test
    void rewritesImmutableBundlesWithoutLosingPacketOrder() {
        var first = message("first");
        var cancelled = message("cancelled");
        var replaced = message("replaced");
        var replacement = message("replacement");
        var last = message("last");
        var original = new ClientboundBundlePacket(List.of(first, cancelled, replaced, last));
        var handler = new GlobalPacketHandlerImpl(null) {
            @Override
            protected PacketWriteResult<?> handleAnyPacket(Channel channel, Object packet) {
                return new PacketWriteResult<>(packet != cancelled, packet == replaced ? replacement : packet);
            }
        };
        var channel = new EmbeddedChannel(new ChannelPacketHandler(handler));
        try {
            assertTrue(channel.writeOutbound(original));
            ClientboundBundlePacket output = channel.readOutbound();
            var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
            output.subPackets().forEach(packets::add);
            assertEquals(List.of(first, replacement, last), packets);
            var unchanged = new ArrayList<Packet<? super ClientGamePacketListener>>();
            original.subPackets().forEach(unchanged::add);
            assertEquals(List.of(first, cancelled, replaced, last), unchanged);
        } finally {
            channel.finishAndReleaseAll();
        }
    }

    @Test
    void completesPromiseWhenEntireBundleIsCancelled() {
        var handler = new GlobalPacketHandlerImpl(null) {
            @Override
            protected PacketWriteResult<?> handleAnyPacket(Channel channel, Object packet) {
                return new PacketWriteResult<>(false, packet);
            }
        };
        var channel = new EmbeddedChannel(new ChannelPacketHandler(handler));
        try {
            var future = channel.writeOneOutbound(new ClientboundBundlePacket(List.of(message("cancelled"))));
            channel.flushOutbound();
            assertTrue(future.isSuccess());
            assertNull(channel.readOutbound());
        } finally {
            channel.finishAndReleaseAll();
        }
    }

    private static ClientboundSystemChatPacket message(String text) {
        return new ClientboundSystemChatPacket(Component.literal(text), false);
    }
}
