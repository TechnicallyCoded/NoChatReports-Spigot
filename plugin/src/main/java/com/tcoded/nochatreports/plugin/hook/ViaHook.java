package com.tcoded.nochatreports.plugin.hook;

import com.tcoded.nochatreports.plugin.NoChatReports;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ViaHook implements Listener {

    public static void hookIfPresent(NoChatReports plugin) {
        boolean showDetails = plugin.getConfig().getBoolean("details-for-unsupported-clients", true);
        if (!showDetails) return;

        // If via is enabled, let's fix some of the problems they create
        boolean viaEnabled = plugin.getServer().getPluginManager().isPluginEnabled("ViaVersion");
        if (!viaEnabled) return;

        hookVia(plugin);
    }

    private static void hookVia(NoChatReports plugin) {
        ViaHook viaHook = new ViaHook(plugin.getLogger(), plugin::isDebug);
        if (!viaHook.shouldRegister()) return;

        plugin.getServer().getPluginManager().registerEvents(viaHook, plugin);
    }

    // Not static to avoid class loading when calling static methods
    private final int SERVER_DATA_PACKET_MAX_PROTO = ProtocolVersion.v1_20_3.getVersion();

    private final Logger logger;
    private final ViaAPI api;
    private final int serverProto;

    private boolean alreadyReceivedDetails;
    private Supplier<Boolean> debugStateSupplier;

    public ViaHook(Logger logger, Supplier<Boolean> debugStateSupplier) {
        this.logger = logger;
        this.api = Via.getAPI();
        this.serverProto = api.getServerVersion().highestSupportedProtocolVersion().getVersion();
        this.debugStateSupplier = debugStateSupplier;
    }

    public boolean shouldRegister() {
        return serverProto <= SERVER_DATA_PACKET_MAX_PROTO;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        int playerProto;

        try {
            // Don't trust
            playerProto = api.getPlayerProtocolVersion(event.getPlayer()).getVersion();
        } catch (Exception e) {
            if (!this.debugStateSupplier.get()) return;
            e.printStackTrace();
            return;
        }

        if (playerProto > SERVER_DATA_PACKET_MAX_PROTO) {
            this.logger.info(event.getPlayer().getName() + " joined using ViaVersion on a higher version. They will see the insecure chat popup.");

            if (this.alreadyReceivedDetails) return;
            this.alreadyReceivedDetails = true;

            String msg = """
                Quick warning about clients on higher Minecraft version than your server:
                {PLAYER_NAME} joined using ViaVersion with a client version of 1.20.5 or higher.
                
                They will therefore see the scary insecure chat popup.
                This will NOT prevent NCR from blocking chat reports. Only the popup.
                These details will only be shown *once* per reboot and can also be disabled permanently in the config.
                
                Why the popup?
                Minecraft versions from 1.19 to 1.20.4 use a separate data packet to inform the clients of the chat reporting status.
                Minecraft versions starting from 1.20.6 and higher include this information in the login sequence.
                However, the data packet sent by 1.19-1.20.4 servers is sent after the player has logged in.
                ViaVersion is therefore unable to convert the information for 1.20.5(or +) clients since it is sent after it is needed.
                
                Updating the server version is the best way to resolve this.
                """.replace("{PLAYER_NAME}", event.getPlayer().getName());

            for (String line : msg.split("\n")) {
                this.logger.warning(line);
            }
        }
    }

}
