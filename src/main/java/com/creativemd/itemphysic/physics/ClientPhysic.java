package com.creativemd.itemphysic.physics;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fluids.Fluid;

public class ClientPhysic {

    public static long tick;
    public static double rotation;

    public static void applyRotations(EntityItem item) {
        if (item.worldObj != null && !RenderItem.renderInFrame && item.age != 0) {
            if (item.onGround || item.prevPosY == item.posY) {
                item.rotationPitch = 0;
            } else {
                double rotation = ClientPhysic.rotation * 2;
                Fluid fluid = ServerPhysic.getFluid(item);
                if (fluid != null) rotation /= (float) ((fluid.getDensity() / 1000) * 10);
                else {
                    fluid = ServerPhysic.getFluid(item, true);
                    if (fluid != null) rotation /= (float) ((fluid.getDensity() / 1000) * 10);
                }
                if (item.isInWeb) rotation /= 50;
                item.rotationPitch += (float) rotation;
            }
        }
    }

}
