package com.creativemd.itemphysic.config;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.creativemd.ingameconfigmanager.api.core.TabRegistry;
import com.creativemd.ingameconfigmanager.api.tab.ModTab;

public class ItemConfigSystem {

    public static ModTab tab = new ModTab("ItemPhysic", new ItemStack(Items.feather));
    public static ItemPhysicBranch branch;

    public static void loadConfig() {
        branch = new ItemPhysicBranch("general");
        tab.addBranch(branch);
        TabRegistry.registerModTab(tab);
    }
}
