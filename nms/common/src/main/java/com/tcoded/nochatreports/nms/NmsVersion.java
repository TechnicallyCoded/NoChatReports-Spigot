package com.tcoded.nochatreports.nms;

public enum NmsVersion {

    v26_1 ("26_1"),
    v1_21_11 ("1_21_11"),
    v1_21_10 ("1_21_(9|10)"),
    v1_21_8 ("1_21_8"),
    v1_21_5 ("1_21_5"),
    v1_21_4 ("1_21_4"),
    v1_21 ("1_21(_1)?"),
    v1_20_6 ("1_20_[5-6]"),
    v1_20_4 ("1_20_4"),
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