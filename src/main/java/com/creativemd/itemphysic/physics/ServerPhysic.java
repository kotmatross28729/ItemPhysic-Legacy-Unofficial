package com.creativemd.itemphysic.physics;

import com.creativemd.itemphysic.ItemPhysic;
import com.creativemd.itemphysic.ItemPhysicConfig;
import com.creativemd.itemphysic.ItemTransformer;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryBurn;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryExplosion;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryFloat;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryIgniting;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistrySulfuricAcid;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryUndestroyable;
import com.hbm.lib.ModDamageSource;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ServerPhysic {
    public static Random random = new Random();
    //For transformer
	public static void update(EntityItem item) {
		ItemStack stack = item.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null) {
            if (stack.getItem().onEntityItemUpdate(item)) return;
        }

        if (item.getEntityItem() == null) item.setDead();
        else {
            item.onEntityUpdate();

            if (item.delayBeforeCanPickup > 0) --item.delayBeforeCanPickup;

            item.prevPosX = item.posX;
            item.prevPosY = item.posY;
            item.prevPosZ = item.posZ;

            float f = 0.98F;
            Fluid fluid = getFluid(item);
            if (fluid == null)
                item.motionY -= 0.04D; //GRAVITY
            else {
            	double density = (double)fluid.getDensity()/1000D;
            	double speed = - 1/density * 0.01;

                if(canItemSwim(stack, fluid))
                    speed = 0.05;

            	double speedreduction = (speed-item.motionY)/2;
            	double maxSpeedReduction = 0.05;

            	if (speedreduction < -maxSpeedReduction) speedreduction = -maxSpeedReduction;
            	if (speedreduction > maxSpeedReduction) speedreduction = maxSpeedReduction;

            	item.motionY += speedreduction;
            	f = (float) (1D/density/1.2);
            }

            item.noClip = func_145771_j(item, item.posX, (item.boundingBox.minY + item.boundingBox.maxY) / 2.0D, item.posZ);
            item.moveEntity(item.motionX, item.motionY, item.motionZ);
            boolean flag = (int)item.prevPosX != (int)item.posX || (int)item.prevPosY != (int)item.posY || (int)item.prevPosZ != (int)item.posZ;

            if (flag || item.ticksExisted % 20 == 0) {
                if (item.worldObj.getBlock(MathHelper.floor_double(item.posX), MathHelper.floor_double(item.posY), MathHelper.floor_double(item.posZ)).getMaterial() == Material.lava && canItemBurn(stack)) {
                	item.playSound("random.fizz", 0.4F, 2.0F + random.nextFloat() * 0.4F);

                    for(int zahl = 0; zahl < 100; zahl++)
                        item.worldObj.spawnParticle("smoke", item.posX, item.posY, item.posZ, (random.nextFloat()*0.1)-0.05, 0.2*random.nextDouble(), (random.nextFloat()*0.1)-0.05);
                }

                if (item.onGround &&
                    canItemIgnite(stack) &&
                    (
                    item.worldObj.getBlock(
                        MathHelper.floor_double(item.posX),
                        MathHelper.floor_double(item.posY),
                        MathHelper.floor_double(item.posZ))
                        .getMaterial() == Material.air ||
                        item.worldObj.getBlock(
                            MathHelper.floor_double(item.posX),
                            MathHelper.floor_double(item.posY),
                            MathHelper.floor_double(item.posZ))
                        .getMaterial() == Material.plants
                    ) && item.worldObj.rand.nextInt(100) <= getIgnitingChance(stack)
                ) {
                    item.worldObj.setBlock(
                        MathHelper.floor_double(item.posX),
                        MathHelper.floor_double(item.posY),
                        MathHelper.floor_double(item.posZ),
                        getIgnitingBlock(stack),
                        getIgnitingBlockMeta(stack),
                        3);
                    }
                if (!item.worldObj.isRemote)
                    searchForOtherItemsNearby(item);
            }

            if (item.onGround && item.prevPosY != item.posY && ItemPhysic.enableFallSounds) {
                    item.playSound("dig.cloth", 1F, (float) Math.random() + 1);
            }

            if (item.onGround)
                f = item.worldObj.getBlock(MathHelper.floor_double(item.posX), MathHelper.floor_double(item.boundingBox.minY) - 1, MathHelper.floor_double(item.posZ)).slipperiness * 0.98F;

            item.motionX *= (double)f;
            item.motionZ *= (double)f;

            if (fluid == null) {
	            item.motionY *= 0.98D;

	            if (item.onGround) item.motionY *= -0.5D;
            }

            if(ItemPhysic.enableItemDespawn) {
                ++item.age; //TICKS
                if (item.lifespan == 6000 && item.lifespan != ItemPhysic.despawnItem) {
                    item.lifespan = ItemPhysic.despawnItem;
                }
                if (!item.worldObj.isRemote && item.age >= item.lifespan) {
                    if (stack != null) {
                        ItemExpireEvent event = new ItemExpireEvent(item, (stack.getItem() == null ? 6000 : stack.getItem().getEntityLifespan(stack, item.worldObj)));
                        if (MinecraftForge.EVENT_BUS.post(event)) //Is canceled?
                            item.lifespan += event.extraLife; //yes - live
                        else
                            item.setDead(); //no - die
                    } else item.setDead();
                }
            } else {
                ++item.age; //TICKS FOR ANIMATION
            }


            if (stack != null && stack.stackSize <= 0) item.setDead();
        }
	}

	public static Fluid getFluid(EntityItem item) {
		return getFluid(item, false);
    }

	public static Fluid getFluid(EntityItem item, boolean below) {
        if(item == null || item.worldObj == null)
            return null;

        double d0 = item.posY + (double)item.getEyeHeight();
        int i = MathHelper.floor_double(item.posX);
        int j = MathHelper.floor_float((float)MathHelper.floor_double(d0));

        if (below) j--;
        int k = MathHelper.floor_double(item.posZ);
        Block block = item.worldObj.getBlock(i, j, k);

        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if (fluid == null && block instanceof IFluidBlock) fluid = ((IFluidBlock)block).getFluid();
        else if (block instanceof BlockLiquid && block.getMaterial() == Material.water) fluid = FluidRegistry.WATER;
        else if (block instanceof BlockLiquid && block.getMaterial() == Material.lava) fluid = FluidRegistry.LAVA;

        if (below) return fluid;

        double filled = 1.0f; //If it's not a liquid assume it's a solid block
        if (block instanceof IFluidBlock) filled = ((IFluidBlock)block).getFilledPercentage(item.worldObj, i, j, k);

        if (filled < 0) {
            filled *= -1;
            //filled -= 0.11111111F; //Why this is needed.. not sure...
            if (d0 > (double) (j + (1 - filled))) return fluid;
        } else {
            if (d0 < (double)(j + filled)) return fluid;
        }

        return null;
    }

	public static double lastPosY;

	public static void updatePositionBefore(EntityItem item) {
		lastPosY = item.posY;
    }

    public static void updatePosition(EntityItem item, double posY) {
		double diff = Math.sqrt(Math.pow(lastPosY - posY, 2));
		if (diff < 0.5D && diff > 0) item.setPosition(item.posX, lastPosY, item.posZ);
    }

	private static void searchForOtherItemsNearby(EntityItem item) {
        Iterator iterator = item.worldObj.getEntitiesWithinAABB(EntityItem.class, item.boundingBox.expand(0.5D, 0.0D, 0.5D)).iterator();

        while (iterator.hasNext()) {
            EntityItem entityitem = (EntityItem)iterator.next();
            item.combineItems(entityitem);
        }
    }

	public static boolean func_145771_j(EntityItem item, double p_145771_1_, double p_145771_3_, double p_145771_5_) {
        int i = MathHelper.floor_double(p_145771_1_);
        int j = MathHelper.floor_double(p_145771_3_);
        int k = MathHelper.floor_double(p_145771_5_);
        double d3 = p_145771_1_ - (double)i;
        double d4 = p_145771_3_ - (double)j;
        double d5 = p_145771_5_ - (double)k;
        List list = item.worldObj.func_147461_a(item.boundingBox);

        if (list.isEmpty() && !item.worldObj.func_147469_q(i, j, k)) return false;
        else {
            boolean flag = !item.worldObj.func_147469_q(i - 1, j, k);
            boolean flag1 = !item.worldObj.func_147469_q(i + 1, j, k);
            boolean flag2 = !item.worldObj.func_147469_q(i, j - 1, k);
            boolean flag3 = !item.worldObj.func_147469_q(i, j + 1, k);
            boolean flag4 = !item.worldObj.func_147469_q(i, j, k - 1);
            boolean flag5 = !item.worldObj.func_147469_q(i, j, k + 1);
            byte b0 = 3;
            double d6 = 9999.0D;

            if (flag && d3 < d6) {
                d6 = d3;
                b0 = 0;
            }

            if (flag1 && 1.0D - d3 < d6) {
                d6 = 1.0D - d3;
                b0 = 1;
            }

            if (flag3 && 1.0D - d4 < d6) {
                d6 = 1.0D - d4;
                b0 = 3;
            }

            if (flag4 && d5 < d6) {
                d6 = d5;
                b0 = 4;
            }

            if (flag5 && 1.0D - d5 < d6) {
                d6 = 1.0D - d5;
                b0 = 5;
            }

            float f = random.nextFloat() * 0.2F + 0.1F;

            if (b0 == 0) item.motionX = (double)(-f);
            if (b0 == 1) item.motionX = (double)f;
            if (b0 == 2) item.motionY = (double)(-f);
            if (b0 == 3) item.motionY = (double)f;
            if (b0 == 4) item.motionZ = (double)(-f);
            if (b0 == 5) item.motionZ = (double)f;

            return true;
        }
    }

	public static void onCollideWithPlayer(EntityItem item, EntityPlayer par1EntityPlayer) {
		onCollideWithPlayer(item, par1EntityPlayer, true);
    }

	public static void onCollideWithPlayer(EntityItem item, EntityPlayer par1EntityPlayer, boolean needsSneak) {
		if (ItemPhysic.customPickup && needsSneak && !par1EntityPlayer.isSneaking()) return;
        if (!item.worldObj.isRemote) {
            if (!ItemPhysic.customPickup && item.delayBeforeCanPickup > 0) return;

            EntityItemPickupEvent event = new EntityItemPickupEvent(par1EntityPlayer, item);

            if (MinecraftForge.EVENT_BUS.post(event)) return;

            ItemStack itemstack = item.getEntityItem();
            int i = itemstack.stackSize;

            if ((ItemPhysic.customPickup | item.delayBeforeCanPickup <= 0) && (item.func_145798_i() == null || item.lifespan - item.age <= 200 || item.func_145798_i().equals(par1EntityPlayer.getCommandSenderName())) && (event.getResult() == Result.ALLOW || i <= 0 || par1EntityPlayer.inventory.addItemStackToInventory(itemstack))) {
    
                for (int id : OreDictionary.getOreIDs(itemstack)) {
                    if (OreDictionary.getOreID("logWood") == id) {
                        par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                        break;
                    }
                }
                
                if (itemstack.getItem() == Items.leather) par1EntityPlayer.triggerAchievement(AchievementList.killCow);
                if (itemstack.getItem() == Items.diamond) par1EntityPlayer.triggerAchievement(AchievementList.diamonds);
                if (itemstack.getItem() == Items.blaze_rod) par1EntityPlayer.triggerAchievement(AchievementList.blazeRod);
                if (itemstack.getItem() == Items.diamond && item.func_145800_j() != null) {
                    EntityPlayer entityplayer1 = item.worldObj.getPlayerEntityByName(item.func_145800_j());

                    if (entityplayer1 != null && entityplayer1 != par1EntityPlayer) entityplayer1.triggerAchievement(AchievementList.field_150966_x);
                }

                FMLCommonHandler.instance().firePlayerItemPickupEvent(par1EntityPlayer, item);

                item.worldObj.playSoundAtEntity(par1EntityPlayer, "random.pop", 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(item, i);

                if (itemstack.stackSize <= 0) item.setDead();
            }
        }
    }

	public static boolean interactFirst(EntityItem item, EntityPlayer par1EntityPlayer) {
		if (ItemPhysic.customPickup) {
			onCollideWithPlayer(item, par1EntityPlayer, false);
			return true;
		}
        return false;
    }
    //For transformer
	public static boolean attackEntityFrom(EntityItem item, DamageSource par1DamageSource, float par2) {
        if(item.getEntityItem() != null) {
            if (isItemUndestroyable(item.getEntityItem()) || item.isEntityInvulnerable())
                return false;
            else if (par1DamageSource.isExplosion() && !canItemExplode(item.getEntityItem()))
                return false;
            else if ((par1DamageSource == DamageSource.lava || par1DamageSource == DamageSource.onFire || par1DamageSource == DamageSource.inFire) && !canItemBurn(item.getEntityItem()))
                return false;
            else if (par1DamageSource == DamageSource.cactus && ItemPhysic.disableCactusDamage)
                return false;
            else if(ItemPhysic.isHBMLoaded && par1DamageSource == ModDamageSource.acid && !canItemDissolve(item.getEntityItem()))
                    return false;
            else {
                if(par1DamageSource.isExplosion())
                    item.setDead();
                setHealth(item, (getHealth(item) - 1));
                if (getHealth(item) <= 0)
                    item.setDead();
            }
        }
            return false;

    }

	public static int getHealth(EntityItem item) {
		boolean obfuscated = false;
		try { obfuscated = EntityItem.class.getField("health") == null; }
        catch(Exception e) { obfuscated = true; }

		String name = ItemTransformer.patchT("health", obfuscated);
		try { return EntityItem.class.getField(name).getInt(item); }
        catch (Exception e) {
			try { return EntityItem.class.getField(name).getInt(item); }
            catch (Exception e1) {
				System.out.println("Field not found health (" + name + ")");
				return 0;
			}
		}
	}

	public static void setHealth(EntityItem item, int health) {
		boolean obfuscated = false;
		try{ obfuscated = EntityItem.class.getField("health") == null; }
        catch(Exception e) { obfuscated = true; }

		String name = ItemTransformer.patchT("health", obfuscated);
		try { EntityItem.class.getField(name).setInt(item, health); }
        catch (Exception e) {
			try { EntityItem.class.getField(name).setInt(item, health); }
            catch (Exception e1) {
				System.out.println("Field not found health (" + name + ")");
			}
		}
	}

    //For transformer
	public static boolean isItemBurning(EntityItem item) {
//        boolean flag = item.worldObj != null && item.worldObj.isRemote;
//        if (!(!item.isImmuneToFire() && (flag && (item.getDataWatcher().getWatchableObjectByte(0) & 1 << 0) != 0))) return false;
//        return canItemBurn(item.getEntityItem());

        boolean flag = item.worldObj != null && item.worldObj.isRemote;
        return canItemBurn(item.getEntityItem()) && !item.isImmuneToFire && (item.fire > 0 || flag && item.getFlag(0));
	}
    public static boolean swim = false;
    public static boolean canItemSwim(ItemStack stack, Fluid fluid) {
        if (stack != null) {
            if(!ItemPhysicConfig.invertFloatList) {
                swim = false;
                for (ItemsWithMetaRegistryFloat.ItemWithMetaFloat itemWithMetaFloat : ItemsWithMetaRegistryFloat.FloatItems) {
                    for(int i = 0; i < itemWithMetaFloat.liquids.length; i++){
                        if(itemWithMetaFloat.liquids[i].equals(fluid.getUnlocalizedName())){
                            if (!itemWithMetaFloat.ignoremeta) {
                                if (stack.getItem() == itemWithMetaFloat.item && stack.getItemDamage() == itemWithMetaFloat.metadata) {
                                    swim = true;
                                }
                            } else {
                                if (stack.getItem() == itemWithMetaFloat.item) {
                                    swim = true;
                                }
                            }
                        }
                    }
                }
            } else {
                swim = true;
                for (ItemsWithMetaRegistryFloat.ItemWithMetaFloat itemWithMetaFloat : ItemsWithMetaRegistryFloat.FloatItems) {
                    for(int i = 0; i < itemWithMetaFloat.liquids.length; i++) {
                        if (itemWithMetaFloat.liquids[i].equals(fluid.getUnlocalizedName())) {
                            if (!itemWithMetaFloat.ignoremeta) {
                                if (stack.getItem() == itemWithMetaFloat.item && stack.getItemDamage() == itemWithMetaFloat.metadata) {
                                    swim = false;
                                }
                            } else {
                                if (stack.getItem() == itemWithMetaFloat.item) {
                                    swim = false;
                                }
                            }
                        }
                    }
                }
            }
            return swim;
        }
        return false;
    }

    public static boolean burn = true;
	public static boolean canItemBurn(ItemStack stack) {
        if (stack != null) {
            if(!ItemPhysicConfig.invertBurnList) {
                burn = true;
                for (ItemsWithMetaRegistryBurn.ItemWithMetaBurn itemWithMetaBurn : ItemsWithMetaRegistryBurn.BurnItems) {
                    if(!itemWithMetaBurn.ignoremeta) {
                        if(stack.getItem() == itemWithMetaBurn.item && stack.getItemDamage() == itemWithMetaBurn.metadata) {
                            burn = false;
                        }
                    } else {
                        if(stack.getItem() == itemWithMetaBurn.item) {
                            burn = false;
                        }
                    }
                }
            } else {
                burn = false;
                for (ItemsWithMetaRegistryBurn.ItemWithMetaBurn itemWithMetaBurn : ItemsWithMetaRegistryBurn.BurnItems) {
                    if(!itemWithMetaBurn.ignoremeta) {
                        if(stack.getItem() == itemWithMetaBurn.item && stack.getItemDamage() == itemWithMetaBurn.metadata) {
                            burn = true;
                        }
                    } else {
                        if(stack.getItem() == itemWithMetaBurn.item) {
                            burn = true;
                        }
                    }
                }
            }
            return burn;
        }
        return true;
	}

    public static boolean explosion = true;
    public static boolean canItemExplode(ItemStack stack) {
        if (stack != null) {
            if(!ItemPhysicConfig.invertExplosionList) {
                explosion = true;
                for (ItemsWithMetaRegistryExplosion.ItemsWithMetaExplosion itemWithMetaExplosion : ItemsWithMetaRegistryExplosion.ExplosionItems) {
                    if(!itemWithMetaExplosion.ignoremeta) {
                        if(stack.getItem() == itemWithMetaExplosion.item && stack.getItemDamage() == itemWithMetaExplosion.metadata) {
                            explosion = false;
                        }
                    } else {
                        if(stack.getItem() == itemWithMetaExplosion.item) {
                            explosion = false;
                        }
                    }
                }
            } else {
                explosion = false;
                for (ItemsWithMetaRegistryExplosion.ItemsWithMetaExplosion itemWithMetaExplosion : ItemsWithMetaRegistryExplosion.ExplosionItems) {
                    if(!itemWithMetaExplosion.ignoremeta) {
                        if(stack.getItem() == itemWithMetaExplosion.item && stack.getItemDamage() == itemWithMetaExplosion.metadata) {
                            explosion = true;
                        }
                    } else {
                        if(stack.getItem() == itemWithMetaExplosion.item) {
                            explosion = true;
                        }
                    }
                }
            }
            return explosion;
        }
        return true;
    }

    public static boolean undestroyable = false;
    public static boolean isItemUndestroyable(ItemStack stack) {
        if (stack != null) {
            if(!ItemPhysicConfig.invertUndestroyableList) {
                undestroyable = false;
                for (ItemsWithMetaRegistryUndestroyable.ItemWithMetaUndestroyable itemWithMetaUndestroyable : ItemsWithMetaRegistryUndestroyable.UndestroyableItems) {
                    if(!itemWithMetaUndestroyable.ignoremeta) {
                        if(stack.getItem() == itemWithMetaUndestroyable.item && stack.getItemDamage() == itemWithMetaUndestroyable.metadata) {
                            undestroyable = true;
                        }
                    } else {
                        if(stack.getItem() == itemWithMetaUndestroyable.item) {
                            undestroyable = true;
                        }
                    }
                }
            } else {
                undestroyable = true;
                for (ItemsWithMetaRegistryUndestroyable.ItemWithMetaUndestroyable itemWithMetaUndestroyable : ItemsWithMetaRegistryUndestroyable.UndestroyableItems) {
                    if(!itemWithMetaUndestroyable.ignoremeta) {
                        if(stack.getItem() == itemWithMetaUndestroyable.item && stack.getItemDamage() == itemWithMetaUndestroyable.metadata) {
                            undestroyable = false;
                        }
                    } else {
                        if(stack.getItem() == itemWithMetaUndestroyable.item) {
                            undestroyable = false;
                        }
                    }
                }
            }
            return undestroyable;
        }
        return true;
    }

    public static boolean dissolve = true;
    public static boolean canItemDissolve(ItemStack stack) {
        if (stack != null) {
            if(!ItemPhysicConfig.invertSulfuricAcidList) {
                dissolve = true;
                for (ItemsWithMetaRegistrySulfuricAcid.ItemsWithMetaSulfuricAcid itemWithMetaSulfuricAcid : ItemsWithMetaRegistrySulfuricAcid.SulfuricAcidItems) {
                    if(!itemWithMetaSulfuricAcid.ignoremeta) {
                        if(stack.getItem() == itemWithMetaSulfuricAcid.item && stack.getItemDamage() == itemWithMetaSulfuricAcid.metadata) {
                            dissolve = false;
                        }
                    } else {
                        if(stack.getItem() == itemWithMetaSulfuricAcid.item) {
                            dissolve = false;
                        }
                    }
                }
            } else {
                dissolve = false;
                for (ItemsWithMetaRegistrySulfuricAcid.ItemsWithMetaSulfuricAcid itemWithMetaSulfuricAcid : ItemsWithMetaRegistrySulfuricAcid.SulfuricAcidItems) {
                    if(!itemWithMetaSulfuricAcid.ignoremeta) {
                        if(stack.getItem() == itemWithMetaSulfuricAcid.item && stack.getItemDamage() == itemWithMetaSulfuricAcid.metadata) {
                            dissolve = true;
                        }
                    } else {
                        if(stack.getItem() == itemWithMetaSulfuricAcid.item) {
                            dissolve = true;
                        }
                    }
                }
            }
            return dissolve;
        }
        return true;
    }

    public static boolean ignite = false;
    public static boolean canItemIgnite(ItemStack stack) {
        if (stack != null) {
            if(!ItemPhysicConfig.invertIgnitingItemsList) {
                ignite = false;
                for (ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting ItemWithMetaIgniting : ItemsWithMetaRegistryIgniting.IgnitingItems) {
                    if(!ItemWithMetaIgniting.ignoremetaItem) {
                        if(stack.getItem() == ItemWithMetaIgniting.item && stack.getItemDamage() == ItemWithMetaIgniting.metadataItem) {
                            ignite = true;
                        }
                    } else {
                        if(stack.getItem() == ItemWithMetaIgniting.item) {
                            ignite = true;
                        }
                    }
                }
            } else {
                ignite = true;
                for (ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting ItemWithMetaIgniting : ItemsWithMetaRegistryIgniting.IgnitingItems) {
                    if(!ItemWithMetaIgniting.ignoremetaItem) {
                        if(stack.getItem() == ItemWithMetaIgniting.item && stack.getItemDamage() == ItemWithMetaIgniting.metadataItem) {
                            ignite = false;
                        }
                    } else {
                        if(stack.getItem() == ItemWithMetaIgniting.item) {
                            ignite = false;
                        }
                    }
                }
            }
            return ignite;
        }
        return true;
    }

    public static Block block = Blocks.fire;
    public static Block getIgnitingBlock(ItemStack stack) {
        for (ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting ItemWithMetaIgniting : ItemsWithMetaRegistryIgniting.IgnitingItems) {
            if(!ItemWithMetaIgniting.ignoremetaItem) {
                if(stack.getItem() == ItemWithMetaIgniting.item && stack.getItemDamage() == ItemWithMetaIgniting.metadataItem) {
                    block = ItemWithMetaIgniting.block;
                }
            } else {
                if(stack.getItem() == ItemWithMetaIgniting.item) {
                    block = ItemWithMetaIgniting.block;
                }
            }
        }
        return block;
    }
    public static int blockMeta = 0;
    public static int getIgnitingBlockMeta(ItemStack stack) {
        for (ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting ItemWithMetaIgniting : ItemsWithMetaRegistryIgniting.IgnitingItems) {
            if(!ItemWithMetaIgniting.ignoremetaItem) {
                if(stack.getItem() == ItemWithMetaIgniting.item && stack.getItemDamage() == ItemWithMetaIgniting.metadataItem) {
                    blockMeta = ItemWithMetaIgniting.metadataBlock;
                }
            } else {
                if(stack.getItem() == ItemWithMetaIgniting.item) {
                    blockMeta = ItemWithMetaIgniting.metadataBlock;
                }
            }
        }
        return blockMeta;
    }

    public static int igniteChance = 10;
    public static int getIgnitingChance(ItemStack stack) {
        for (ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting ItemWithMetaIgniting : ItemsWithMetaRegistryIgniting.IgnitingItems) {
            if(!ItemWithMetaIgniting.ignoremetaItem) {
                if(stack.getItem() == ItemWithMetaIgniting.item && stack.getItemDamage() == ItemWithMetaIgniting.metadataItem) {
                    igniteChance = ItemWithMetaIgniting.igniteChance;
                }
            } else {
                if(stack.getItem() == ItemWithMetaIgniting.item) {
                    igniteChance = ItemWithMetaIgniting.igniteChance;
                }
            }
        }
        return igniteChance;
    }
}
