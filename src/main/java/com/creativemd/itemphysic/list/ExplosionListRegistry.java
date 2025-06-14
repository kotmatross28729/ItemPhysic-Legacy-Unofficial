package com.creativemd.itemphysic.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.creativemd.itemphysic.config.ItemPhysicConfig;

import cpw.mods.fml.common.registry.GameRegistry;

public class ExplosionListRegistry {

    public static List<ExplosionList> ExplosionItems = new ArrayList<>();

    public static class ExplosionList {

        public final Item item;
        public final int metadata;
        public final boolean ignoremeta;

        public ExplosionList(Item item, int metadata, boolean ignoremeta) {
            this.item = item;
            this.metadata = metadata;
            this.ignoremeta = ignoremeta;
        }
    }

    public static void registerDefaults() {
        if (ItemPhysicConfig.explosionList != null) {
            for (String itemName : ItemPhysicConfig.explosionList) {
                String modId;
                String itemNameOnly;
                int metadata = 0;
                boolean ignoremeta = false;

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
                    }
                    Item item = GameRegistry.findItem(modId, itemNameOnly);
                    if (item != null) {
                        ExplosionListRegistry.ExplosionList Item = new ExplosionListRegistry.ExplosionList(
                            item,
                            metadata,
                            ignoremeta);
                        ExplosionListRegistry.ExplosionItems.add(Item);
                    }
                } else if (parts.length == 1) {
                    List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                    if (oredictNames.contains(itemName)) {
                        for (ItemStack oreStack : OreDictionary.getOres(itemName)) {
                            ExplosionListRegistry.ExplosionList Item = new ExplosionListRegistry.ExplosionList(
                                oreStack.getItem(),
                                oreStack.getItemDamage(),
                                ignoremeta);
                            ExplosionListRegistry.ExplosionItems.add(Item);
                        }
                    }
                }
            }
        }
    }
}
