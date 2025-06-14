package com.creativemd.itemphysic.physics;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.OreDictionary;

import com.creativemd.itemphysic.config.ItemPhysicConfig;
import com.creativemd.itemphysic.list.BurnListRegistry;
import com.creativemd.itemphysic.list.ExplosionListRegistry;
import com.creativemd.itemphysic.list.FloatListRegistry;
import com.creativemd.itemphysic.list.IgnitingListRegistry;
import com.creativemd.itemphysic.list.SulfuricAcidListRegistry;
import com.creativemd.itemphysic.list.UndestroyableListRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;

public class ServerPhysic {

    public static Fluid getFluid(EntityItem item) {
        return getFluid(item, false);
    }

    public static Fluid getFluid(EntityItem item, boolean below) {
        if (item == null || item.worldObj == null) return null;

        double d0 = item.posY + (double) item.getEyeHeight();
        int i = MathHelper.floor_double(item.posX);
        int j = MathHelper.floor_float((float) MathHelper.floor_double(d0));

        if (below) j--;
        int k = MathHelper.floor_double(item.posZ);
        Block block = item.worldObj.getBlock(i, j, k);

        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if (fluid == null && block instanceof IFluidBlock) fluid = ((IFluidBlock) block).getFluid();
        else if (block instanceof BlockLiquid && block.getMaterial() == Material.water) fluid = FluidRegistry.WATER;
        else if (block instanceof BlockLiquid && block.getMaterial() == Material.lava) fluid = FluidRegistry.LAVA;

        if (below) return fluid;

        double filled = 1.0f;
        if (block instanceof IFluidBlock) filled = ((IFluidBlock) block).getFilledPercentage(item.worldObj, i, j, k);

        if (filled < 0) {
            filled *= -1;
            // filled -= 0.11111111F; //Why this is needed.. not sure...
            if (d0 > (j + (1 - filled))) return fluid;
        } else {
            if (d0 < (j + filled)) return fluid;
        }

        return null;
    }

    public static double lastPosY;

    public static void updatePositionBefore(EntityItem item) {
        lastPosY = item.posY;
    }

    public static void updatePosition(EntityItem item) {
        double diff = Math.sqrt(Math.pow(lastPosY - item.posY, 2));
        if (diff < 0.5D && diff > 0) item.setPosition(item.posX, lastPosY, item.posZ);
    }

