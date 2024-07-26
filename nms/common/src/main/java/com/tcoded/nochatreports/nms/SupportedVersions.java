package com.tcoded.nochatreports.nms;

public enum SupportedVersions {

    v_1_20_4,
    v_1_20_3,
//    v_1_20_2,
//    v_1_20_1,
    v_1_20,
    v_1_19_4,
//    v_1_19_3,
    v_1_19_2,
//    v_1_19_1
    ;

    public static SupportedVersions getNmsVersion(String version) {
        String versionWithUnderscores = version.replace(".", "_");
        SupportedVersions[] values = SupportedVersions.values();

        for (SupportedVersions nmsVersion : values) {
            if (nmsVersion.name().equalsIgnoreCase("v" + versionWithUnderscores)) {
                return nmsVersion;
            }
        }

        return null;
    }

    public static boolean isSupported(String version) {
        return getNmsVersion(version) != null;
    }

}
