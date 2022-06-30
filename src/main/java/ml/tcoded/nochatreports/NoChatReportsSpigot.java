package ml.tcoded.nochatreports;

import ml.tcoded.nochatreports.hook.AbstractHook;
import ml.tcoded.nochatreports.hook.EssentialsXDiscordHook;
import ml.tcoded.nochatreports.listener.ChatListener;
import ml.tcoded.nochatreports.listener.WhisperListener;
import ml.tcoded.nochatreports.util.UpdateUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
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
        this.getLogger().info("Loading plugin hooks...");
        if (this.getServer().getPluginManager().getPlugin("EssentialsDiscord") != null) {
            this.hooks.put(EssentialsXDiscordHook.class, new EssentialsXDiscordHook(this));
        }

        this.hooks.values().forEach(AbstractHook::init);
        this.getLogger().info("Loaded " + this.hooks.size() + " plugin hooks!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Unloading plugin hooks...");
        this.hooks.values().forEach(AbstractHook::disable);
        this.hooks.clear();
        this.getLogger().info("Unloaded all plugin hooks!");
        HandlerList.unregisterAll(this);
    }

    public ConsoleCommandSender getConsoleSender() {
        return this.getServer().getConsoleSender();
    }
}
