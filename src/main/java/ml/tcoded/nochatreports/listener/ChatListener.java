package ml.tcoded.nochatreports.listener;

import ml.tcoded.nochatreports.event.AsyncNonReportableChatEvent;
import ml.tcoded.nochatreports.event.AsyncPostNonReportableChatEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {

    @SuppressWarnings("DefaultAnnotationParam")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        // Fire event with information provided to the NoChatReports plugin
        AsyncNonReportableChatEvent noReportChatEvent = new AsyncNonReportableChatEvent(event.isAsynchronous(), event.getPlayer(), event.getFormat(), event.getMessage(), event.getRecipients());
        Bukkit.getPluginManager().callEvent(noReportChatEvent);
        if (noReportChatEvent.isCancelled()) return;

        // Parse result
        UUID uuid = noReportChatEvent.getPlayer().getUniqueId();
        String formattedMsg = String.format(noReportChatEvent.getFormat(), noReportChatEvent.getPlayer().getName(), noReportChatEvent.getMessage());
        BaseComponent msgComponent = new TextComponent(formattedMsg);

        // Log chat to console since cancelling the event causes the chat to not be logged
        Bukkit.getLogger().info(formattedMsg);

        // Send the message to recipients
        for (Player player : noReportChatEvent.getRecipients()) {
            player.spigot().sendMessage(ChatMessageType.CHAT, uuid, msgComponent);
        }

        // Fire event with final message that was sent to the players
        AsyncPostNonReportableChatEvent postNoReportChatEvent = new AsyncPostNonReportableChatEvent(event.isAsynchronous(), noReportChatEvent.getPlayer(), formattedMsg, noReportChatEvent.getRecipients());
        Bukkit.getPluginManager().callEvent(postNoReportChatEvent);
    }

}
