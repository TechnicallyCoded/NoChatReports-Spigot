package com.tcoded.nochatreports.nms;

import com.tcoded.nochatreports.nms.wrapper.PlayerChatPacket;
import com.tcoded.nochatreports.nms.wrapper.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public abstract class NmsProvider<T> {

    public static NmsProvider getNmsProvider(String minecraftVersion) {
        try {
            String versionName = NmsVersion.getNmsVersion(minecraftVersion).name();
            Class<?> providerName = Class.forName(
                    NmsProvider.class.getPackageName() + "." + versionName + ".NMS_" + versionName
            );
            Constructor<?> constructor = providerName.getConstructor();

            return (NmsProvider) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public abstract PlayerChatPacket wrapChatPacket(ByteBuf packet);

    public abstract void sendSystemPacket(Player player, SystemChatPacket systemPacket);

    public abstract T getNmsPlayer(Player player);

}
