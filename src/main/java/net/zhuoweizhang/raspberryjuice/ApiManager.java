package net.zhuoweizhang.raspberryjuice;

public class ApiManager {
    private static VersionManager versionManager;

    public static VersionManager getVersionManager() {
        return versionManager;
    }

    public static void setVersionManager(VersionManager versionManager) {
        ApiManager.versionManager = versionManager;
    }
}
