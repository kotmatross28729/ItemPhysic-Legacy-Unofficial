package com.creativemd.itemphysic.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.creativemd.itemphysic.config.ItemPhysicMixinConfig;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class ItemPhysicLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.itemphysic.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if (ItemPhysicMixinConfig.MixinTConstructRenderTools) mixins.add("MixinTConstructItemRenderer");
        return mixins;
    }
}
