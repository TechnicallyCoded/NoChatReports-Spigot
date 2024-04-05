package com.tcoded.nochatreports.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class KickListener implements Listener {

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        String reason = event.getReason();
        if (reason.equals("Received chat packet with missing or invalid signature.")) {
            event.setCancelled(true);
        }
    }

}
