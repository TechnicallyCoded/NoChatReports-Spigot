package com.tcoded.nochatreports.nms.channel;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.listener.PacketListener;
import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalPacketHandler {

    protected static final List<PacketListener<?>> EMPTY_LISTENER_LIST = List.of();

    protected final ConcurrentHashMap<Class<?>, List<PacketListener<?>>> listeners;
    private final NmsProvider<?> nms;

    public GlobalPacketHandler(NmsProvider<?> nms) {
        this.nms = nms;
        this.listeners = new ConcurrentHashMap<>();
    }

    protected PacketWriteResult<?> handleAnyPacket(Channel channel, Object packet) {
        Player player = this.nms.getChannelInjector().getPlayer(channel);
        List<PacketListener<?>> listenersForType = this.listeners.getOrDefault(packet.getClass(), EMPTY_LISTENER_LIST);

        PacketWriteResult<?> result = new PacketWriteResult<>(true, packet);
        for (PacketListener<?> listener : listenersForType) {
            if (listener.getPacketClass().isAssignableFrom(packet.getClass())) {
                result = listener.onPacketSendInternal(player, result.packet());
            }
        }

        return result;
    }

    public void addListener(PacketListener<?> packetListener) {
        this.listeners.computeIfAbsent(packetListener.getPacketClass(), k -> new ArrayList<>())
                .add(packetListener);
    }

}
