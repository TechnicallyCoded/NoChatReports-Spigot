package com.tcoded.nochatreports.nms.v1_19_2;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMS_v1_19_2 extends NmsProvider<ServerPlayer> {
    private final boolean isPaper;

    public NMS_v1_19_2(boolean isPaper) {
        this.isPaper = isPaper;
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
