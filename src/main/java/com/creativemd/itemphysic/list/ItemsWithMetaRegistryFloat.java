package com.creativemd.itemphysic.list;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;

public class ItemsWithMetaRegistryFloat {

    public static List<ItemWithMetaFloat> FloatItems = new ArrayList<>();

    public static class ItemWithMetaFloat {

        public final Item item; // Item
        public final int metadata; // Meta
        public final boolean ignoremeta; // No meta
        public final String[] liquids; // In what liquid item float

        public ItemWithMetaFloat(Item item, int metadata, boolean ignoremeta, String[] liquids) {
            this.item = item;
            this.metadata = metadata;
            this.ignoremeta = ignoremeta;
            this.liquids = liquids;
        }
    }
}
