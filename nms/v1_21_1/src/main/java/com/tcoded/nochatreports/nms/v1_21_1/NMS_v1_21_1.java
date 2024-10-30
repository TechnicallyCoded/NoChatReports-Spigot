package com.tcoded.nochatreports.nms.v1_21_1;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Properties;

@SuppressWarnings("unused")
public class NMS_v1_21_1 extends NmsProvider<ServerPlayer> {

    private final boolean isPaper;

    public NMS_v1_21_1(boolean isPaper) {
        this.isPaper = isPaper;

        DedicatedServer server = (DedicatedServer) MinecraftServer.getServer();
        server.settings.update((config) -> {
            final Properties newProps = new Properties(config.properties);
            newProps.setProperty("enforce-secure-profile", String.valueOf(false));
            return config.reload(server.registryAccess(), newProps, server.options);
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