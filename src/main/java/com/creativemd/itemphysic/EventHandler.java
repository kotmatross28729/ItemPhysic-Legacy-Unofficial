package com.creativemd.itemphysic;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.creativemd.itemphysic.config.ItemPhysicConfig;
import com.creativemd.itemphysic.packet.PacketDispatcher;
import com.creativemd.itemphysic.packet.PickupPacket;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(ItemPhysic.MODID)) ItemPhysic.syncConfig();
    }

    public static int DropPower = 1;

    @SubscribeEvent
    public void onToos(ItemTossEvent event) {
        event.entityItem.motionX *= DropPower;
        event.entityItem.motionY *= DropPower;
        event.entityItem.motionZ *= DropPower;
    }

    public static EntityItem getEntityItem(EntityPlayer player, Vec3 vec31, Vec3 vec3) {
        float f1 = 1.0F;
        double d0 = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
        List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(
            player,
            player.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
                .expand(f1, f1, f1));

        Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
        double d1 = d0;

        if (FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient()) {
            if (Minecraft.getMinecraft().objectMouseOver != null)
                d1 = Minecraft.getMinecraft().objectMouseOver.hitVec.distanceTo(vec3);
        }

        double d2 = d1;
        for (Entity entity : list) {
            if (entity instanceof EntityItem) {
                float f2 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (0.0D < d2 || d2 == 0.0D) return (EntityItem) entity;
                } else if (movingobjectposition != null) return (EntityItem) entity;
            }
        }
        return null;
    }

    public static EntityItem getEntityItem(double distance, EntityPlayer player) {
        Vec3 vec31 = player.getLook(1.0F);
        Vec3 vec3 = player.getPosition(1.0F);
        EntityItem item = getEntityItem(player, vec31, vec3);

        if (item != null && player.getDistanceToEntity(item) < distance) return item;

        return null;
    }

    public static boolean cancel = false;

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.world.isRemote && ItemPhysicConfig.customPickup
            && (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR
                || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            double distance = 100;
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
                distance = event.entityPlayer.getDistance(event.x, event.y, event.z);
            EntityItem entity = getEntityItem(distance, event.entityPlayer);

            if (event.entityPlayer.worldObj.isRemote && entity != null) PacketDispatcher.wrapper
                .sendToServer(new PickupPacket(event.entityPlayer.getLook(1.0F), event.entityPlayer.getPosition(1.0F)));
        }
        if (!event.entityPlayer.worldObj.isRemote && cancel) {
            cancel = false;
            event.setCanceled(true);
        }
    }

}
