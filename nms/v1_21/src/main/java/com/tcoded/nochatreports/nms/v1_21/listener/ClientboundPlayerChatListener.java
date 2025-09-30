package com.tcoded.nochatreports.nms.v1_21.listener;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.PlayerChatPacketEvent;
import com.tcoded.nochatreports.nms.listener.PacketListener;
import com.tcoded.nochatreports.nms.types.PacketWriteResult;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClientboundPlayerChatListener implements PacketListener<ClientboundPlayerChatPacket> {

    private final NmsProvider<?> nms;

    public ClientboundPlayerChatListener(NmsProvider<?> plugin) {
        this.nms = plugin;
    }

    @Override
    public PacketWriteResult<ClientboundPlayerChatPacket> onPacketSend(Player player, ClientboundPlayerChatPacket packet) {
        PlayerChatPacketEvent event = new PlayerChatPacketEvent(player, this.nms.wrapChatPacket(packet));
        Bukkit.getPluginManager().callEvent(event);

        return new PacketWriteResult<>(!event.isCancelled(), packet);
    }

    @Override
    public Class<ClientboundPlayerChatPacket> getPacketClass() {
        return ClientboundPlayerChatPacket.class;
    }

}
