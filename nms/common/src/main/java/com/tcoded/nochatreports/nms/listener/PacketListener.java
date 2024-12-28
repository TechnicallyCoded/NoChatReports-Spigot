package com.tcoded.nochatreports.nms.listener;

import org.bukkit.entity.Player;

public interface PacketListener <T> {

    /**
     * Called when a packet is about to be sent to the player.
     *
     * @param packet The packet
     * @return true to keep the packet, false to cancel it
     */
    boolean onPacketSend(Player player, T packet);

    /**
     * Get the class of the packet this listener listens to.
     * @return The packet class
     */
    Class<T> getPacketClass();

    /**
     * Internal use only!
     * @param packet The packet
     * @return true to keep the packet, false to cancel it
     */
    default boolean onPacketSendInternal(Player player, Object packet) {
        // noinspection unchecked
        return onPacketSend(player, (T) packet);
    }

}
