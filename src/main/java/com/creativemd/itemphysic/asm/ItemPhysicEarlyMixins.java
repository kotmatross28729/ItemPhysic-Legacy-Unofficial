package com.creativemd.itemphysic.asm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.Launch;

import com.creativemd.itemphysic.config.ItemPhysicMixinConfig;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("ItemPhysicEarlyMixins")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class ItemPhysicEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.itemphysic.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        String configFolder = "config" + File.separator;
        ItemPhysicMixinConfig.loadMixinConfig(new File(Launch.minecraftHome, configFolder + "itemphysicMixins.cfg"));

        List<String> mixins = new ArrayList<>();

        if (ItemPhysicMixinConfig.MixinEntityItem) mixins.add("MixinEntityItem");
        if (ItemPhysicMixinConfig.MixinRenderItem) mixins.add("MixinRenderItem");
        if (ItemPhysicMixinConfig.MixinEntityClientPlayerMP) mixins.add("MixinEntityClientPlayerMP");
        if (ItemPhysicMixinConfig.MixinEntityPlayer) mixins.add("MixinEntityPlayer");

        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
