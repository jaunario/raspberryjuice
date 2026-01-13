package pi;

import net.zhuoweizhang.raspberryjuice.ApiManager;
import net.zhuoweizhang.raspberryjuice.ItemData;
import net.zhuoweizhang.raspberryjuice.VersionManager;

public class Item {
    private final ItemData itemData;

    public Item(String name) {
        if (ApiManager.getVersionManager() == null) {
            throw new IllegalStateException("VersionManager is not initialized.");
        }
        this.itemData = ApiManager.getVersionManager().getItemData(name);
        if (this.itemData == null) {
            throw new IllegalArgumentException("Item not found: " + name);
        }
    }

    public Item(int id) {
        if (ApiManager.getVersionManager() == null) {
            throw new IllegalStateException("VersionManager is not initialized.");
        }
        this.itemData = ApiManager.getVersionManager().getItemData(id);
        if (this.itemData == null) {
            throw new IllegalArgumentException("Item not found: " + id);
        }
    }

    public static Item fromId(int id) {
        return new Item(id);
    }

    public static Item fromName(String name) {
        return new Item(name);
    }

    public String getName() {
        return itemData.name;
    }

    public int getId() {
        return itemData.id;
    }

    public String getCategory() {
        return itemData.category;
    }

    public String getGroup() {
        return itemData.group;
    }

    public String getSubgroup() {
        return itemData.subgroup;
    }

    public int getStackability() {
        return itemData.stackability;
    }

    public boolean isRenewable() {
        return itemData.renewable;
    }

    public int getDamage() {
        return itemData.damage;
    }

    public int getDurability() {
        return itemData.durability;
    }

    public int getHunger() {
        return itemData.hunger;
    }

    public float getSaturation() {
        return itemData.saturation;
    }

    @Override
    public int hashCode() {
        return itemData.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Item)) {
            return false;
        }
        return itemData.id == ((Item) obj).itemData.id;
    }

    @Override
    public String toString() {
        return Integer.toString(itemData.id);
    }
}
