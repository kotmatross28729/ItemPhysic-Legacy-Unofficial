package com.creativemd.itemphysic.list;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ItemsWithMetaRegistryIgniting {

    public static List<ItemWithMetaIgniting> IgnitingItems = new ArrayList<>();

    public static class ItemWithMetaIgniting {

        public final Item item;
        public final int metadataItem;
        public final boolean ignoremetaItem;

        public final Block block;
        public final int metadataBlock;
        public final boolean ignoremetaBlock;

        public final int igniteChance;

        public ItemWithMetaIgniting(Item item, int metadataItem, boolean ignoremetaItem, Block block, int metadataBlock,
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
}
