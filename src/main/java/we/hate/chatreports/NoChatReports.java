package we.hate.chatreports;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.util.TimeStampMode;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import we.hate.chatreports.listener.ChatPacketListener;
import we.hate.chatreports.util.UpdateUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class NoChatReports extends JavaPlugin {

    public NoChatReports() {
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PacketEventsAPI<?> api = PacketEvents.getAPI();
        api.getSettings().debug(false).bStats(false).checkForUpdates(false).timeStampMode(TimeStampMode.MILLIS).reEncodeByDefault(true);
        api.init();

        api.getEventManager().registerListener(new ChatPacketListener(this), PacketListenerPriority.NORMAL);
        UpdateUtil.checkUpdate(this);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        HandlerList.unregisterAll(this);
    }

    public ConsoleCommandSender getConsoleSender() {
        return this.getServer().getConsoleSender();
    }
}
