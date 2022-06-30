package ml.tcoded.nochatreports.hook;

import ml.tcoded.nochatreports.NoChatReportsSpigot;
import ml.tcoded.nochatreports.event.AsyncPostNonReportableChatEvent;
import net.essentialsx.api.v2.services.discord.DiscordService;
import net.essentialsx.api.v2.services.discord.MessageType;
import net.essentialsx.discord.JDADiscordService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class EssentialsXDiscordHook extends AbstractHook implements Listener {

    private final MessageType channel = MessageType.DefaultTypes.CHAT;

    private JDADiscordService api;

    public EssentialsXDiscordHook(NoChatReportsSpigot pluginIn) {
        super(pluginIn);
    }


    @Override
    public void onInit() {
        DiscordService rawApi = this.plugin.getServer().getServicesManager().load(DiscordService.class);
        if (!(rawApi instanceof JDADiscordService fullApi)) return;
        this.api = fullApi;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.api = null;
    }

    @EventHandler
    public void onNonReportableChat(AsyncPostNonReportableChatEvent event) {
        api.sendChatMessage(event.getPlayer(), event.getMessage());
    }
}
