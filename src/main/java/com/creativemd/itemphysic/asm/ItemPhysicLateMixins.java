package com.creativemd.itemphysic.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.creativemd.itemphysic.config.ItemPhysicMixinConfig;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.common.Loader;

@LateMixin
public class ItemPhysicLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.itemphysic.late.json";
    }

    public static boolean IS_TC_PRESENT = false;

    @Override
    public List<String> getMixins(Set<String> loadedMods) {

        if (Loader.isModLoaded("TConstruct")) {
            IS_TC_PRESENT = true;
        }

        List<String> mixins = new ArrayList<>();

        if (ItemPhysicMixinConfig.MixinTConstructRenderTools && IS_TC_PRESENT) {
            mixins.add("MixinTConstructItemRenderer");
        }

        return mixins;
    }
}
