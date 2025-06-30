package com.tcoded.nochatreports.nms.v1_21_6;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.channel.ChannelInjector;
import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import com.tcoded.nochatreports.nms.types.NmsProviderConfig;
import com.tcoded.nochatreports.nms.v1_21_6.channel.ChannelInjectorImpl;
import com.tcoded.nochatreports.nms.v1_21_6.channel.GlobalPacketHandlerImpl;
import com.tcoded.nochatreports.nms.v1_21_6.listener.ClientboundLoginListener;
import com.tcoded.nochatreports.nms.v1_21_6.listener.ClientboundPlayerChatListener;
import com.tcoded.nochatreports.nms.v1_21_6.listener.ServerboundChatSessionUpdateListener;
import com.tcoded.nochatreports.nms.v1_21_6.wrapper.PlayerChatPacketImpl;
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
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Properties;

@SuppressWarnings("unused")
public class NmsProviderImpl extends NmsProvider<ServerPlayer> {

    private final GlobalPacketHandler globalPacketHandler;
    private final ChannelInjector channelInjector;

    public NmsProviderImpl(NmsProviderConfig nmsConfig) {
        super(nmsConfig);

        DedicatedServer server = (DedicatedServer) MinecraftServer.getServer();
        server.settings.update((config) -> {
            final Properties newProps = new Properties(config.properties);
            newProps.setProperty("enforce-secure-profile", String.valueOf(false));

            if (this.getConfig().isPaper()) {
                return config.reload(server.registryAccess(), newProps, server.options);
            } else {
                try {
                    Method reloadMethod = config.getClass().getDeclaredMethod("reload", RegistryAccess.class, Properties.class, OptionSet.class);
                    boolean prevAccessible = reloadMethod.isAccessible();
                    reloadMethod.setAccessible(true);
                    Object retValue = reloadMethod.invoke(config, server.registryAccess(), newProps, server.options);
                    reloadMethod.setAccessible(prevAccessible);
                    return (DedicatedServerProperties) retValue;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        });

        this.globalPacketHandler = new GlobalPacketHandlerImpl(this);
        this.channelInjector = new ChannelInjectorImpl(this, globalPacketHandler);
    }

    @Override
    public PlayerChatPacket wrapChatPacket(ByteBuf byteBuf) {
        return new PlayerChatPacketImpl(byteBuf);
    }

    @Override
    public void sendSystemPacket(Player player, SystemChatPacket systemPacket) {
        ServerPlayer nmsPlayer = getNmsPlayer(player);
        Packet<?> nmsPacket = (Packet<?>) systemPacket.toNmsPacket();

        nmsPlayer.connection.sendPacket(nmsPacket);
    }

    @Override
    public ServerPlayer getNmsPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        if (this.getConfig().isPaper()) return (ServerPlayer) craftPlayer.getHandleRaw();
        else return craftPlayer.getHandle();
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
//        this.getGlobalPacketHandler().addListener(new ClientboundServerStatusListener(this));
        this.getGlobalPacketHandler().addListener(new ServerboundChatSessionUpdateListener(this));
        this.getGlobalPacketHandler().addListener(new ClientboundLoginListener(this));
    }

    @Override
    public PlayerChatPacket wrapChatPacket(Object packet) {
        if (packet instanceof ClientboundPlayerChatPacket chatPacket) {
            return new PlayerChatPacketImpl(chatPacket);
        }

        return null;
    }

}