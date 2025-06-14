package com.creativemd.itemphysic.config;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import com.creativemd.itemphysic.ItemPhysic;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class ItemPhysicGuiFactory implements IModGuiFactory {

    public static class ItemPhysicGuiConfig extends GuiConfig {

        private static final IConfigElement<?> general = new ConfigElement<>(
            ItemPhysic.config.getCategory(ItemPhysicConfig.CATEGORY_GENERAL));
        // private static final IConfigElement<?> listBurn = new ConfigElement<>(
        // ItemPhysic.config.getCategory(ItemPhysicConfig.CATEGORY_BURN));
        // private static final IConfigElement<?> listFloat = new ConfigElement<>(
        // ItemPhysic.config.getCategory(ItemPhysicConfig.CATEGORY_FLOAT));
        // private static final IConfigElement<?> listExplosion = new ConfigElement<>(
        // ItemPhysic.config.getCategory(ItemPhysicConfig.CATEGORY_EXPLOSION));
        // private static final IConfigElement<?> listUndestroyable = new ConfigElement<>(
        // ItemPhysic.config.getCategory(ItemPhysicConfig.CATEGORY_UNDESTROYABLE));
        // private static final IConfigElement<?> listSulfuricAcid = new ConfigElement<>(
        // ItemPhysic.config.getCategory(ItemPhysicConfig.CATEGORY_ACID));
        // private static final IConfigElement<?> listIgnitingItems = new ConfigElement<>(
        // ItemPhysic.config.getCategory(ItemPhysicConfig.CATEGORY_IGNITING));

        public ItemPhysicGuiConfig(GuiScreen parent) {
            super(
                parent,
                ImmutableList.of(general),
                // ImmutableList.of(
                // general,
                // listBurn,
                // listFloat,
                // listExplosion,
                // listUndestroyable,
                // listSulfuricAcid,
                // listIgnitingItems
                // ),
                ItemPhysic.MODID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ItemPhysic.config.toString()));
        }
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ItemPhysicGuiConfig.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

}
