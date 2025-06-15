package com.creativemd.itemphysic.mixins.early;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.creativemd.itemphysic.config.ItemPhysicConfig;

@Mixin(value = EntityPlayer.class, priority = 456)
public abstract class MixinEntityPlayer extends EntityLivingBase implements ICommandSender {

    public MixinEntityPlayer(World p_i1594_1_) {
        super(p_i1594_1_);
    }

    @Shadow
    private void collideWithPlayer(Entity p_71044_1_) {}

    @Inject(method = "onLivingUpdate", at = @At(value = "TAIL"))
    public void onLivingUpdate(CallbackInfo ci) {
        AxisAlignedBB axisalignedbb = this.boundingBox.expand(
            ItemPhysicConfig.playerPickupExpansionXZ,
            ItemPhysicConfig.playerPickupExpansionY,
            ItemPhysicConfig.playerPickupExpansionXZ);
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);
        if (list != null) {
            for (Entity entity : list) {
                if (entity instanceof EntityItem && !entity.isDead) {
                    this.collideWithPlayer(entity);
                }
            }
        }
    }

}
