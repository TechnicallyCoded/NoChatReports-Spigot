package ml.tcoded.nochatreports.hook;

import ml.tcoded.nochatreports.NoChatReportsSpigot;

public abstract class AbstractHook {

    protected final NoChatReportsSpigot plugin;

    public AbstractHook(NoChatReportsSpigot plugin) {
        this.plugin = plugin;
    }

    public abstract void onInit();

    public abstract void onDisable();

    public void init() {
        this.onInit();
        this.plugin.getLogger().info("- Loaded hook " + this.getClass().getSimpleName());
    }

    public void disable() {
        this.onDisable();
        this.plugin.getLogger().info("- Unloaded hook " + this.getClass().getSimpleName());
    }

}
