package com.tcoded.nochatreports.plugin.listener;

import com.tcoded.nochatreports.nms.PlayerChatPacketEvent;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import com.tcoded.nochatreports.plugin.NoChatReports;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final NoChatReports plugin;

    public ChatListener(NoChatReports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatPacket(PlayerChatPacketEvent event) {
        SystemChatPacket systemChatPacket = event.getPacket().toSystem();
        event.setCancelled(true);

        this.plugin.getNmsProvider().sendSystemPacket(event.getPlayer(), systemChatPacket);
    }

}
