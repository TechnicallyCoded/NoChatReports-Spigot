package ml.tcoded.nochatreports.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AsyncPostNonReportableChatEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private Player player;
    private String message;
    private String formattedMessage;
    private Set<Player> recipients;


    public AsyncPostNonReportableChatEvent(boolean async, Player player, String message, String formattedMessage, Set<Player> recipients) {
        super(async);

        this.player = player;
        this.message = message;
        this.formattedMessage = formattedMessage;
        this.recipients = recipients;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @SuppressWarnings("unused")
    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage() {
        return formattedMessage;
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
    public void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    @SuppressWarnings("unused")
    public void setRecipients(Set<Player> recipients) {
        this.recipients = recipients;
    }
}
