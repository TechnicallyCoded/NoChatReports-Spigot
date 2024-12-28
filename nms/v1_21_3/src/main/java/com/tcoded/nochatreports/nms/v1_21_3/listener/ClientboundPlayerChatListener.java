package com.tcoded.nochatreports.nms.v1_21_3.listener;

import com.tcoded.nochatreports.nms.PlayerChatPacketEvent;
import com.tcoded.nochatreports.nms.listener.PacketListener;
import com.tcoded.nochatreports.nms.v1_21_3.NMS_v1_21_3;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.bukkit.entity.Player;

public class ClientboundPlayerChatListener implements PacketListener<ClientboundPlayerChatPacket> {

    private final NMS_v1_21_3 nms;

    public ClientboundPlayerChatListener(NMS_v1_21_3 plugin) {
        this.nms = plugin;
    }

    @Override
    public boolean onPacketSend(Player player, ClientboundPlayerChatPacket packet) {
        PlayerChatPacketEvent event = new PlayerChatPacketEvent(player, this.nms.wrapChatPacket(packet));
        event.callEvent();

        return !event.isCancelled();
    }

    @Override
    public Class<ClientboundPlayerChatPacket> getPacketClass() {
        return ClientboundPlayerChatPacket.class;
    }

}
