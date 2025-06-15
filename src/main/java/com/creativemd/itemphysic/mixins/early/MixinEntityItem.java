package com.creativemd.itemphysic.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fluids.Fluid;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.creativemd.itemphysic.config.ItemPhysicConfig;
import com.creativemd.itemphysic.physics.ServerPhysic;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

@Mixin(value = EntityItem.class, priority = 456)
public abstract class MixinEntityItem extends Entity {

    public MixinEntityItem(World worldIn) {
        super(worldIn);
    }

    @Unique
    Fluid itemPhysic$fluid;
    @Unique
    float itemPhysic$f = 0.98F;
    @Unique
    ItemStack itemPhysic$stack;
    @Unique
    EntityItem itemPhysic$thiz;

    @Inject(method = "onUpdate", at = @At(value = "HEAD"))
    public void initFields(CallbackInfo ci) {
        itemPhysic$thiz = (EntityItem) ((Object) this);
        itemPhysic$fluid = ServerPhysic.getFluid(itemPhysic$thiz);
        itemPhysic$stack = (itemPhysic$thiz).getDataWatcher()
            .getWatchableObjectItemStack(10);
    }

    @WrapWithCondition(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/item/EntityItem.motionY : D",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0))
    private boolean disableMotionYNoFluid(EntityItem instance, double newValue) {
        return itemPhysic$fluid == null;
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/item/EntityItem.func_145771_j (DDD)Z",
            shift = At.Shift.BEFORE))
    public void fluidPhysics(CallbackInfo ci) {
        if (itemPhysic$fluid != null) {
            double density = (double) itemPhysic$fluid.getDensity() / 1000D;
            double speed = -1 / density * 0.01;

            if (ServerPhysic.canItemSwim(itemPhysic$stack, itemPhysic$fluid)) speed = 0.05;

            double speedreduction = (speed - itemPhysic$thiz.motionY) / 2;
            double maxSpeedReduction = 0.05;

            if (speedreduction < -maxSpeedReduction) speedreduction = -maxSpeedReduction;
            if (speedreduction > maxSpeedReduction) speedreduction = maxSpeedReduction;

            itemPhysic$thiz.motionY += speedreduction;
            itemPhysic$f = (float) (1D / density / 1.2);
        }
    }

