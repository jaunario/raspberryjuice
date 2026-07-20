package pi;

import net.zhuoweizhang.raspberryjuice.ApiManager;
import net.zhuoweizhang.raspberryjuice.BlockData;
import net.zhuoweizhang.raspberryjuice.VersionManager;

public class Block {
    private final BlockData blockData;
    private final int data;

    public Block(String name, int data) {
        if (ApiManager.getVersionManager() == null) {
            throw new IllegalStateException("VersionManager is not initialized.");
        }
        this.blockData = ApiManager.getVersionManager().getBlockData(name);
        if (this.blockData == null) {
            throw new IllegalArgumentException("Block not found: " + name);
        }
        this.data = data & 0xf;
    }

    public Block(int id, int data) {
        if (ApiManager.getVersionManager() == null) {
            throw new IllegalStateException("VersionManager is not initialized.");
        }
        this.blockData = ApiManager.getVersionManager().getBlockData(id);
        if (this.blockData == null) {
            throw new IllegalArgumentException("Block not found: " + id);
        }
        this.data = data & 0xf;
    }

    public static Block fromId(int id) {
        return new Block(id, 0);
    }

    public static Block fromName(String name) {
        return new Block(name, 0);
    }

    public Block withData(int data) {
        return new Block(blockData.id, data);
    }

    public String getName() {
        return blockData.name;
    }

    public int getId() {
        return blockData.id;
    }

    public String getCategory() {
        return blockData.category;
    }

    public String getGroup() {
        return blockData.group;
    }

    public String getSubgroup() {
        return blockData.subgroup;
    }

    public float getBlastResistance() {
        return blockData.blastResistance;
    }

    public float getHardness() {
        return blockData.hardness;
    }

    public boolean isWaterLoggable() {
        return blockData.waterLoggable;
    }

    public boolean isRenewable() {
        return blockData.renewable;
    }

    public boolean isStackable() {
        return blockData.stackable;
    }

    public String getTool() {
        return blockData.tool;
    }

    public boolean isFlammable() {
        return blockData.flammable;
    }

    public int getLuminance() {
        return blockData.luminance;
    }

    @Override
    public int hashCode() {
        return (blockData.id << 8) + data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Block)) {
            return false;
        }
        return hashCode() == ((Block) obj).hashCode();
    }

    @Override
    public String toString() {
        return blockData.id + (data == 0 ? "" : "," + data);
    }
}
