package com.tcoded.nochatreports.nms;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Constructor;

public abstract class NmsProvider {

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
}
