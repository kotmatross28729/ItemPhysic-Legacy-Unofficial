package com.creativemd.itemphysic.config;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.ingameconfigmanager.api.common.branch.ConfigBranch;
import com.creativemd.ingameconfigmanager.api.common.branch.ConfigSegmentCollection;
import com.creativemd.ingameconfigmanager.api.common.segment.BooleanSegment;
import com.creativemd.ingameconfigmanager.api.common.segment.IntegerSegment;
import com.creativemd.itemphysic.ItemPhysic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPhysicBranch extends ConfigBranch {

    public ItemPhysicBranch(String name) {
        super(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected Avatar getAvatar() {
        return new AvatarItemStack(new ItemStack(Items.feather));
    }

    @Override
    public void loadCore() {}

    @Override
    public void createConfigSegments() {
        segments.add(new IntegerSegment("despawn", "despawn time", 6000));
        segments.add(new BooleanSegment("pickup", "custom pickup", false));
        segments.add(new BooleanSegment("throw", "custom throw", true));
    }

    @Override
    public boolean needPacket() {
        return true;
    }

    @Override
    public void onRecieveFrom(boolean isServer, ConfigSegmentCollection collection) {
        ItemPhysic.despawnItem = (Integer) collection.getSegmentValue("despawn");
        ItemPhysic.customPickup = (Boolean) collection.getSegmentValue("pickup");
        ItemPhysic.customThrow = (Boolean) collection.getSegmentValue("throw");
    }

}
