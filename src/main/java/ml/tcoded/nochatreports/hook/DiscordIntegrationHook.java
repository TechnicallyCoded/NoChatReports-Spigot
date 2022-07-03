package ml.tcoded.nochatreports.hook;

import com.dominikkorsa.discordintegration.Client;
import com.dominikkorsa.discordintegration.DiscordIntegration;
import discord4j.core.spec.WebhookExecuteSpec;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import ml.tcoded.nochatreports.NoChatReportsSpigot;
import ml.tcoded.nochatreports.event.AsyncPostNonReportableChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DiscordIntegrationHook extends AbstractHook implements Listener {

    private DiscordIntegration api;

    public DiscordIntegrationHook(NoChatReportsSpigot pluginIn) {
        super(pluginIn);
    }


    @Override
    public void onInit() {
        Plugin rawApi = this.plugin.getServer().getPluginManager().getPlugin("DiscordIntegration");
        if (!(rawApi instanceof DiscordIntegration fullApi)) return;
        this.api = fullApi;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.api = null;
    }

    @EventHandler
    public void onNonReportableChat(AsyncPostNonReportableChatEvent event) {
        String discordMsg = api.getDiscordFormatter().formatMessageContent(event.getMessage());
        Client client = api.getClient();

        Continuation<Unit> postWebhookContinuation = new ConsumerContinuation<>(o -> {

        });

        Continuation<WebhookExecuteSpec.Builder> postPlayerContinuation = new ConsumerContinuation<>(o -> {
            o.content(discordMsg);
            client.sendWebhook(o.build(), postWebhookContinuation);
        });

        client.getPlayerWebhookBuilder(event.getPlayer(), postPlayerContinuation);
    }

    private static class ConsumerContinuation<T> implements Continuation<T> {

        Consumer<T> consumer;

        public ConsumerContinuation(Consumer<T> consumerIn) {
            this.consumer = consumerIn;
        }

        @NotNull
        @Override
        public CoroutineContext getContext() {
            return EmptyCoroutineContext.INSTANCE;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void resumeWith(@NotNull Object o) {
            consumer.accept((T) o);
        }
    }
}
