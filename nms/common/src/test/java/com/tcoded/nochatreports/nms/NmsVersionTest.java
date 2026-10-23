package com.tcoded.nochatreports.nms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NmsVersionTest {
    @Test
    void selects26_3ReleaseBuildAndLocalVersions() {
        for (String version : new String[]{"26.3", "26.3.build.211-stable", "26.3.local-SNAPSHOT"}) {
            assertEquals(NmsVersion.v26_3, NmsVersion.getNmsVersion(version), version);
            assertEquals("26.3", NmsVersion.getMinecraftVersion(version));
        }
        assertEquals(NmsVersion.v26_3, NmsVersion.getMostRecentVersion());
    }

    @Test
    void preservesLegacyVersionSelection() {
        assertEquals(NmsVersion.v26_2, NmsVersion.getNmsVersion("26.2.build.59-stable"));
        assertEquals(NmsVersion.v26_1_2, NmsVersion.getNmsVersion("26.1.2.build.20-stable"));
        assertEquals(NmsVersion.v1_21_10, NmsVersion.getNmsVersion("1.21.9-R0.1-SNAPSHOT"));
        assertEquals(NmsVersion.v1_19_4, NmsVersion.getNmsVersion("1.19.4-R0.1-SNAPSHOT"));
    }

    @Test
    void rejectsUnsupportedVersionsInsteadOfPickingLatest() {
        assertNull(NmsVersion.getNmsVersion("26.30"));
        assertNull(NmsVersion.getNmsVersion("26.4"));
        assertNull(NmsVersion.getNmsVersion("26.3.1"));
        assertNull(NmsVersion.getNmsVersion("unknown"));
        assertNull(NmsVersion.getNmsVersion(null));
    }
}
