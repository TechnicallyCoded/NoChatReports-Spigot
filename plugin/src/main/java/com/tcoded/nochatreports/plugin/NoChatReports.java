package com.tcoded.nochatreports.plugin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.util.TimeStampMode;
import com.tcoded.lightlibs.updatechecker.SimpleUpdateChecker;
import com.tcoded.nochatreports.plugin.listener.ChatPacketListener;
import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.plugin.listener.KickListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class NoChatReports extends JavaPlugin {

    private static final int SPIGOT_RESOURCE_ID = 102931;
    private final List<Plugin> suspiciousPlugins;
    private NmsProvider nmsProvider;

    public NoChatReports() {
        this.suspiciousPlugins = new ArrayList<>();
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        // Check if we need to handle intentional misbehavior
        PluginManager pluginManager = this.getServer().getPluginManager();

        // ViaVersion is known to display fake and scary error banners - Trojan Horse? Manipulation?
        // Nothing more than lies, let's warn heavily against it. ViaVersion is blatantly malicious and deceptive. Yay!
        Plugin susPlugin1 = pluginManager.getPlugin("ViaVersion");
        if (susPlugin1 != null) {
            suspiciousPlugins.add(susPlugin1);
            this.getLogger().warning("\n" +
                    "***\n" +
                    "Yikes, ViaVersion detection!\n" +
                    "Please note that ViaVersion claims that dangerous issues are present when an anti-chat-reporting " +
                    "plugin is installed. Please ignore any fake and scary messages found below. Feel free to test for " +
                    "real incompatibilities, you are unlikely to find any.\n" +
                    "- Please report any *real* issues to my GitHub page :) -\n" +
                    "***");
        }
        // EssentialsX is known to warn about "unsupported" plugins that don't even mess with their code - Deceptive.
        // However, even if misleading, they at least don't blatantly lie to the user in their console with a MASSIVE
        // and scary banner like ViaVersion does. Their code does however mention anti-chat-reporting plugins as
        // "brain-dead" and "stupid". Plugins written with immature & ego-driven wording are *always* known to be very
        // reliable *cough*.
        Plugin susPlugin2 = pluginManager.getPlugin("Essentials");
        if (susPlugin2 != null) {
            suspiciousPlugins.add(susPlugin2);
            this.getLogger().warning("EssentialsX found. Please note that Essentials may claim that an " +
                    "anti-chat-reporting plugin is 'unsupported' when installed. Feel free to ignore any 'unsupported' " +
                    "messages below. And also feel free to test for incompatibilities, you are unlikely to find any.. " +
                    "But if you do, please report any issues to my GitHub :)");
        }


    }

    @Override
    public void onEnable() {
        // Utils
        String bukkitVersion = this.getServer().getBukkitVersion();
        String mcVersion = bukkitVersion.substring(0, bukkitVersion.indexOf("-"));

        getLogger().info("Loading support for Minecraft version: " + mcVersion);
        this.nmsProvider = NmsProvider.getNmsProvider(mcVersion);

        PacketEventsAPI<?> api = PacketEvents.getAPI();
        api.getSettings().debug(false).bStats(false).checkForUpdates(false).timeStampMode(TimeStampMode.MILLIS).reEncodeByDefault(true);
        api.init();

        // Listeners
        api.getEventManager().registerListener(new ChatPacketListener(this), PacketListenerPriority.NORMAL);
        this.getServer().getPluginManager().registerEvents(new KickListener(this.getConfig().getStringList("invalid-kick-reasons")), this);

        // Update checker
        SimpleUpdateChecker.checkUpdate(this, "[NoChatReports] ", SPIGOT_RESOURCE_ID);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        HandlerList.unregisterAll(this);
    }

    public ConsoleCommandSender getConsoleSender() {
        return this.getServer().getConsoleSender();
    }

    public NmsProvider getNmsProvider() {
        return this.nmsProvider;
    }

}
