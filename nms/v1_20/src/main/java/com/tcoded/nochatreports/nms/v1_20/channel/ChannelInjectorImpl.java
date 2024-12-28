package com.tcoded.nochatreports.nms.v1_20.channel;

import com.tcoded.nochatreports.nms.channel.AbstractChannelInjector;
import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;

public class ChannelInjectorImpl extends AbstractChannelInjector {

    public ChannelInjectorImpl(GlobalPacketHandler packetHandler) {
        super(packetHandler);

        try {
            for (Field field : ServerGamePacketListenerImpl.class.getDeclaredFields()) {
                if (field.getType().equals(Connection.class)) {
                    field.setAccessible(true);
                    connectionField = field;
                    break;
                }
            }
        } catch (SecurityException | InaccessibleObjectException e) {
            throw new RuntimeException(e);
        }
    }

    public void inject(Player player) {
        ServerGamePacketListenerImpl listener = ((CraftPlayer) player).getHandle().connection;
        Channel channel = getConnection(listener).channel;

        channel.eventLoop().submit(() -> {
            ChannelPipeline pipeline = channel.pipeline();

            if (pipeline.get(HANDLER_NAME) != null) {
                pipeline.remove(HANDLER_NAME);
            }

            pipeline.addBefore(ADD_BEFORE, HANDLER_NAME, new ChannelPacketHandler(this.getGlobalPacketHandler()));
        });

        this.mapPlayerChannel(channel.remoteAddress(), player);
    }

    public void uninject(Player player) {
        ServerGamePacketListenerImpl listener = ((CraftPlayer) player).getHandle().connection;
        Channel channel = getConnection(listener).channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(HANDLER_NAME);
        });
        this.unmapPlayerChannel(channel.remoteAddress());
    }

    private Connection getConnection(ServerGamePacketListenerImpl listener) {
        try {
            return (Connection) connectionField.get(listener);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
