package com.tcoded.nochatreports.plugin.listener;

import com.tcoded.nochatreports.nms.PlayerChatPacketEvent;
import com.tcoded.nochatreports.nms.event.ChatSessionUpdateEvent;
import com.tcoded.nochatreports.nms.event.SecureChatNotificationPacketEvent;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import com.tcoded.nochatreports.plugin.NoChatReports;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PacketListener implements Listener {

    private final NoChatReports plugin;

    public PacketListener(NoChatReports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatPacket(PlayerChatPacketEvent event) {
        // Config check
        if (!plugin.getConfig().getBoolean("strip-server-chat-signatures", true)) return;

        SystemChatPacket systemChatPacket = event.getPacket().toSystem();
        event.setCancelled(true);

        this.plugin.getNmsProvider().sendSystemPacket(event.getPlayer(), systemChatPacket);
    }

    @EventHandler
    public void onServerStatus(SecureChatNotificationPacketEvent event) {
        // Config check
        if (!plugin.getConfig().getBoolean("hide-scary-popup", true)) return;

        event.setEnforcesSecureChat(true); // We don't really, but let's say that we do!
    }

    @EventHandler
    public void onChatSessionUpdate(ChatSessionUpdateEvent event) {
        // Config check
        if (!plugin.getConfig().getBoolean("strip-server-chat-signatures", true)) return;

        event.setCancelled(true);
    }

}
