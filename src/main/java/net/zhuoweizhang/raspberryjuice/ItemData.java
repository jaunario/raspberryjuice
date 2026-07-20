package net.zhuoweizhang.raspberryjuice;

public class ItemData {
    public final String name;
    public final int id;
    public final String minVersion;
    public final String maxVersion;
    public final String category;
    public final String group;
    public final String subgroup;
    public final int stackability;
    public final boolean renewable;
    public final int damage;
    public final int durability;
    public final int hunger;
    public final float saturation;

    public ItemData(String name, int id, String minVersion, String maxVersion, String category, String group, String subgroup, int stackability, boolean renewable, int damage, int durability, int hunger, float saturation) {
        this.name = name;
        this.id = id;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.category = category;
        this.group = group;
        this.subgroup = subgroup;
        this.stackability = stackability;
        this.renewable = renewable;
        this.damage = damage;
        this.durability = durability;
        this.hunger = hunger;
        this.saturation = saturation;
    }
}
