package com.tcoded.nochatreports.nms.channel;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.net.SocketAddress;

public interface ChannelInjector {

    static final String HANDLER_NAME = "ncr_packet_handler";
    static final String ADD_BEFORE = "packet_handler";

    void inject(Player player);
    void uninject(Player player);

    Player getPlayer(Channel channel);

    default void injectAll(Iterable<? extends Player> players) {
        for (Player player : players) {
            inject(player);
        }
    }

    default void uninjectAll(Iterable<? extends Player> players) {
        for (Player player : players) {
            uninject(player);
        }
    }

}
