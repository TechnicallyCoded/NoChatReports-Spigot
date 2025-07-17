package com.tcoded.nochatreports.nms.channel;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractChannelInjector implements ChannelInjector {

    private final GlobalPacketHandler globalPacketHandler;
    private final ConcurrentHashMap<SocketAddress, Player> playerChannelMap = new ConcurrentHashMap<>();
    protected Field connectionField;

    protected AbstractChannelInjector(GlobalPacketHandler globalPacketHandler) {
        this.globalPacketHandler = globalPacketHandler;
    }

    protected void mapPlayerChannel(SocketAddress socketAddress, Player player) {
        playerChannelMap.put(socketAddress, player);
    }

    protected void unmapPlayerChannel(SocketAddress socketAddress) {
        playerChannelMap.remove(socketAddress);
    }

    @Override
    public Player getPlayer(Channel channel) {
        return playerChannelMap.get(channel.remoteAddress());
    }

    protected GlobalPacketHandler getGlobalPacketHandler() {
        return globalPacketHandler;
    }

}
