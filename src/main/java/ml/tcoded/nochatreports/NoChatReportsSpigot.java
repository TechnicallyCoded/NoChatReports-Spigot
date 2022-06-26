package ml.tcoded.nochatreports;

import ml.tcoded.nochatreports.listener.ChatListener;
import ml.tcoded.nochatreports.listener.WhisperListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class NoChatReportsSpigot extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new WhisperListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
