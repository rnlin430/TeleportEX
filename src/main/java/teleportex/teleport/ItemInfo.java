package teleportex.teleport;

public class ItemInfo {

    private String name = "";
    private String lore = "";
    private String enchant = "";
    private String itemType = "";

    public void setEnchant(String enchant) {
        this.enchant = enchant;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public String getEnchant() {
        return enchant;
    }

    public String getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
