package ml.tcoded.nochatreports.hook;

import github.scarsz.discordsrv.DiscordSRV;
import ml.tcoded.nochatreports.NoChatReportsSpigot;
import ml.tcoded.nochatreports.event.AsyncPostNonReportableChatEvent;
import net.essentialsx.api.v2.services.discord.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class DiscordSRVHook extends AbstractHook implements Listener {

    private final MessageType channel = MessageType.DefaultTypes.CHAT;

    private DiscordSRV api;

    public DiscordSRVHook(NoChatReportsSpigot pluginIn) {
        super(pluginIn);
    }


    @Override
    public void onInit() {
        Plugin rawApi = this.plugin.getServer().getPluginManager().getPlugin("DiscordSRV");
        if (!(rawApi instanceof DiscordSRV fullApi)) return;
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
        if (DiscordSRV.config().getBooleanElse("UseModernPaperChatEvent", false)
                && DiscordSRV.getPlugin().isModernChatEventAvailable()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(DiscordSRV.getPlugin(), () ->
                DiscordSRV.getPlugin().processChatMessage(
                        event.getPlayer(),
                        event.getMessage(),
                        DiscordSRV.getPlugin().getOptionalChannel("global"),
                        false
                )
        );
    }
}
