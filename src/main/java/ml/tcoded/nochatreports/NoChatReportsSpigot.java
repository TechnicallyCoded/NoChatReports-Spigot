package ml.tcoded.nochatreports;

import ml.tcoded.nochatreports.listener.ChatListener;
import ml.tcoded.nochatreports.listener.WhisperListener;
import ml.tcoded.nochatreports.util.UpdateUtil;
import net.minecraft.server.rcon.RemoteControlCommandListener;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class NoChatReportsSpigot extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new WhisperListener(), this);
        UpdateUtil.checkUpdate(this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public ConsoleCommandSender getConsoleSender() {
        return this.getServer().getConsoleSender();
    }
}
