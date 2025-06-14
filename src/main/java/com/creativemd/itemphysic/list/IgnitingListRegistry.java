package com.creativemd.itemphysic.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.creativemd.itemphysic.config.ItemPhysicConfig;

import cpw.mods.fml.common.registry.GameRegistry;

public class IgnitingListRegistry {

    public static List<IgnitingList> IgnitingItems = new ArrayList<>();

    public static class IgnitingList {

        public final Item item;
        public final int metadataItem;
        public final boolean ignoremetaItem;

        public final Block block;
        public final int metadataBlock;
        public final boolean ignoremetaBlock;

        public final int igniteChance;

        public IgnitingList(Item item, int metadataItem, boolean ignoremetaItem, Block block, int metadataBlock,
            boolean ignoremetaBlock, int igniteChance) {
            this.item = item;
            this.metadataItem = metadataItem;
            this.ignoremetaItem = ignoremetaItem;
            this.block = block;
            this.metadataBlock = metadataBlock;
            this.ignoremetaBlock = ignoremetaBlock;
            this.igniteChance = igniteChance;
        }
    }

    public static void registerDefaults() {

        if (ItemPhysicConfig.ignitingItemsList != null) {
            for (String entry : ItemPhysicConfig.ignitingItemsList) {
                String modIdItem;
                String itemName;
                int metadataItem = 0;
                boolean ignoremetaItem = false;

                String modIdBlock;
                String blockName;
                int metadataBlock = 0;
                boolean ignoremetaBlock = false;

                int igniteChance = 10;

                String[] parts = entry.split(":");

                if (parts.length > 4) {
                    if (parts.length == 5) {
                        modIdItem = parts[0];
                        itemName = parts[1];
                        modIdBlock = parts[2];
                        blockName = parts[3];
                        try {
                            igniteChance = Integer.parseInt(parts[4]);
                        } catch (NumberFormatException ignored) {}
                    } else if (parts.length == 6) {
                        modIdItem = parts[0];
                        itemName = parts[1];
                        try {
                            metadataItem = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            ignoremetaItem = Boolean.parseBoolean(parts[2]);
                        }
                        if (metadataItem == 0 && !ignoremetaItem) {
                            modIdBlock = parts[2];
                            blockName = parts[3];
                            try {
                                metadataItem = Integer.parseInt(parts[4]);
                            } catch (NumberFormatException e) {
                                ignoremetaItem = Boolean.parseBoolean(parts[4]);
                            }
                        } else {
                            modIdBlock = parts[3];
                            blockName = parts[4];
                        }
                        try {
                            igniteChance = Integer.parseInt(parts[5]);
                        } catch (NumberFormatException ignored) {}
                    } else {
                        modIdItem = parts[0];
                        itemName = parts[1];
                        try {
                            metadataItem = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            ignoremetaItem = Boolean.parseBoolean(parts[2]);
                        }
                        modIdBlock = parts[3];
                        blockName = parts[4];
                        try {
                            metadataBlock = Integer.parseInt(parts[5]);
                        } catch (NumberFormatException e) {
                            ignoremetaBlock = Boolean.parseBoolean(parts[5]);
                        }
                        try {
                            igniteChance = Integer.parseInt(parts[6]);
                        } catch (NumberFormatException ignored) {}
                    }
                    Item item = GameRegistry.findItem(modIdItem, itemName);
                    Block block = GameRegistry.findBlock(modIdBlock, blockName);

                    if (item != null && block != null) {
                        IgnitingListRegistry.IgnitingList ItemAndBlock = new IgnitingListRegistry.IgnitingList(
                            item,
                            metadataItem,
                            ignoremetaItem,
                            block,
                            metadataBlock,
                            ignoremetaBlock,
                            igniteChance);
                        IgnitingListRegistry.IgnitingItems.add(ItemAndBlock);
                    }
                } else if (parts.length == 4) {
                    String oreDict = parts[0];
                    modIdBlock = parts[1];
                    blockName = parts[2];
                    try {
                        igniteChance = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException ignored) {}

                    Block block = GameRegistry.findBlock(modIdBlock, blockName);

                    List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                    if (oredictNames.contains(oreDict)) {
                        for (ItemStack oreStack : OreDictionary.getOres(oreDict)) {
                            IgnitingListRegistry.IgnitingList Item = new IgnitingListRegistry.IgnitingList(
                                oreStack.getItem(),
                                oreStack.getItemDamage(),
                                ignoremetaItem,
                                block,
                                metadataBlock,
                                ignoremetaBlock,
                                igniteChance);
                            IgnitingListRegistry.IgnitingItems.add(Item);
                        }
                    }
                }
            }
        }
    }
}
