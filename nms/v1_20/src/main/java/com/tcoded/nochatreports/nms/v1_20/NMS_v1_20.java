package com.tcoded.nochatreports.nms.v1_20;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.channel.ChannelInjector;
import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import com.tcoded.nochatreports.nms.v1_20.channel.ChannelInjectorImpl;
import com.tcoded.nochatreports.nms.v1_20.channel.GlobalPacketHandlerImpl;
import com.tcoded.nochatreports.nms.v1_20.listener.ClientboundPlayerChatListener;
import com.tcoded.nochatreports.nms.v1_20.wrapper.PlayerChatPacketImpl;
import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import joptsimple.OptionSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Properties;

@SuppressWarnings("unused")
public class NMS_v1_20 extends NmsProvider<ServerPlayer> {

    private final boolean isPaper;
    private final GlobalPacketHandler globalPacketHandler;
    private final ChannelInjector channelInjector;

    public NMS_v1_20(boolean isPaper) {
        this.isPaper = isPaper;

        DedicatedServer server = (DedicatedServer) MinecraftServer.getServer();
        server.settings.update((config) -> {
            final Properties newProps = new Properties(config.properties);
            newProps.setProperty("enforce-secure-profile", String.valueOf(false));

            try {
                Class<? extends DedicatedServerProperties> serverPropsClass = config.getClass();
                Method reloadMethod = serverPropsClass.getDeclaredMethod("reload", RegistryAccess.class, Properties.class, OptionSet.class);

                boolean prev = reloadMethod.isAccessible();
                reloadMethod.setAccessible(true);

                DedicatedServerProperties newServerProps = (DedicatedServerProperties) reloadMethod.invoke(config, server.registryAccess(), newProps, server.options);
                reloadMethod.setAccessible(prev);

                return newServerProps;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        this.globalPacketHandler = new GlobalPacketHandlerImpl(this);
        this.channelInjector = new ChannelInjectorImpl(globalPacketHandler);
    }

    @Override
    public PlayerChatPacket wrapChatPacket(ByteBuf byteBuf) {
        return new PlayerChatPacketImpl(byteBuf);
    }

    @Override
    public void sendSystemPacket(Player player, SystemChatPacket systemPacket) {
        ServerPlayer nmsPlayer = getNmsPlayer(player);
        Packet<?> nmsPacket = (Packet<?>) systemPacket.toNmsPacket();

        nmsPlayer.connection.send(nmsPacket);
    }

    @Override
    public ServerPlayer getNmsPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return craftPlayer.getHandle();
    }

    @Override
    public GlobalPacketHandler getGlobalPacketHandler() {
        return globalPacketHandler;
    }

    @Override
    public ChannelInjector getChannelInjector() {
        return channelInjector;
    }

    public void registerListeners() {
        this.getGlobalPacketHandler().addListener(new ClientboundPlayerChatListener(this));
    }

    @Override
    public PlayerChatPacket wrapChatPacket(Object packet) {
        if (packet instanceof ClientboundPlayerChatPacket chatPacket) {
            return new PlayerChatPacketImpl(chatPacket);
        }

        return null;
    }

}