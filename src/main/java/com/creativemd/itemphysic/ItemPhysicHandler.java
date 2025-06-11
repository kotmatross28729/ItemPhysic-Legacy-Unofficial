package com.creativemd.itemphysic;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import com.creativemd.itemphysic.physics.ServerPhysic;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemPhysicHandler {

    @SubscribeEvent
    public void drawTooltip(ItemTooltipEvent event) {
        if (event.itemStack != null) {
            if (ServerPhysic.canItemIgnite(event.itemStack)) {
                event.toolTip.add(EnumChatFormatting.GOLD + "[" + I18n.format("itemphysic.igniting") + "]");
            }
        }
    }
}
