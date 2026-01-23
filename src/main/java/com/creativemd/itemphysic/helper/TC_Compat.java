package com.creativemd.itemphysic.helper;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.ENTITY_ROTATION;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.creativemd.itemphysic.physics.ClientPhysic;

import tconstruct.client.FlexibleToolRenderer;

public class TC_Compat {

    public static boolean applyPatch(IItemRenderer customRenderer, EntityItem entity, ItemStack item,
        TextureManager engine, RenderBlocks renderBlocks) {
        if (customRenderer instanceof FlexibleToolRenderer) {
            if (customRenderer.shouldUseRenderHelper(ENTITY, item, ENTITY_ROTATION)) {
                ClientPhysic.applyRotations(entity);
                GL11.glRotatef(entity.rotationPitch + 90, 1.0F, 0.0F, 0.0F);
            }
            engine.bindTexture(
                item.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);

            GL11.glScalef(0.5F, 0.5F, 0.5F);
            customRenderer.renderItem(ENTITY, item, renderBlocks, entity);
            return true;
        }
        return false;
    }

}
