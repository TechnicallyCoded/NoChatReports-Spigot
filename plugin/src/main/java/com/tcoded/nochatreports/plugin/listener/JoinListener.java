package com.tcoded.nochatreports.plugin.listener;

import com.tcoded.nochatreports.nms.channel.ChannelInjector;
import com.tcoded.nochatreports.plugin.NoChatReports;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    private final NoChatReports plugin;
    private final ChannelInjector channelInjector;

    public JoinListener(NoChatReports plugin, ChannelInjector channelInjector) {
        this.plugin = plugin;
        this.channelInjector = channelInjector;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getLogger().info("Setting up player: " + player.getName());
        this.channelInjector.inject(player); // Replace the injected handler, just to be sure
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.plugin.getLogger().info("Forgetting player: " + player.getName());
        this.channelInjector.uninject(player);
    }

}
