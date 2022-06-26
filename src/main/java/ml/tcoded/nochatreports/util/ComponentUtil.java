package ml.tcoded.nochatreports.util;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;
import org.bukkit.entity.Player;

public class ComponentUtil {

    public static TextComponent createPlayerComponent(Player player) {
        TextComponent playerComponent = new TextComponent(player.getName());
        playerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new Entity("Player", player.getUniqueId().toString(), new TextComponent(player.getName()))));
        return playerComponent;
    }

}
