package com.tcoded.nochatreports.nms;

import com.tcoded.nochatreports.nms.channel.ChannelInjector;
import com.tcoded.nochatreports.nms.channel.GlobalPacketHandler;
import com.tcoded.nochatreports.nms.types.NmsProviderConfig;
import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public abstract class NmsProvider<T> {

    public static NmsProvider getNmsProvider(String minecraftVersion, NmsProviderConfig config) {
        try {
            String versionName = NmsVersion.getNmsVersion(minecraftVersion).name();
            Class<?> providerName = Class.forName(
                    NmsProvider.class.getPackageName() + "." + versionName + ".NmsProviderImpl"
            );
            Constructor<?> constructor = providerName.getConstructor(NmsProviderConfig.class);

            return (NmsProvider) constructor.newInstance(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private final NmsProviderConfig config;

    public NmsProvider(NmsProviderConfig config) {
        this.config = config;
    }

    public abstract PlayerChatPacket wrapChatPacket(ByteBuf packet);

    public abstract void sendSystemPacket(Player player, SystemChatPacket systemPacket);

    public abstract T getNmsPlayer(Player player);

    public abstract GlobalPacketHandler getGlobalPacketHandler();

    public abstract ChannelInjector getChannelInjector();

    public abstract void registerListeners();

    /**
     * Wrap NMS packet object to PlayerChatPacket object
     * @param packet NMS packet object (ClientboundPlayerChatPacket, for example)
     * @return PlayerChatPacket object
     */
    public abstract PlayerChatPacket wrapChatPacket(Object packet);

    public NmsProviderConfig getConfig() {
        return config;
    }

    public boolean isDebug() {
        return this.config.debugStateSupplier().get();
    }

}
