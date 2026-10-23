package com.tcoded.nochatreports.plugin.util;

import com.tcoded.nochatreports.nms.NmsVersion;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ServerVersionTest {
    @Test
    void nativeGameVersionTakesPrecedenceOverApiBuildVersion() {
        String version = ServerVersion.readMinecraftVersion(Native263.class,
                () -> { throw new AssertionError("API version must not be read"); });
        assertEquals("26.3", version);
        assertEquals(NmsVersion.v26_3, NmsVersion.getNmsVersion(version));
    }

    @Test
    void keepsNative26_2Support() {
        String version = ServerVersion.readMinecraftVersion(Native262.class,
                () -> "26.3.local-SNAPSHOT");
        assertEquals(NmsVersion.v26_2, NmsVersion.getNmsVersion(version));
    }

    @Test
    void usesLegacyVersionOnlyWhenNativeApiDoesNotExist() {
        AtomicInteger calls = new AtomicInteger();
        String version = ServerVersion.readMinecraftVersion(LegacySpigot.class,
                () -> { calls.incrementAndGet(); return "1.19.4-R0.1-SNAPSHOT"; });
        assertEquals(1, calls.get());
        assertEquals(NmsVersion.v1_19_4, NmsVersion.getNmsVersion(version));
    }

    @Test
    void nativeFailureDoesNotSelectAnIncorrectLegacyAdapter() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> ServerVersion.readMinecraftVersion(BrokenNative.class,
                        () -> { throw new AssertionError("No fallback on native API failure"); }));
        assertInstanceOf(UnsupportedOperationException.class, error.getCause());
    }

    @Test
    void invalidNativeResultDoesNotFallBack() {
        for (Class<?> api : new Class<?>[]{NullNative.class, BlankNative.class, InvalidNative.class}) {
            assertThrows(IllegalStateException.class,
                    () -> ServerVersion.readMinecraftVersion(api,
                            () -> { throw new AssertionError("No fallback on invalid native result"); }));
        }
    }

    @Test
    void unknownNativeVersionRemainsUnsupported() {
        String version = ServerVersion.readMinecraftVersion(FutureNative.class,
                () -> "26.2.local-SNAPSHOT");
        assertEquals("26.30", version);
        assertNull(NmsVersion.getNmsVersion(version));
    }

    public static class Native263 { public static String getMinecraftVersion() { return "26.3"; } }
    public static class Native262 { public static String getMinecraftVersion() { return "26.2"; } }
    public static class LegacySpigot { }
    public static class BrokenNative { public static String getMinecraftVersion() { throw new UnsupportedOperationException(); } }
    public static class NullNative { public static String getMinecraftVersion() { return null; } }
    public static class BlankNative { public static String getMinecraftVersion() { return " "; } }
    public static class InvalidNative { public static int getMinecraftVersion() { return 263; } }
    public static class FutureNative { public static String getMinecraftVersion() { return "26.30"; } }
}
