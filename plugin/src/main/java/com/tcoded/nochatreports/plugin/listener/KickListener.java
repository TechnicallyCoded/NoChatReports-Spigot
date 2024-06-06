package com.tcoded.nochatreports.plugin.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;

import java.lang.reflect.Method;
import java.util.List;

public class KickListener implements Listener {

    private final String preventedKickMessage;
    private final String[] invalidReasons;

    public KickListener(String preventedKickMessage, List<String> invalidReasons) {
        this.preventedKickMessage = preventedKickMessage;
        this.invalidReasons = invalidReasons.toArray(String[]::new);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {

        try {
            Class<? extends PlayerKickEvent> kickClass = event.getClass();
            Method getCause = kickClass.getMethod("getCause");
            Enum<?> cause = (Enum<?>) getCause.invoke(event);
            String paperReason = cause.name();

            for (String invalidReason : invalidReasons) {
                if (!paperReason.equals(invalidReason)) continue;

                event.setCancelled(true);
                event.getPlayer().sendMessage(preventedKickMessage);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String reason = event.getReason();
        if (reason.equals("Received chat packet with missing or invalid signature.")) {
            event.getPlayer().sendMessage(preventedKickMessage);
            event.setCancelled(true);
        }
    }

}
