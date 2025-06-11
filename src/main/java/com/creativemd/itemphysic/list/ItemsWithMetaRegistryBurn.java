package com.creativemd.itemphysic.list;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;

public class ItemsWithMetaRegistryBurn {

    public static List<ItemWithMetaBurn> BurnItems = new ArrayList<>();

    public static class ItemWithMetaBurn {

        public final Item item;
        public final int metadata;
        public final boolean ignoremeta;

        public ItemWithMetaBurn(Item item, int metadata, boolean ignoremeta) {
            this.item = item;
            this.metadata = metadata;
            this.ignoremeta = ignoremeta;
        }
    }
}
