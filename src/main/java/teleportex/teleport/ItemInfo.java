package teleportex.teleport;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ItemInfo implements ConfigurationSerializable {

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

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map =new HashMap<>();
        map.put("name", name);
        map.put("lore", lore);
        map.put("enchant", enchant);
        map.put("itemType", itemType);
        return map;
    }

    public static ItemInfo deserialize(Map<String,Object> map){
        ItemInfo iteminfo =new ItemInfo();

        //「キー」で「値」を取り出してオブジェクトにセット
        iteminfo.setName(map.get("name").toString());
        iteminfo.setLore(map.get("lore").toString());
        iteminfo.setEnchant(map.get("enchant").toString());
        iteminfo.setItemType(map.get("itemType").toString());

        return iteminfo;
    }
}
