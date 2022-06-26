package ml.tcoded.nochatreports.listener;

import ml.tcoded.nochatreports.util.ComponentUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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

        String cmdPart = parts[0].substring(1).toLowerCase(); // remove "/"
        String recipientPart = parts[1];
        String[] cmdParts = cmdPart.split(":");
        
        // Don't forget to remove extra spaces and /
        String msg = rawMsg.substring(cmdPart.length() + recipientPart.length() + 3);

        // No plugin specified
        if (cmdParts.length == 1) {

            // Allow compatibility with whisper command overrides
            // If registered ignore filtering && check if cmd is actually a whisper cmd
            if (Bukkit.getPluginCommand(cmdPart) == null && isWhisperCmd(cmdPart)) {
                sendWhisperMsg(event.getPlayer(), cmdPart, recipientPart, msg);
                event.setCancelled(true);
            }
        }
        else if (cmdParts[0].equalsIgnoreCase("minecraft") && isWhisperCmd(cmdParts[1])) {
            sendWhisperMsg(event.getPlayer(), cmdParts[1], recipientPart, msg);
            event.setCancelled(true);
        }

        // In all other cases another plugin seems to want to handle the whisper messages
    }

    private void sendWhisperMsg(Player sender, String alias, String recipient, String msg) {

        if (recipient.toLowerCase().startsWith("@e")) {
            ComponentBuilder builder = new ComponentBuilder();
            TextComponent errComp = new TextComponent("Only players may be affected by this command, but the provided selector includes entities\n");
            errComp.setColor(ChatColor.RED);
            errComp.setUnderlined(false);

            TextComponent errDetails = new TextComponent(alias + " " + recipient + " " + msg);
            errDetails.setColor(ChatColor.RED);
            errDetails.setUnderlined(true);

            TextComponent pointer = new TextComponent("<--[HERE]");
            pointer.setColor(ChatColor.RED);
            pointer.setUnderlined(false);
            pointer.setItalic(true);

            builder.append(errComp);
            builder.append(errDetails);
            builder.append(pointer);

            sender.spigot().sendMessage(ChatMessageType.SYSTEM, builder.create());
            return;
        }

        List<Entity> targets = Bukkit.selectEntities(sender, recipient);
        if (targets.size() == 0) {
            TextComponent errComp = new TextComponent("No player was found");
            errComp.setColor(ChatColor.RED);
            sender.spigot().sendMessage(ChatMessageType.SYSTEM, errComp);
            return;
        }

        for (Entity entity : targets) {
            if (!(entity instanceof Player target)) continue;

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
    }

    private boolean isWhisperCmd(String cmd) {
        return whisperCommands.contains(cmd.toLowerCase());
    }

}
