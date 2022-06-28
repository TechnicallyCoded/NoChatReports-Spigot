package ml.tcoded.nochatreports.hook;

public abstract class AbstractHook {

    public abstract void onInit();

    public abstract void onDisable();

    public void init() {
        this.onInit();
    }

    public void disable() {
        this.onDisable();
    }

}