    public static void onCollideWithPlayer(Random rand, EntityItem item, EntityPlayer par1EntityPlayer,
        boolean needsSneak) {
        if (ItemPhysicConfig.customPickup && needsSneak && !par1EntityPlayer.isSneaking()) return;
        if (!item.worldObj.isRemote) {
            if (!ItemPhysicConfig.customPickup && item.delayBeforeCanPickup > 0) return;

            EntityItemPickupEvent event = new EntityItemPickupEvent(par1EntityPlayer, item);

            if (MinecraftForge.EVENT_BUS.post(event)) return;

            ItemStack itemstack = item.getEntityItem();
            int i = itemstack.stackSize;

            if ((ItemPhysicConfig.customPickup | item.delayBeforeCanPickup <= 0)
                && (item.func_145798_i() == null || item.lifespan - item.age <= 200
                    || item.func_145798_i()
                        .equals(par1EntityPlayer.getCommandSenderName()))
                && (event.getResult() == Event.Result.ALLOW || i <= 0
                    || par1EntityPlayer.inventory.addItemStackToInventory(itemstack))) {

                for (int id : OreDictionary.getOreIDs(itemstack)) {
                    if (OreDictionary.getOreID("logWood") == id) {
                        par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                        break;
                    }
                }

                if (itemstack.getItem() == Items.leather) par1EntityPlayer.triggerAchievement(AchievementList.killCow);
                if (itemstack.getItem() == Items.diamond) par1EntityPlayer.triggerAchievement(AchievementList.diamonds);
                if (itemstack.getItem() == Items.blaze_rod)
                    par1EntityPlayer.triggerAchievement(AchievementList.blazeRod);
                if (itemstack.getItem() == Items.diamond && item.func_145800_j() != null) {
                    EntityPlayer entityplayer1 = item.worldObj.getPlayerEntityByName(item.func_145800_j());

                    if (entityplayer1 != null && entityplayer1 != par1EntityPlayer)
                        entityplayer1.triggerAchievement(AchievementList.field_150966_x);
                }

                FMLCommonHandler.instance()
                    .firePlayerItemPickupEvent(par1EntityPlayer, item);

                item.worldObj.playSoundAtEntity(
                    par1EntityPlayer,
                    "random.pop",
                    0.2F,
                    ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(item, i);

                if (itemstack.stackSize <= 0) item.setDead();
            }
        }
    }

    // Oh god, forgive me for the code that was here before
    // Oh god, it makes me feel SO bad

    public static boolean canItemSwim(ItemStack stack, Fluid fluid) {
        if (stack != null) {
            for (FloatListRegistry.FloatList floatList : FloatListRegistry.FloatItems) {
                for (int i = 0; i < floatList.liquids.length; i++) {
                    if (floatList.liquids[i].equals(fluid.getUnlocalizedName())) {
                        if ((floatList.ignoremeta && stack.getItem() == floatList.item)
                            || stack.getItem() == floatList.item && stack.getItemDamage() == floatList.metadata) {
                            return !ItemPhysicConfig.invertFloatList; // ItemPhysicConfig.invertFloatList ? false : true
                        }
                    }
                }
            }
        }
        return ItemPhysicConfig.invertFloatList;
    }

    public static boolean canItemBurn(ItemStack stack) {
        if (stack != null) {
            for (BurnListRegistry.BurnList burnList : BurnListRegistry.BurnItems) {
                if ((burnList.ignoremeta && stack.getItem() == burnList.item)
                    || stack.getItem() == burnList.item && stack.getItemDamage() == burnList.metadata) {
                    return ItemPhysicConfig.invertBurnList; // ItemPhysicConfig.invertBurnList ? true : false
                }
            }
        }
        return !ItemPhysicConfig.invertBurnList;
    }

    public static boolean canItemExplode(ItemStack stack) {
        if (stack != null) {
            for (ExplosionListRegistry.ExplosionList itemWithMetaExplosion : ExplosionListRegistry.ExplosionItems) {
                if ((itemWithMetaExplosion.ignoremeta && stack.getItem() == itemWithMetaExplosion.item)
                    || stack.getItem() == itemWithMetaExplosion.item
                        && stack.getItemDamage() == itemWithMetaExplosion.metadata) {
                    return ItemPhysicConfig.invertExplosionList; // ItemPhysicConfig.invertExplosionList ? true : false
                }
            }
        }
        return !ItemPhysicConfig.invertExplosionList;
    }

    public static boolean isItemUndestroyable(ItemStack stack) {
        if (stack != null) {
            for (UndestroyableListRegistry.UndestroyableList undestroyableList : UndestroyableListRegistry.UndestroyableItems) {
                if ((undestroyableList.ignoremeta && stack.getItem() == undestroyableList.item)
                    || stack.getItem() == undestroyableList.item
                        && stack.getItemDamage() == undestroyableList.metadata) {
                    return !ItemPhysicConfig.invertUndestroyableList; // ItemPhysicConfig.invertUndestroyableList ?
                                                                      // false : true
                }
            }
        }
        return ItemPhysicConfig.invertUndestroyableList;
    }

    public static boolean canItemDissolve(ItemStack stack) {
        if (stack != null) {
            for (SulfuricAcidListRegistry.SulfuricAcidList itemWithMetaSulfuricAcid : SulfuricAcidListRegistry.SulfuricAcidItems) {
                if ((itemWithMetaSulfuricAcid.ignoremeta && stack.getItem() == itemWithMetaSulfuricAcid.item)
                    || stack.getItem() == itemWithMetaSulfuricAcid.item
                        && stack.getItemDamage() == itemWithMetaSulfuricAcid.metadata) {
                    return ItemPhysicConfig.invertSulfuricAcidList; // ItemPhysicConfig.invertSulfuricAcidList ? true
                                                                    // : false
                }
            }
        }
        return !ItemPhysicConfig.invertSulfuricAcidList;
    }

    public static boolean canItemIgnite(ItemStack stack) {
        if (stack != null) {
            for (IgnitingListRegistry.IgnitingList IgnitingList : IgnitingListRegistry.IgnitingItems) {
                if ((IgnitingList.ignoremetaItem && stack.getItem() == IgnitingList.item)
                    || stack.getItem() == IgnitingList.item && stack.getItemDamage() == IgnitingList.metadataItem) {
                    return !ItemPhysicConfig.invertIgnitingItemsList; // ItemPhysicConfig.invertIgnitingItemsList ?
                                                                      // false : true
                }
            }
        }
        return ItemPhysicConfig.invertIgnitingItemsList;
    }

    public static Block getIgnitingBlock(ItemStack stack) {
        if (stack != null) {
            for (IgnitingListRegistry.IgnitingList IgnitingList : IgnitingListRegistry.IgnitingItems) {
                if ((IgnitingList.ignoremetaItem && stack.getItem() == IgnitingList.item)
                    || stack.getItem() == IgnitingList.item && stack.getItemDamage() == IgnitingList.metadataItem) {
                    return IgnitingList.block;
                }
            }
        }
        return Blocks.fire;
    }

    public static int getIgnitingBlockMeta(ItemStack stack) {
        if (stack != null) {
            for (IgnitingListRegistry.IgnitingList IgnitingList : IgnitingListRegistry.IgnitingItems) {
                if ((IgnitingList.ignoremetaItem && stack.getItem() == IgnitingList.item)
                    || stack.getItem() == IgnitingList.item && stack.getItemDamage() == IgnitingList.metadataItem) {
                    return IgnitingList.metadataBlock;
                }
            }
        }
        return 0;
    }

    public static int getIgnitingChance(ItemStack stack) {
        if (stack != null) {
            for (IgnitingListRegistry.IgnitingList IgnitingList : IgnitingListRegistry.IgnitingItems) {
                if ((IgnitingList.ignoremetaItem && stack.getItem() == IgnitingList.item)
                    || stack.getItem() == IgnitingList.item && stack.getItemDamage() == IgnitingList.metadataItem) {
                    return IgnitingList.igniteChance;
                }
            }
        }
        return 10;
    }
}
