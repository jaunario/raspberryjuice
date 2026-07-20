package net.zhuoweizhang.raspberryjuice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class VersionManager {

    private static final Logger LOGGER = Logger.getLogger(VersionManager.class.getName());
    private final Map<String, BlockData> blockDataMap = new HashMap<>();
    private final Map<String, ItemData> itemDataMap = new HashMap<>();
    private final Map<Integer, BlockData> idToBlockDataMap = new HashMap<>();
    private final Map<Integer, ItemData> idToItemDataMap = new HashMap<>();

    public VersionManager(InputStream blockCsvStream, InputStream itemCsvStream) {
        loadBlockData(blockCsvStream);
        loadItemData(itemCsvStream);
    }

    public void loadBlockData(InputStream csvStream) {
        blockDataMap.clear();
        idToBlockDataMap.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream))) {
            String line;
            // Skip header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = parseCsvLine(line);
                    if (parts.length < 15) continue; // Skip malformed lines

                    String name = parts[0].trim().toUpperCase();
                    int id = Integer.parseInt(parts[1].trim());
                    String minVersion = parts[2].trim();
                    String maxVersion = parts[3].trim();
                    String category = parts[4].trim();
                    String group = parts[5].trim();
                    String subgroup = parts[6].trim();
                    float blastResistance = Float.parseFloat(parts[7].trim());
                    float hardness = Float.parseFloat(parts[8].trim());
                    boolean waterLoggable = Boolean.parseBoolean(parts[9].trim());
                    boolean renewable = Boolean.parseBoolean(parts[10].trim());
                    boolean stackable = Boolean.parseBoolean(parts[11].trim());
                    String tool = parts[12].trim();
                    boolean flammable = Boolean.parseBoolean(parts[13].trim());
                    int luminance = Integer.parseInt(parts[14].trim());

                    BlockData blockData = new BlockData(name, id, minVersion, maxVersion, category, group, subgroup, blastResistance, hardness, waterLoggable, renewable, stackable, tool, flammable, luminance);
                    blockDataMap.put(name, blockData);
                    idToBlockDataMap.put(id, blockData);
                } catch (Exception e) {
                    LOGGER.warning("Skipping malformed line in blocks.csv: " + line);
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to load block data CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadItemData(InputStream csvStream) {
        itemDataMap.clear();
        idToItemDataMap.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream))) {
            String line;
            // Skip header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = parseCsvLine(line);
                    if (parts.length < 13) continue; // Skip malformed lines

                    String name = parts[0].trim().toUpperCase();
                    int id = Integer.parseInt(parts[1].trim());
                    String minVersion = parts[2].trim();
                    String maxVersion = parts[3].trim();
                    String category = parts[4].trim();
                    String group = parts[5].trim();
                    String subgroup = parts[6].trim();
                    int stackability = Integer.parseInt(parts[7].trim());
                    boolean renewable = Boolean.parseBoolean(parts[8].trim());
                    int damage = Integer.parseInt(parts[9].trim());
                    int durability = Integer.parseInt(parts[10].trim());
                    int hunger = Integer.parseInt(parts[11].trim());
                    float saturation = Float.parseFloat(parts[12].trim());

                    ItemData itemData = new ItemData(name, id, minVersion, maxVersion, category, group, subgroup, stackability, renewable, damage, durability, hunger, saturation);
                    itemDataMap.put(name, itemData);
                    idToItemDataMap.put(id, itemData);
                } catch (Exception e) {
                    LOGGER.warning("Skipping malformed line in items.csv: " + line);
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to load item data CSV: " + e.getMessage());
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
        BlockData data = blockDataMap.get(blockName.toUpperCase());
        if (data == null) {
            return false;
        }
        return isVersionCompatible(minecraftVersion, data.minVersion, data.maxVersion);
    }

    public boolean isItemAvailable(String itemName, String minecraftVersion) {
        ItemData data = itemDataMap.get(itemName.toUpperCase());
        if (data == null) {
            return false;
        }
        return isVersionCompatible(minecraftVersion, data.minVersion, data.maxVersion);
    }

    public BlockData getBlockData(String blockName) {
        return blockDataMap.get(blockName.toUpperCase());
    }

    public ItemData getItemData(String itemName) {
        return itemDataMap.get(itemName.toUpperCase());
    }

    public BlockData getBlockData(int id) {
        return idToBlockDataMap.get(id);
    }

    public ItemData getItemData(int id) {
        return idToItemDataMap.get(id);
    }

    private String[] parseCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        boolean inQuote = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuote = !inQuote;
            } else if (c == ',' && !inQuote) {
                parts.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        parts.add(sb.toString());
        return parts.toArray(new String[0]);
    }
}
