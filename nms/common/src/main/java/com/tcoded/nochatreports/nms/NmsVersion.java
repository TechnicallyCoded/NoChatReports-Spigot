package com.tcoded.nochatreports.nms;

public enum NmsVersion {

    v1_20_4 ("1\\.20(\\.[4-])?"),
    v1_20 ("1\\.20(\\.[1-3])?"),
    v1_19_4 ("1\\.19\\.4"),
    v1_19_3 ("1\\.19\\.3"),
    v1_19_2 ("1\\.19\\.2"),
    v1_19_1 ("1.19.1")
    ;

    String[] versions;
    NmsVersion parent;

    NmsVersion(String... versions) {
        this.versions = versions;
    }

    public String[] getVersions() {
        return versions;
    }

    public static NmsVersion getNmsVersion(String version) {
        NmsVersion[] values = NmsVersion.values();

        for (NmsVersion nmsVersion : values) {
            for (String matchVersion : nmsVersion.getVersions()) {
                if (version.matches(matchVersion)) {
                    return nmsVersion;
                }
            }
        }

        return null;
    }

    public static NmsVersion getMostRecentVersion() {
        NmsVersion[] values = NmsVersion.values();
        return values[0];
    }
}