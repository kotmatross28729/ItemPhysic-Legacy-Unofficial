package com.creativemd.itemphysic.mixins.early;

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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.creativemd.itemphysic.config.ItemPhysicConfig;
import com.creativemd.itemphysic.physics.ServerPhysic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(value = EntityItem.class, priority = 1006)
public abstract class MixinEntityItem extends Entity {

    public MixinEntityItem(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public int age;

    @Shadow(remap = false)
    public int lifespan = 6000;

    @Shadow
    public int delayBeforeCanPickup;

    @Shadow
    private void searchForOtherItemsNearby() {}

    /**
     * @author kotmatross
     * @reason redirect to our system
     */
    @Overwrite
    public void onUpdate() {
        ItemStack stack = this.getDataWatcher()
            .getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null) {
            if (stack.getItem()
                .onEntityItemUpdate(((EntityItem) ((Object) this)))) {
                return;
            }
        }

        if (this.getEntityItem() == null) {
            this.setDead();
        } else {
            super.onUpdate();

            if (this.delayBeforeCanPickup > 0) {
                --this.delayBeforeCanPickup;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            float f = 0.98F;

            Fluid fluid = ServerPhysic.getFluid(((EntityItem) ((Object) this)));
            if (fluid == null) this.motionY -= 0.03999999910593033D; // GRAVITY
            else {
                double density = (double) fluid.getDensity() / 1000D;
                double speed = -1 / density * 0.01;

                if (ServerPhysic.canItemSwim(stack, fluid)) speed = 0.05;

                double speedreduction = (speed - this.motionY) / 2;
                double maxSpeedReduction = 0.05;

                if (speedreduction < -maxSpeedReduction) speedreduction = -maxSpeedReduction;
                if (speedreduction > maxSpeedReduction) speedreduction = maxSpeedReduction;

                this.motionY += speedreduction;
                f = (float) (1D / density / 1.2);
            }

            this.noClip = this
                .func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            boolean flag = (int) this.prevPosX != (int) this.posX || (int) this.prevPosY != (int) this.posY
                || (int) this.prevPosZ != (int) this.posZ;

            if (flag || this.ticksExisted % 20 == 0) {
                if (this.worldObj
                    .getBlock(
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posY),
                        MathHelper.floor_double(this.posZ))
                    .getMaterial() == Material.lava && ServerPhysic.canItemBurn(stack)) {
                    this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                    for (int zahl = 0; zahl < 50; zahl++) this.worldObj.spawnParticle(
                        "smoke",
                        this.posX,
                        this.posY,
                        this.posZ,
                        (this.rand.nextFloat() * 0.1) - 0.05,
                        0.2 * this.rand.nextDouble(),
                        (this.rand.nextFloat() * 0.1) - 0.05);
                }

                if (this.onGround && ServerPhysic.canItemIgnite(stack)
                    && (this.worldObj
                        .getBlock(
                            MathHelper.floor_double(this.posX),
                            MathHelper.floor_double(this.posY),
                            MathHelper.floor_double(this.posZ))
                        .getMaterial() == Material.air
                        || this.worldObj
                            .getBlock(
                                MathHelper.floor_double(this.posX),
                                MathHelper.floor_double(this.posY),
                                MathHelper.floor_double(this.posZ))
                            .getMaterial() == Material.plants)
                    && this.worldObj.rand.nextInt(100) <= ServerPhysic.getIgnitingChance(stack)) {
                    this.worldObj.setBlock(
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posY),
                        MathHelper.floor_double(this.posZ),
                        ServerPhysic.getIgnitingBlock(stack),
                        ServerPhysic.getIgnitingBlockMeta(stack),
                        3);
                }

                if (!this.worldObj.isRemote) {
                    this.searchForOtherItemsNearby();
                }
            }

            if (this.onGround && this.prevPosY != this.posY && ItemPhysicConfig.enableFallSounds) {
                this.playSound("dig.cloth", 1F, (float) Math.random() + 1);
            }

            if (this.onGround) {
                f = this.worldObj.getBlock(
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.boundingBox.minY) - 1,
                    MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;
            }

            this.motionX *= f;
            this.motionZ *= f;

            if (fluid == null) {
                this.motionY *= 0.9800000190734863D;

                if (this.onGround) this.motionY *= -0.5D;
            }

            if (ItemPhysicConfig.enableItemDespawn) {
                ++this.age; // TICKS
                if (this.lifespan == 6000 && this.lifespan != ItemPhysicConfig.despawnItem) {
                    this.lifespan = ItemPhysicConfig.despawnItem;
                }
                if (!this.worldObj.isRemote && this.age >= this.lifespan) {
                    if (stack != null) {

                        ItemExpireEvent event = new ItemExpireEvent(
                            ((EntityItem) ((Object) this)),
                            (stack.getItem() == null ? 6000
                                : stack.getItem()
                                    .getEntityLifespan(stack, this.worldObj)));

                        if (MinecraftForge.EVENT_BUS.post(event)) // Is event canceled?
                            this.lifespan += event.extraLife; // yes - live
                        else this.setDead(); // no - die

                    } else this.setDead();
                }
            } else {
                ++this.age; // TICKS FOR ANIMATION
            }

            if (stack != null && stack.stackSize <= 0) this.setDead();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
        ServerPhysic.updatePositionBefore(((EntityItem) (Object) (this)));
        super.setPositionAndRotation2(x, y, z, yaw, pitch, rotationIncrements);
        ServerPhysic.updatePosition(((EntityItem) (Object) (this)));
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
