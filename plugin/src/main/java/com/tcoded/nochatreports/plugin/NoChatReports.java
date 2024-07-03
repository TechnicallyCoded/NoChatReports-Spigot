package com.tcoded.nochatreports.plugin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.util.TimeStampMode;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.lightlibs.updatechecker.SimpleUpdateChecker;
import com.tcoded.nochatreports.plugin.listener.ChatPacketListener;
import com.tcoded.nochatreports.nms.NmsProvider;
import com.tcoded.nochatreports.plugin.listener.KickListener;
import com.tcoded.nochatreports.plugin.util.PluginUtil;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class NoChatReports extends JavaPlugin {

    private static final int SPIGOT_RESOURCE_ID = 102931;
    public final List<Plugin> pls;
    private NmsProvider<?> nmsProvider;
    private FoliaLib foliaLib;
    public boolean disWarn;

    public NoChatReports() {
        this.pls = new ArrayList<>();
    }

    @Override
    public void onLoad() {
        this.saveDefaultConfig();

        // Set defaults
        try {
            InputStream defaultConfigStream = this.getResource("config.yml");
            if (defaultConfigStream == null) throw new Exception("Default config not found!");
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
            this.getConfig().setDefaults(defaultConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        // Check if we need to handle intentional misbehavior
        PluginManager pluginManager = this.getServer().getPluginManager();

        // User choice is great, you know...
        disWarn = this.getConfig().getBoolean("disable-plugin-policing", true);

        // ViaVersion is known to display fake and scary error banners - Trojan Horse? Manipulation?
        // Nothing more than lies, let's warn heavily against it. ViaVersion is blatantly malicious and deceptive. Yay!
        Plugin via = PluginUtil.getPlugin(this,"ViaVersion");
        if (via != null) {
            pls.add(via);

            if (!disWarn) {
                this.getLogger().warning("\n" +
                        "***\n" +
                        "Yikes, ViaVersion detection!\n" +
                        "Please note that ViaVersion claims that dangerous issues are present when an anti-chat-reporting " +
                        "plugin is installed. Please ignore any fake and scary messages found below. Feel free to test for " +
                        "real incompatibilities, you are unlikely to find any.\n" +
                        "- Please report any *real* issues to my GitHub page :) -\n" +
                        "***");
            }


        }
        // EssentialsX is known to warn about "unsupported" plugins that don't even mess with their code - Deceptive.
        // However, even if misleading, they at least don't blatantly lie to the user in their console with a MASSIVE
        // and scary banner like ViaVersion does. Their code does however mention anti-chat-reporting plugins as
        // "brain-dead" and "stupid". Plugins written with immature & ego-driven wording are *always* known to be very
        // reliable *cough*.
        Plugin ess = PluginUtil.getPlugin(this,"Essentials");
        if (ess != null) {
            pls.add(ess);

            if (!disWarn) {
                this.getLogger().warning("EssentialsX note: Essentials may claim that an anti-chat-reporting plugin " +
                        "is 'unsupported' when installed. It is safe to ignore any 'unsupported' messages below. " +
                        "Feel free to test for incompatibilities, there are no known problems. " +
                        "If you do find a bug, please report any issues to my GitHub :)");
            }
        }
    }

    @Override
    public void onEnable() {
        // Utils
        this.foliaLib = new FoliaLib(this);
        String bukkitVersion = this.getServer().getBukkitVersion();
        String mcVersion = bukkitVersion.substring(0, bukkitVersion.indexOf("-"));

        getLogger().info("Loading support for Minecraft version: " + mcVersion);
        this.nmsProvider = NmsProvider.getNmsProvider(mcVersion, this.foliaLib.isFolia() || this.foliaLib.isPaper());

        PacketEventsAPI<?> api = PacketEvents.getAPI();
        api.getSettings().debug(false).bStats(false).checkForUpdates(false).timeStampMode(TimeStampMode.MILLIS).reEncodeByDefault(true);
        api.init();

        // Listeners
        api.getEventManager().registerListener(new ChatPacketListener(this), PacketListenerPriority.NORMAL);
        this.getServer().getPluginManager().registerEvents(new KickListener(
                this.getConfig().getString("prevented-kick-message"),
                this.getConfig().getStringList("invalid-kick-reasons")
        ), this);

        // Update checker
        Consumer<Runnable> asyncConsumer = runnable -> this.foliaLib.getScheduler().runAsync(wt -> runnable.run());
        SimpleUpdateChecker.checkUpdate(this, "[NoChatReports] ", SPIGOT_RESOURCE_ID, asyncConsumer);
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
