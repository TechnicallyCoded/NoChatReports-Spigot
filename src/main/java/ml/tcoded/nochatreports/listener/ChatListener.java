package ml.tcoded.nochatreports.listener;

import ml.tcoded.nochatreports.event.AsyncNonReportableChatEvent;
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

        AsyncNonReportableChatEvent noReportChatEvent = new AsyncNonReportableChatEvent(event.getPlayer(), event.getFormat(), event.getMessage(), event.getRecipients());
        Bukkit.getPluginManager().callEvent(noReportChatEvent);
        if (noReportChatEvent.isCancelled()) return;

        UUID uuid = noReportChatEvent.getPlayer().getUniqueId();
        String formattedMsg = String.format(noReportChatEvent.getFormat(), noReportChatEvent.getPlayer().getName(), noReportChatEvent.getMessage());
        BaseComponent msgComponent = new TextComponent(formattedMsg);

        for (Player player : noReportChatEvent.getRecipients()) {
            player.spigot().sendMessage(ChatMessageType.CHAT, uuid, msgComponent);
        }
    }

}
