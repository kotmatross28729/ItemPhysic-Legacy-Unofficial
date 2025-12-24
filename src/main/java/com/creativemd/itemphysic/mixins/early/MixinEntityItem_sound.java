package com.creativemd.itemphysic.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.creativemd.itemphysic.config.ItemPhysicConfig;

@Mixin(value = EntityItem.class, priority = 456)
public class MixinEntityItem_sound {

    @Unique
    boolean itemPhysic$hasPlayedSound = false;

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/item/EntityItem;onGround:Z",
            opcode = Opcodes.GETFIELD,
            ordinal = 0))
    public void addFallSound(CallbackInfo ci) {
        EntityItem itemPhysic$thiz = ((EntityItem) ((Object) this));

        if (!itemPhysic$thiz.onGround) {
            itemPhysic$hasPlayedSound = false;
            return;
        }

        if (!itemPhysic$hasPlayedSound && ItemPhysicConfig.enableFallSounds) {
            itemPhysic$hasPlayedSound = true;

            String soundName = ItemPhysicConfig.itemFallSound;

            if (ItemPhysicConfig.enableMaterialSensitiveFallSounds) {
                int x = MathHelper.floor_double(itemPhysic$thiz.posX);
                int y = MathHelper.floor_double(itemPhysic$thiz.posY - 0.5D);
                int z = MathHelper.floor_double(itemPhysic$thiz.posZ);
                Block blockUnder = itemPhysic$thiz.worldObj.getBlock(x, y, z);

                if (blockUnder != null && blockUnder.getMaterial() != Material.air) {
                    soundName = blockUnder.stepSound.getStepResourcePath();
                }
            }

            float pitch = 1.0F + itemPhysic$thiz.worldObj.rand.nextFloat();
            itemPhysic$thiz.playSound(soundName, ItemPhysicConfig.itemFallSoundVolume, pitch);
        }
    }

}
