package com.creativemd.itemphysic.helper;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class TC_Compat_WRAPPER {

    public static boolean applyPatch(IItemRenderer customRenderer, EntityItem entity, ItemStack item,
        TextureManager engine, RenderBlocks renderBlocks) {
        return TC_Compat.applyPatch(customRenderer, entity, item, engine, renderBlocks);
    }

}
