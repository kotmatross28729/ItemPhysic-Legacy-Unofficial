package com.creativemd.itemphysic.mixins.late;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.item.EntityItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.creativemd.itemphysic.config.ItemPhysicConfig;
import com.creativemd.itemphysic.physics.ClientPhysic;

import tconstruct.tools.model.FancyItemRender;

@Mixin(value = FancyItemRender.class, priority = 456, remap = false)
public abstract class MixinTConstructItemRenderer extends Render {

    @Inject(method = "doRenderItem(Lnet/minecraft/entity/item/EntityItem;DDDFF)V", at = @At(value = "HEAD"))
    public void setUpRotations(EntityItem item, double x, double y, double z, float par8, float par9, CallbackInfo ci) {
        ClientPhysic.rotation = (double) (System.nanoTime() - ClientPhysic.tick) / 2_500_000
            * ItemPhysicConfig.rotationSpeed;
        if (!Minecraft.getMinecraft().inGameHasFocus) ClientPhysic.rotation = 0;
    }

    /**
     * @author GamingB3ast
     * @reason disable item floating
     */
    @Overwrite(remap = false)
    public boolean shouldBob() {
        return false;
    }
}
