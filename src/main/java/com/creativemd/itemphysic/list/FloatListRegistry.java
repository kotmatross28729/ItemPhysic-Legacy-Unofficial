package com.creativemd.itemphysic.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.creativemd.itemphysic.config.ItemPhysicConfig;

import cpw.mods.fml.common.registry.GameRegistry;

public class FloatListRegistry {

    public static List<FloatList> FloatItems = new ArrayList<>();

    public static class FloatList {

        public final Item item; // Item
        public final int metadata; // Meta
        public final boolean ignoremeta; // No meta
        public final String[] liquids; // In what liquid item float

        public FloatList(Item item, int metadata, boolean ignoremeta, String[] liquids) {
            this.item = item;
            this.metadata = metadata;
            this.ignoremeta = ignoremeta;
            this.liquids = liquids;
        }
    }

    public static void registerDefaults() {
        if (ItemPhysicConfig.floatList != null) {
            for (String itemName : ItemPhysicConfig.floatList) {
                String modId;
                String itemNameOnly;
                int metadata = 0;
                boolean ignoremeta = false;
                List<String> liquidsList = new ArrayList<>();
                liquidsList.add("fluid.tile.water"); // default

                String[] parts = itemName.split(":");
                if (parts.length >= 2) {
                    modId = parts[0];
                    itemNameOnly = parts[1];
                    if (parts.length == 3) {
                        try {
                            metadata = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            ignoremeta = Boolean.parseBoolean(parts[2]);
                        }
                        if (!ignoremeta && metadata == 0) {
                            liquidsList.set(0, parts[2]); // assert that ignoremeta missing (I assure you, no one will
                                                          // write
                            // :false)
                        }
                    } else if (parts.length > 3) {
                        try {
                            metadata = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            ignoremeta = Boolean.parseBoolean(parts[2]);
                        }
                        liquidsList.addAll(
                            Arrays.asList(parts)
                                .subList(3, parts.length));
                    }
                    Item item = GameRegistry.findItem(modId, itemNameOnly);
                    if (item != null) {
                        String[] liquidsArray = liquidsList.toArray(new String[0]);
                        FloatListRegistry.FloatList Item = new FloatListRegistry.FloatList(
                            item,
                            metadata,
                            ignoremeta,
                            liquidsArray);
                        FloatListRegistry.FloatItems.add(Item);
                    }
                } else if (parts.length == 1) {
                    List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                    if (oredictNames.contains(itemName)) {
                        for (ItemStack oreStack : OreDictionary.getOres(itemName)) {
                            String[] liquidsArray = liquidsList.toArray(new String[0]);
                            if (oreStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                                FloatListRegistry.FloatList Item = new FloatListRegistry.FloatList(
                                    oreStack.getItem(),
                                    oreStack.getItemDamage(),
                                    true,
                                    liquidsArray);
                                FloatListRegistry.FloatItems.add(Item);
                            } else {
                                FloatListRegistry.FloatList Item = new FloatListRegistry.FloatList(
                                    oreStack.getItem(),
                                    oreStack.getItemDamage(),
                                    ignoremeta,
                                    liquidsArray);
                                FloatListRegistry.FloatItems.add(Item);
                            }
                        }
                    }
                }
            }
        }
    }

}
