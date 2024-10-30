package com.tcoded.nochatreports.nms;

public enum NmsVersion {

    v1_21_3 ("1_21_[23]"),
    v1_21 ("1_21(_1)?"),
    v1_20_6 ("1_20_[5-6]"),
    v1_20_4 ("1_20_4"),
    v1_20 ("1_20(_[1-3])?"),
    v1_19_4 ("1_19_4")
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

        String replacedVersion = version.replace(".", "_");
        for (NmsVersion nmsVersion : values) {
            for (String matchVersion : nmsVersion.getVersions()) {
                if (replacedVersion.matches(matchVersion)) {
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