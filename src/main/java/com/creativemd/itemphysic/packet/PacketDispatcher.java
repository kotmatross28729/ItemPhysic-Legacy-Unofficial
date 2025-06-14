package com.creativemd.itemphysic.packet;

import com.creativemd.itemphysic.ItemPhysic;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketDispatcher {

    public static final SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ItemPhysic.MODID);

    public static void registerPackets() {
        int i = 0;
        wrapper.registerMessage(DropPacket.Handler.class, DropPacket.class, i++, Side.SERVER);
        wrapper.registerMessage(PickupPacket.Handler.class, PickupPacket.class, i++, Side.SERVER);
    }
}
