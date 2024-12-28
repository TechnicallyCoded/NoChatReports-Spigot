package com.tcoded.nochatreports.nms.v1_21.listener;

import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.nms.PlayerChatPacketEvent;
import com.tcoded.nochatreports.nms.listener.PacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.bukkit.entity.Player;

public class ClientboundPlayerChatListener implements PacketListener<ClientboundPlayerChatPacket> {

    private final NmsProvider<?> nms;

    public ClientboundPlayerChatListener(NmsProvider<?> plugin) {
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
