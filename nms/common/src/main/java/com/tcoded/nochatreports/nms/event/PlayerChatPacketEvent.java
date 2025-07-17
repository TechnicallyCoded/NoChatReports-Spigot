package com.tcoded.nochatreports.nms.event;

import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChatPacketEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final PlayerChatPacket packet;
    private boolean cancelled;

    public PlayerChatPacketEvent(PlayerChatPacket packet) {
        this.packet = packet;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}

