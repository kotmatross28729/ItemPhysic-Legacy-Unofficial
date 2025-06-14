package com.creativemd.itemphysic.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.MixinEnvironment;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class ItemPhysicLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.itemphysic.late.json";
    }

    public static final MixinEnvironment.Side side = MixinEnvironment.getCurrentEnvironment()
        .getSide();

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        return mixins;
    }
}
