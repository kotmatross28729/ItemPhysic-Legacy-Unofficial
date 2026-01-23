package com.creativemd.itemphysic.mixins.early;

import com.creativemd.itemphysic.physics.ClientPhysic;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tconstruct.client.FlexibleToolRenderer;

import java.util.Random;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.*;

@Mixin(value = ForgeHooksClient.class, priority = 456, remap = false)
public abstract class MixinForgeHooksClient {
    //Only for Tconstruct rendering

    @Inject(method = "renderEntityItem", at= @At(value = "HEAD"), cancellable = true)
    private static void renderEntityTConstructTool(EntityItem entity, ItemStack item, float bobing, float rotation, Random random, TextureManager engine, RenderBlocks renderBlocks, int count, CallbackInfoReturnable<Boolean> cir) {
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ENTITY);
        if (customRenderer == null) {
            cir.setReturnValue(false);
            return;
        }
        if (customRenderer instanceof FlexibleToolRenderer) {

            if (customRenderer.shouldUseRenderHelper(ENTITY, item, ENTITY_ROTATION)) {
                ClientPhysic.applyRotations(entity);
                GL11.glRotatef(entity.rotationPitch+90, 1.0F, 0.0F, 0.0F);
            }
            engine.bindTexture(item.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);

            GL11.glScalef(0.5F, 0.5F, 0.5F);
            customRenderer.renderItem(ENTITY, item, renderBlocks, entity);
            cir.setReturnValue(true);
        }
    }
}
