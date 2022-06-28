package ml.tcoded.nochatreports.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AsyncPostNonReportableChatEvent extends Event {

    private final HandlerList handlerList;
    private Player player;
    private String message;
    private Set<Player> recipients;


    public AsyncPostNonReportableChatEvent(Player player, String message, Set<Player> recipients) {
        super(true);
        this.handlerList = new HandlerList();

        this.player = player;
        this.message = message;
        this.recipients = recipients;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @SuppressWarnings("unused")
    public Player getPlayer() {
        return player;
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
    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public void setRecipients(Set<Player> recipients) {
        this.recipients = recipients;
    }
}
