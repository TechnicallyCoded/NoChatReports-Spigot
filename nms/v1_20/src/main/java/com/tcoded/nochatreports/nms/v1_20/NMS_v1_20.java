package com.tcoded.nochatreports.nms.v1_20;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import joptsimple.OptionSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.Packet;
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

}