    @WrapWithCondition(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/item/EntityItem.motionY : D",
            opcode = Opcodes.PUTFIELD,
            ordinal = 1))
    private boolean disableMotionY(EntityItem instance, double newValue) {
        return false;
    }

    @WrapWithCondition(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/item/EntityItem.motionX : D",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0))
    private boolean disableMotionX(EntityItem instance, double newValue) {
        return false;
    }

    @WrapWithCondition(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/item/EntityItem.motionZ : D",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0))
    private boolean disableMotionZ(EntityItem instance, double newValue) {
        return false;
    }

    @WrapWithCondition(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/item/EntityItem.playSound (Ljava/lang/String;FF)V",
            ordinal = 0))
    private boolean checkBurnSound(EntityItem instance, String name, float volume, float pitch) {
        return ServerPhysic.canItemBurn(itemPhysic$stack);
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/item/EntityItem.playSound (Ljava/lang/String;FF)V",
            ordinal = 0,
            shift = At.Shift.AFTER))
    public void addBurnParticles(CallbackInfo ci) {
        if (ServerPhysic.canItemBurn(itemPhysic$stack)) {
            for (int zahl = 0; zahl < 75; zahl++) itemPhysic$thiz.worldObj.spawnParticle(
                "smoke",
                itemPhysic$thiz.posX,
                itemPhysic$thiz.posY,
                itemPhysic$thiz.posZ,
                (this.rand.nextFloat() * 0.1) - 0.05,
                0.2 * this.rand.nextDouble(),
                (this.rand.nextFloat() * 0.1) - 0.05);
        }
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/world/World.isRemote:Z",
            opcode = Opcodes.GETFIELD,
            ordinal = 0))
    public void addIgniting(CallbackInfo ci) {
        Block block = itemPhysic$thiz.worldObj.getBlock(
            MathHelper.floor_double(itemPhysic$thiz.posX),
            MathHelper.floor_double(itemPhysic$thiz.posY),
            MathHelper.floor_double(itemPhysic$thiz.posZ));

        if (itemPhysic$thiz.onGround && ServerPhysic.canItemIgnite(itemPhysic$stack)
            && (block.getMaterial() == Material.air || block.getMaterial() == Material.plants
                || block.getMaterial() == Material.vine
                || block.getMaterial() == Material.carpet)

            && itemPhysic$thiz.worldObj.rand.nextInt(100) <= ServerPhysic.getIgnitingChance(itemPhysic$stack)) {
            itemPhysic$thiz.worldObj.setBlock(
                MathHelper.floor_double(itemPhysic$thiz.posX),
                MathHelper.floor_double(itemPhysic$thiz.posY),
                MathHelper.floor_double(itemPhysic$thiz.posZ),
                ServerPhysic.getIgnitingBlock(itemPhysic$stack),
                ServerPhysic.getIgnitingBlockMeta(itemPhysic$stack),
                3);
        }
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/item/EntityItem.onGround : Z",
            opcode = Opcodes.GETFIELD,
            ordinal = 0))
    public void addFallSound(CallbackInfo ci) {
        if (itemPhysic$thiz.onGround && itemPhysic$thiz.prevPosY != itemPhysic$thiz.posY
            && ItemPhysicConfig.enableFallSounds) {
            itemPhysic$thiz.playSound("dig.cloth", 1F, (float) Math.random() + 1);
        }
    }

    @WrapWithCondition(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/item/EntityItem.motionY : D",
            opcode = Opcodes.PUTFIELD,
            ordinal = 2))
    private boolean checkBurnSound(EntityItem instance, double newValue) {
        return itemPhysic$fluid == null;
    }

    @WrapWithCondition(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/item/EntityItem.motionY : D",
            opcode = Opcodes.PUTFIELD,
            ordinal = 3))
    private boolean disableMotionYNoFluid2(EntityItem instance, double newValue) {
        return itemPhysic$fluid == null;
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/item/EntityItem.age : I",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0),
        cancellable = true)
    public void customDespawn(CallbackInfo ci) {
        if (ItemPhysicConfig.enableItemDespawn) {
            ++itemPhysic$thiz.age;
            if (itemPhysic$thiz.lifespan == 6000 && itemPhysic$thiz.lifespan != ItemPhysicConfig.despawnItem) {
                itemPhysic$thiz.lifespan = ItemPhysicConfig.despawnItem;
            }
            if (!itemPhysic$thiz.worldObj.isRemote && itemPhysic$thiz.age >= itemPhysic$thiz.lifespan) {
                if (itemPhysic$stack != null) {
                    ItemExpireEvent event = new ItemExpireEvent(
                        itemPhysic$thiz,
                        (itemPhysic$stack.getItem() == null ? 6000
                            : itemPhysic$stack.getItem()
                                .getEntityLifespan(itemPhysic$stack, itemPhysic$thiz.worldObj)));
                    if (MinecraftForge.EVENT_BUS.post(event)) // Is canceled?
                        itemPhysic$thiz.lifespan += event.extraLife; // yes - live
                    else itemPhysic$thiz.setDead(); // no - die
                } else itemPhysic$thiz.setDead();
            }
        } else {
            ++itemPhysic$thiz.age;
        }

        if (itemPhysic$stack != null && itemPhysic$stack.stackSize <= 0) itemPhysic$thiz.setDead();

        ci.cancel();
    }

    /**
     * @author kotmatross
     * @reason redirect to our system
     */
    @Overwrite
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        ServerPhysic.onCollideWithPlayer(this.rand, ((EntityItem) (Object) (this)), entityIn, true);
    }

    @Shadow
    private int health;

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (ItemPhysicConfig.customPickup) {
            ServerPhysic.onCollideWithPlayer(this.rand, ((EntityItem) (Object) (this)), player, false);
            return true;
        }
        return false;
    }

    /**
     * @author kotmatross
     * @reason redirect to our system
     */
    @Overwrite
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.getEntityItem() != null) {

            if (ServerPhysic.isItemUndestroyable(this.getEntityItem()) || this.isEntityInvulnerable()) return false;

            else if (source.isExplosion() && !ServerPhysic.canItemExplode(this.getEntityItem())) return false;

            else if ((source == DamageSource.lava || source == DamageSource.onFire || source == DamageSource.inFire)
                && !ServerPhysic.canItemBurn(this.getEntityItem())) return false;

            else if (source == DamageSource.cactus && ItemPhysicConfig.disableCactusDamage) return false;

            else if (source.damageType.equals("acid") && !ServerPhysic.canItemDissolve(this.getEntityItem()))
                return false;

            else {
                if (source.isExplosion()) this.setDead();

                this.health--;

                if (this.health <= 0) this.setDead();
            }

        }
        return false;
    }

    @Shadow
    public ItemStack getEntityItem() {
        ItemStack itemstack = this.getDataWatcher()
            .getWatchableObjectItemStack(10);
        return itemstack == null ? new ItemStack(Blocks.stone) : itemstack;
    }

    @Override
    public boolean isBurning() {
        boolean flag = this.worldObj != null && this.worldObj.isRemote;
        return ServerPhysic.canItemBurn(this.getEntityItem()) && !this.isImmuneToFire
            && (this.fire > 0 || flag && this.getFlag(0));
    }

}
