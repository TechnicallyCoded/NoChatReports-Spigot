package com.tcoded.nochatreports.nms.v1_21_6.channel;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.channel.AbstractChannelInjector;
import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import com.tcoded.nochatreports.nms.types.MIMList;
import com.tcoded.nochatreports.nms.types.MIMQueue;
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
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class ChannelInjectorImpl extends AbstractChannelInjector {

    private static final Logger logger = Logger.getLogger("NCR Channel Injector");

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

        ServerConnectionListener srvConnHandler = MinecraftServer.getServer().getConnection();
        List<Connection> connList = srvConnHandler.getConnections();

        Class<? extends ServerConnectionListener> serverConnectionClass = srvConnHandler.getClass();
        boolean unlockPaperQueue = false;
        for (Field field : serverConnectionClass.getDeclaredFields()) {
            try {
                if (field.getType().isAssignableFrom(List.class)) {
                    if (!isIdentityEq(field, srvConnHandler, connList)) this.applyMimList(field, srvConnHandler, this::handleNewChannel, this::handleRemoveChannel); // 1st list
                    else this.applyMimList(field, srvConnHandler, this::handleNewConnection, this::handleRemoveConnection); // 2nd list
                    unlockPaperQueue = true; // Paper's "pending" Queue should come right after
                }
                if (unlockPaperQueue && field.getType().isAssignableFrom(Queue.class)) {
                    this.applyMimQueue(field, srvConnHandler); // 3rd "list" (actually a queue)
                    unlockPaperQueue = false; // Lock it again
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        for (Connection conn : srvConnHandler.getConnections()) {
            setPacketHandler(conn, false);
        }
    }

    private static boolean isIdentityEq(Field field, ServerConnectionListener serverConnectionHandler, List<Connection> connectionsList) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(serverConnectionHandler) == connectionsList;
    }

    private <T> void applyMimList(Field field, ServerConnectionListener handler, Function<T, T> interceptor, Consumer<T> cleanup) {
        try {
            field.setAccessible(true);
            // noinspection unchecked
            List<T> objectList = (List<T>) field.get(handler);
            field.set(handler, new MIMList<>(objectList, interceptor, cleanup));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyMimQueue(Field field, ServerConnectionListener handler) {
        try {
            field.setAccessible(true);
            // noinspection unchecked
            Queue<Connection> pendingQueue = (Queue<Connection>) field.get(handler);
            field.set(handler, new MIMQueue<>(pendingQueue, this::handleNewConnection, this::handleRemoveConnection));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection handleNewConnection(Connection connection) {
        handleNewChannel(connection.channel);
        return connection;
    }

    private Channel handleNewChannel(Channel channel) {
        setPacketHandler(channel, false);
        return channel;
    }

    public void inject(Player player) {
        ServerGamePacketListenerImpl listener = nms.getNmsPlayer(player).connection;

        Connection connection = getConnection(listener);
        setPacketHandler(connection, true);

        this.mapPlayerChannel(connection.channel.remoteAddress(), player);
    }

    private void setPacketHandler(Connection connection, boolean replace) {
        // This is definitely null sometimes
        // noinspection ConstantValue
        if (connection.channel == null) {
            if (nms.isDebug()) logger.info("setPacketHandler: connection.channel is null");
            return;
        }
        setPacketHandler(connection.channel, replace);
    }

    private void setPacketHandler(Channel channel, boolean replace) {
        if (channel == null) {
            if (nms.isDebug()) logger.warning("setPacketHandler failed: channel is null");
            return;
        }
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
        handleRemoveConnection(connection);

        this.unmapPlayerChannel(connection.channel.remoteAddress());
    }

    private void handleRemoveConnection(Connection connection) {
        // This is definitely null sometimes
        // noinspection ConstantValue
        if (connection.channel == null) return;
        this.handleRemoveChannel(connection.channel);
    }

    private void handleRemoveChannel(Channel channel) {
        if (channel == null) {
            if (nms.getConfig().isPaper()) logger.warning("handleRemoveChannel channel is null");
            return;
        }

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
