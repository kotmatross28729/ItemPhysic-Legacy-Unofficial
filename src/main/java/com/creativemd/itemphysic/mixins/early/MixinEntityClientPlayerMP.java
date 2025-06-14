package com.creativemd.itemphysic.mixins.early;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.item.EntityItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.creativemd.itemphysic.config.ItemPhysicConfig;

@Mixin(value = EntityClientPlayerMP.class, priority = 1006)
public class MixinEntityClientPlayerMP {

    @Inject(method = "dropOneItem", at = @At(value = "HEAD"), cancellable = true)
    public void dropOneItem(boolean p_71040_1_, CallbackInfoReturnable<EntityItem> cir) {
        if (ItemPhysicConfig.customThrow) {
            cir.setReturnValue(null);
        }
    }

}
