package net.zhuoweizhang.raspberryjuice;

public class BlockData {
    public final String name;
    public final int id;
    public final String minVersion;
    public final String maxVersion;
    public final String category;
    public final String group;
    public final String subgroup;
    public final float blastResistance;
    public final float hardness;
    public final boolean waterLoggable;
    public final boolean renewable;
    public final boolean stackable;
    public final String tool;
    public final boolean flammable;
    public final int luminance;

    public BlockData(String name, int id, String minVersion, String maxVersion, String category, String group, String subgroup, float blastResistance, float hardness, boolean waterLoggable, boolean renewable, boolean stackable, String tool, boolean flammable, int luminance) {
        this.name = name;
        this.id = id;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.category = category;
        this.group = group;
        this.subgroup = subgroup;
        this.blastResistance = blastResistance;
        this.hardness = hardness;
        this.waterLoggable = waterLoggable;
        this.renewable = renewable;
        this.stackable = stackable;
        this.tool = tool;
        this.flammable = flammable;
        this.luminance = luminance;
    }
}
