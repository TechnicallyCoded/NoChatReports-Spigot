package com.tcoded.nochatreports.plugin.util;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class ServerVersion {
    private ServerVersion() {
    }

    public static String getMinecraftVersion() {
        return readMinecraftVersion(Bukkit.class, Bukkit::getBukkitVersion);
    }

    static String readMinecraftVersion(Class<?> bukkitApi, Supplier<String> legacyVersion) {
        Method nativeVersion;
        try {
            // Paper exposes the game version separately from the API/build version.
            nativeVersion = bukkitApi.getMethod("getMinecraftVersion");
        } catch (NoSuchMethodException absentOnLegacySpigot) {
            return legacyVersion.get();
        }

        try {
            Object version = nativeVersion.invoke(null);
            if (!(version instanceof String) || ((String) version).isBlank()) {
                throw new IllegalStateException("The native Minecraft version is unavailable");
            }
            return (String) version;
        } catch (InvocationTargetException failure) {
            throw new IllegalStateException("Could not read the native Minecraft version", failure.getCause());
        } catch (IllegalAccessException failure) {
            throw new IllegalStateException("Could not access the native Minecraft version", failure);
        }
    }
}
