package com.creativemd.itemphysic.proxy;

import com.creativemd.itemphysic.ItemPhysic;

public class CommonProxy {

    public void registerEvents() {}

    public ItemPhysic mod;

    public void init(ItemPhysic Tmod) {
        mod = Tmod;
    }
}
