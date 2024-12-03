package com.tcoded.nochatreports.nms.v1_21_4;

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
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Properties;

@SuppressWarnings("unused")
public class NMS_v1_21_4 extends NmsProvider<ServerPlayer> {

    private final boolean isPaper;

    public NMS_v1_21_4(boolean isPaper) {
        this.isPaper = isPaper;

        DedicatedServer server = (DedicatedServer) MinecraftServer.getServer();
        server.settings.update((config) -> {
            final Properties newProps = new Properties(config.properties);
            newProps.setProperty("enforce-secure-profile", String.valueOf(false));

            if (isPaper) {
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
        if (isPaper) return (ServerPlayer) craftPlayer.getHandleRaw();
        else return craftPlayer.getHandle();
    }

}