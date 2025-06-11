package com.creativemd.itemphysic.list;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;

public class ItemsWithMetaRegistrySulfuricAcid {

    public static List<ItemsWithMetaSulfuricAcid> SulfuricAcidItems = new ArrayList<>();

    public static class ItemsWithMetaSulfuricAcid {

        public final Item item;
        public final int metadata;
        public final boolean ignoremeta;

        public ItemsWithMetaSulfuricAcid(Item item, int metadata, boolean ignoremeta) {
            this.item = item;
            this.metadata = metadata;
            this.ignoremeta = ignoremeta;
        }
    }
}
