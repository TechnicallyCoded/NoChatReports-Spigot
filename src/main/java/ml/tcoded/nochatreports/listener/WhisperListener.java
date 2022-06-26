package ml.tcoded.nochatreports.listener;

import ml.tcoded.nochatreports.util.ComponentUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class WhisperListener implements Listener {

    private final List<String> whisperCommands;

    public WhisperListener() {
        this.whisperCommands = List.of("w", "tell", "msg");
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onWhisperCmd(PlayerCommandPreprocessEvent event) {

        String rawMsg = event.getMessage();
        String[] parts = rawMsg.split(" ");

        // Check that msg command is valid
        if (parts.length < 3) return;

        String cmdPart = parts[0];
        String recipientPart = parts[1];
        String[] cmdParts = cmdPart.split(":");
        
        // Don't forget to remove extra spaces
        String msg = rawMsg.substring(cmdPart.length() + recipientPart.length() + 2);

        // No plugin specified
        if (cmdParts.length == 1) {

            // Allow compatibility with whisper command overrides
            // If registered ignore filtering && check if cmd is actually a whisper cmd
            if (Bukkit.getPluginCommand(cmdPart) == null && isWhisperCmd(cmdPart)) {
                sendWhisperMsg(event.getPlayer(), recipientPart, msg);
            }
        }
        else if (cmdParts[0].equalsIgnoreCase("minecraft") && isWhisperCmd(cmdParts[1])) {
            sendWhisperMsg(event.getPlayer(), recipientPart, msg);
        }

        // In all other cases another plugin seems to want to handle the whisper messages
    }

    private void sendWhisperMsg(Player sender, String recipient, String msg) {
        Player target = Bukkit.getPlayer(recipient);
        if (target == null) {
            TextComponent errComp = new TextComponent("No player was found");
            errComp.setColor(ChatColor.RED);
            sender.spigot().sendMessage(ChatMessageType.SYSTEM, errComp);
            return;
        }

        BaseComponent senderPrefix = new TextComponent("You whisper to ");
        senderPrefix.setColor(ChatColor.GRAY);
        senderPrefix.setItalic(true);

        BaseComponent senderComponent = ComponentUtil.createPlayerComponent(sender);
        BaseComponent targetComponent = ComponentUtil.createPlayerComponent(target);

        BaseComponent senderMsg = new TranslatableComponent("commands.message.display.outgoing", targetComponent, msg);
        senderMsg.setColor(ChatColor.GRAY);
        senderMsg.setItalic(true);

        BaseComponent recipientMsg = new TranslatableComponent("commands.message.display.incoming", senderComponent, msg);
        recipientMsg.setColor(ChatColor.GRAY);
        recipientMsg.setItalic(true);

        sender.spigot().sendMessage(ChatMessageType.CHAT, target.getUniqueId(), senderMsg);
        target.spigot().sendMessage(ChatMessageType.CHAT, sender.getUniqueId(), recipientMsg);
    }

    private boolean isWhisperCmd(String cmd) {
        return whisperCommands.contains(cmd.toLowerCase());
    }

}
