package ml.tcoded.nochatreports.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AsyncNonReportableChatEvent extends Event implements Cancellable {

    private final HandlerList handlerList;
    private Player player;

    private String format;
    private String message;
    private Set<Player> recipients;

    private boolean cancelled;


    public AsyncNonReportableChatEvent(Player player, String format, String message, Set<Player> recipients) {
        super(true);
        this.handlerList = new HandlerList();

        this.player = player;
        this.format = format;
        this.message = message;
        this.recipients = recipients;

        this.cancelled = false;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public String getFormat() {
        return format;
    }

    public String getMessage() {
        return message;
    }

    @SuppressWarnings("unused")
    public Set<Player> getRecipients() {
        return recipients;
    }

    @SuppressWarnings("unused")
    public void setPlayer(Player player) {
        this.player = player;
    }

    @SuppressWarnings("unused")
    public void setFormat(String format) {
        this.format = format;
    }

    @SuppressWarnings("unused")
    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public void setRecipients(Set<Player> recipients) {
        this.recipients = recipients;
    }
}
