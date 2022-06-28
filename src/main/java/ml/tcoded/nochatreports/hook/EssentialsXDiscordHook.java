package ml.tcoded.nochatreports.hook;

import ml.tcoded.nochatreports.NoChatReportsSpigot;
import ml.tcoded.nochatreports.event.AsyncPostNonReportableChatEvent;
import net.essentialsx.api.v2.services.discord.DiscordService;
import net.essentialsx.api.v2.services.discord.MessageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class EssentialsXDiscordHook extends AbstractHook implements Listener {

    private final NoChatReportsSpigot plugin;
    private final MessageType channel = MessageType.DefaultTypes.CHAT;

    private DiscordService api;

    public EssentialsXDiscordHook(NoChatReportsSpigot pluginIn) {
        this.plugin = pluginIn;
    }


    @Override
    public void onInit() {
        this.api = this.plugin.getServer().getServicesManager().load(DiscordService.class);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.api = null;
    }

    @EventHandler
    public void onNonReportableChat(AsyncPostNonReportableChatEvent event) {
        api.sendMessage(channel, event.getMessage(), false);
    }
}
