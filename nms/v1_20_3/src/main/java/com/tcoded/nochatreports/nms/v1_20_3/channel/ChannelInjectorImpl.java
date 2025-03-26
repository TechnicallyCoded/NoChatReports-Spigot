package com.tcoded.nochatreports.nms.v1_20_3.channel;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.channel.AbstractChannelInjector;
import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import com.tcoded.nochatreports.nms.types.MIMList;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.List;

public class ChannelInjectorImpl extends AbstractChannelInjector {

    private final NmsProvider<ServerPlayer> nms;

    public ChannelInjectorImpl(NmsProvider<ServerPlayer> nms, GlobalPacketHandler packetHandler) {
        super(packetHandler);
        this.nms = nms;

        try {
            for (Field field : ServerCommonPacketListenerImpl.class.getDeclaredFields()) {
                if (field.getType().equals(Connection.class)) {
                    field.setAccessible(true);
                    connectionField = field;
                    break;
                }
            }
        } catch (SecurityException | InaccessibleObjectException e) {
            throw new RuntimeException(e);
        }

        ServerConnectionListener serverConnectionHandler = MinecraftServer.getServer().getConnection();
        List<Connection> connectionsList = serverConnectionHandler.getConnections();

        Class<? extends ServerConnectionListener> serverConnectionClass = serverConnectionHandler.getClass();
        for (Field field : serverConnectionClass.getFields()) {
            try {
                if (field.get(serverConnectionHandler) != connectionsList) continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            this.mimList(field, serverConnectionHandler, connectionsList);
        }

        for (Connection conn : serverConnectionHandler.getConnections()) {
            setPacketHandler(conn, false);
        }
    }

    private void mimList(Field field, ServerConnectionListener serverConnectionHandler, List<Connection> connectionsList) {
        try {
            field.setAccessible(true);
            field.set(serverConnectionHandler, new MIMList<Connection>(connectionsList, this::autoAdd, this::unsetPacketHandler));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection autoAdd(Connection connection) {
        setPacketHandler(connection, false);
        return connection;
    }

    public void inject(Player player) {
        ServerGamePacketListenerImpl listener = nms.getNmsPlayer(player).connection;

        Connection connection = getConnection(listener);
        setPacketHandler(connection, true);

        this.mapPlayerChannel(connection.channel.remoteAddress(), player);
    }

    private void setPacketHandler(Connection connection, boolean replace) {
        setPacketHandler(connection.channel, replace);
    }

    private void setPacketHandler(Channel channel, boolean replace) {
        channel.eventLoop().submit(() -> {
            ChannelPipeline pipeline = channel.pipeline();

            boolean hasCustomHandler = pipeline.get(HANDLER_NAME) != null;
            if (hasCustomHandler && replace) {
                pipeline.remove(HANDLER_NAME);
            }

            if (!hasCustomHandler || replace) {
                pipeline.addBefore(ADD_BEFORE, HANDLER_NAME, new ChannelPacketHandler(this.getGlobalPacketHandler()));
            }
        });
    }

    public void uninject(Player player) {
        ServerGamePacketListenerImpl listener = this.nms.getNmsPlayer(player).connection;

        Connection connection = getConnection(listener);
        unsetPacketHandler(connection);

        this.unmapPlayerChannel(connection.channel.remoteAddress());
    }

    private void unsetPacketHandler(Connection connection) {
        Channel channel = connection.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(HANDLER_NAME);
        });
    }

    private Connection getConnection(ServerGamePacketListenerImpl listener) {
        if (listener == null) return null;

        try {
            return (Connection) connectionField.get(listener);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
