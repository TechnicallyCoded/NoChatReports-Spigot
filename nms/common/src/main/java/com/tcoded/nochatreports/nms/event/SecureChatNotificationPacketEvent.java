package com.tcoded.nochatreports.nms.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SecureChatNotificationPacketEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private boolean enforcesSecureChat;

    public SecureChatNotificationPacketEvent(boolean enforcesSecureChat) {
        super(!Bukkit.isPrimaryThread());
        this.enforcesSecureChat = enforcesSecureChat;
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

    public boolean isEnforcesSecureChat() {
        return enforcesSecureChat;
    }

    public void setEnforcesSecureChat(boolean enforcesSecureChat) {
        this.enforcesSecureChat = enforcesSecureChat;
    }

}

