package ml.tcoded.nochatreports;

import ml.tcoded.nochatreports.hook.AbstractHook;
import ml.tcoded.nochatreports.hook.EssentialsXDiscordHook;
import ml.tcoded.nochatreports.listener.ChatListener;
import ml.tcoded.nochatreports.listener.WhisperListener;
import ml.tcoded.nochatreports.util.UpdateUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@SuppressWarnings("unused")
public final class NoChatReportsSpigot extends JavaPlugin {

    private final HashMap<Class<? extends AbstractHook>, AbstractHook> hooks;

    public NoChatReportsSpigot() {
        this.hooks = new HashMap<>();
    }

    @Override
    public void onEnable() {
        initHooks();
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new WhisperListener(), this);
        UpdateUtil.checkUpdate(this);
    }

    private void initHooks() {
        if (this.getServer().getPluginManager().getPlugin("EssentialsXDiscord") != null) {
            this.hooks.put(EssentialsXDiscordHook.class, new EssentialsXDiscordHook(this));
        }
    }

    @Override
    public void onDisable() {
        this.hooks.values().forEach(AbstractHook::disable);
        this.hooks.clear();
        HandlerList.unregisterAll(this);
    }

    public ConsoleCommandSender getConsoleSender() {
        return this.getServer().getConsoleSender();
    }
}
