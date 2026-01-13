package net.zhuoweizhang.raspberryjuice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class VersionManager {

    private static final Logger LOGGER = Logger.getLogger(VersionManager.class.getName());
    private final Map<String, VersionData> dataMap = new HashMap<>();

    private static class VersionData {
        final int id;
        final String minVersion;
        final String maxVersion;

        VersionData(int id, String minVersion, String maxVersion) {
            this.id = id;
            this.minVersion = minVersion;
            this.maxVersion = maxVersion;
        }
    }

    public VersionManager(InputStream csvStream) {
        loadData(csvStream);
    }

    public void loadData(InputStream csvStream) {
        dataMap.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream))) {
            String line;
            // Skip header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue; // Skip malformed lines

                String name = parts[0].trim().toUpperCase();
                int id;
                try {
                    id = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    LOGGER.warning("Skipping invalid ID for " + name + ": " + parts[1]);
                    continue;
                }

                String minVersion = (parts.length > 2 && !parts[2].trim().isEmpty()) ? parts[2].trim() : "0.0";
                String maxVersion = (parts.length > 3 && !parts[3].trim().isEmpty()) ? parts[3].trim() : null;

                dataMap.put(name, new VersionData(id, minVersion, maxVersion));
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to load version data CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isVersionCompatible(String version, String minVersion, String maxVersion) {
        String parsedVersion = version;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\(MC: (\\d+\\.\\d+(\\.\\d+)?)\\)").matcher(version);
        if (matcher.find()) {
            parsedVersion = matcher.group(1);
        }

        if (compareVersions(parsedVersion, minVersion) < 0) {
            return false;
        }
        if (maxVersion != null && compareVersions(parsedVersion, maxVersion) > 0) {
            return false;
        }
        return true;
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int p1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
            int p2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;
            if (p1 < p2) return -1;
            if (p1 > p2) return 1;
        }
        return 0;
    }

    public boolean isBlockAvailable(String blockName, String minecraftVersion) {
        VersionData data = dataMap.get(blockName.toUpperCase());
        if (data == null) {
            return false;
        }
        return isVersionCompatible(minecraftVersion, data.minVersion, data.maxVersion);
    }

    public boolean isItemAvailable(String itemName, String minecraftVersion) {
        return isBlockAvailable(itemName, minecraftVersion);
    }

    public int getBlockId(String blockName) {
        VersionData data = dataMap.get(blockName.toUpperCase());
        return (data != null) ? data.id : -1;
    }

    public int getItemId(String itemName) {
        return getBlockId(itemName);
    }
}
