package ml.tcoded.nochatreports.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import org.bukkit.entity.Player;

public class ComponentUtil {

    public static TextComponent createPlayerComponent(Player player) {
        TextComponent playerComponent = new TextComponent(player.getName());

        playerComponent.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_ENTITY,
                        new Entity(
                                "player",
                                player.getUniqueId().toString(),
                                new TextComponent(player.getName())
                        )));

        playerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + player.getName() + " "));

        return playerComponent;
    }

}
