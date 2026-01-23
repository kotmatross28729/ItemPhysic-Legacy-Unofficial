package com.creativemd.itemphysic.mixins.early;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;

import java.util.Random;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.creativemd.itemphysic.asm.ItemPhysicLateMixins;
import com.creativemd.itemphysic.helper.TC_Compat_WRAPPER;

@Mixin(value = ForgeHooksClient.class, priority = 456, remap = false)
public abstract class MixinForgeHooksClient {
    // Only for Tconstruct rendering

    @Inject(method = "renderEntityItem", at = @At(value = "HEAD"), cancellable = true)
    private static void renderEntityTConstructTool(EntityItem entity, ItemStack item, float bobing, float rotation,
        Random random, TextureManager engine, RenderBlocks renderBlocks, int count,
        CallbackInfoReturnable<Boolean> cir) {
        if (ItemPhysicLateMixins.IS_TC_PRESENT) {
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ENTITY);
            if (TC_Compat_WRAPPER.applyPatch(customRenderer, entity, item, engine, renderBlocks)) {
                cir.setReturnValue(true);
            }
        }
    }

}
