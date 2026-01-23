package com.creativemd.itemphysic.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ItemPhysicMixinConfig {

    public static boolean MixinEntityItem;
    public static boolean MixinRenderItem;
    public static boolean MixinEntityItemSound;
    public static boolean MixinEntityClientPlayerMP;
    public static boolean MixinEntityPlayer;
    public static boolean MixinTConstructRenderTools;

    static final String categoryMixins = "Mixins: change with caution";

    public static void loadMixinConfig(File configFile) {
        Configuration config = new Configuration(configFile);

        MixinEntityItem = config.getBoolean(
            "MixinEntityItem",
            categoryMixins,
            true,
            "Basic logic of item physic. When disabled: 1) items will behave the same in different liquids and will not float 2) Lists will be ignored (items that should not burn will burn) 3) customPickup will stop working.");
        MixinRenderItem = config.getBoolean(
            "MixinRenderItem",
            categoryMixins,
            true,
            "Basic renderer of item physic. When disabled: 1) items will be rendered like in vanilla (without rotation, with spinning and levitating a little).");
        MixinEntityItemSound = config.getBoolean(
            "MixinEntityItemSound",
            categoryMixins,
            true,
            "Enables item sound mixin. When disabled: 1) Items will not make a sound on fall.");
        MixinEntityClientPlayerMP = config.getBoolean(
            "MixinEntityClientPlayerMP",
            categoryMixins,
            true,
            "Disables the dropOneItem method if customThrow is enabled. When disabled: 1) If customThrow is enabled, the player will drop 2 items at once: the first when pressed the key, the second when released it.");
        MixinEntityPlayer = config.getBoolean(
            "MixinEntityPlayer",
            categoryMixins,
            true,
            "Enables expanded auto pickup distance. When disabled: 1) Auto pickup distance will remain vanilla (playerPickupExpansionXZ and playerPickupExpansionY will be ignored).");
        MixinTConstructRenderTools = config.getBoolean(
            "MixinTConstructRenderTools",
            categoryMixins,
            true,
            "Basic Physics for Tinkers Construct tools. When disabled: 1) items will be rendered like in vanilla (without rotation, with spinning and levitating a little)."
        );
    if (config.hasChanged()) {
            config.save();
        }
    }

}
