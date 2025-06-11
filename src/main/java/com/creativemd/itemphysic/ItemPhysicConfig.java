package com.creativemd.itemphysic;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ItemPhysicConfig {

    public static boolean invertBurnList;
    public static String[] burnList;

    public static boolean invertFloatList;
    public static String[] floatList;

    public static boolean invertExplosionList;
    public static String[] explosionList;

    public static boolean invertUndestroyableList;
    public static String[] undestroyableList;

    public static boolean invertSulfuricAcidList;
    public static String[] sulfuricAcidList;

    public static boolean invertIgnitingItemsList;
    public static String[] ignitingItemsList;

    public static void loadBurnListConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        invertBurnList = config.getBoolean(
            "invertBurnList",
            "listBurn",
            false,
            "Whether to invert the burn list (so items in it will be the only ones that able to burn).");
        burnList = config.getStringList(
            "burnList",
            "listBurn",
            new String[] { "minecraft:obsidian", "minecraft:netherrack", "minecraft:soul_sand", "minecraft:glowstone",
                "minecraft:nether_brick", "minecraft:nether_brick_fence", "minecraft:nether_brick_stairs",
                "minecraft:enchanting_table", "minecraft:golden_apple:1", "minecraft:bucket", "minecraft:water_bucket",
                "minecraft:lava_bucket", "minecraft:milk_bucket", "minecraft:blaze_rod", "minecraft:ghast_tear",
                "minecraft:nether_wart", "minecraft:blaze_powder", "minecraft:magma_cream", "minecraft:fire_charge",
                "minecraft:netherbrick", "Thaumcraft:blockCustomOre:2", "Thaumcraft:blockCrystal:1",
                "Thaumcraft:ItemShard:1", "Thaumcraft:ItemShard:6", "Thaumcraft:FocusFire", "etfuturum:netherite_scrap",
                "etfuturum:netherite_ingot", "etfuturum:netherite_helmet", "etfuturum:netherite_chestplate",
                "etfuturum:netherite_leggings", "etfuturum:netherite_boots", "etfuturum:netherite_pickaxe",
                "etfuturum:netherite_spade", "etfuturum:netherite_axe", "etfuturum:netherite_hoe",
                "etfuturum:netherite_sword", "etfuturum:totem_of_undying", "oreTungsten", "etfuturum:red_netherbrick",
                "etfuturum:red_netherbrick:1", "etfuturum:red_netherbrick:2", "etfuturum:ancient_debris",
                "etfuturum:netherite_block", "etfuturum:nether_gold_ore", "etfuturum:nether_brick_wall",
                "etfuturum:red_nether_brick_wall", "etfuturum:red_netherbrick_stairs", "etfuturum:red_netherbrick_slab",
                "etfuturum:soul_soil", "etfuturum:netherite_stairs", "etfuturum:modded_raw_ore_block:9",
                "etfuturum:deepslate_thaumcraft_ore:2", "hbm:item.ingot_schrabidium", "hbm:item.ingot_schrabidate",
                "hbm:item.egg_balefire_shard", "hbm:item.egg_balefire", "Co60", "Sr90", "Au198", "Pb209", "Po210",
                "Pu238", "Cf251", "Cf252", "Es253", "WhitePhosphorus", "billetCo60", "billetSr90", "billetAu198",
                "billetPb209", "billetPo210", "billetPu238", "billetCf251", "billetCf252", "billetEs253", "dustSr90",
                "dustAu198", "dustPo210", },
            "List of items that won't burn in lava or fire. See documentation on github");

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void loadFloatListConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        invertFloatList = config.getBoolean(
            "invertFloatList",
            "listFloat",
            false,
            "Whether to invert the float list (so items in it won't be able to float).");
        floatList = config.getStringList(
            "floatList",
            "listFloat",
            new String[] { "minecraft:bedrock:fluid.tile.lava", "minecraft:netherrack:fluid.tile.lava",
                "minecraft:glowstone:fluid.tile.lava", "minecraft:nether_brick:fluid.tile.lava",
                "minecraft:nether_brick_fence:fluid.tile.lava", "minecraft:nether_brick_stairs:fluid.tile.lava",
                "minecraft:dragon_egg:fluid.tile.lava", "minecraft:golden_apple:1:fluid.tile.lava",
                "minecraft:blaze_rod:fluid.tile.lava", "minecraft:nether_wart:fluid.tile.lava",
                "minecraft:blaze_powder:fluid.tile.lava", "minecraft:magma_cream:fluid.tile.lava",
                "minecraft:fire_charge:fluid.tile.lava", "minecraft:netherbrick:fluid.tile.lava",
                "Thaumcraft:blockCustomOre:2:fluid.tile.lava", "Thaumcraft:blockCrystal:1:fluid.tile.lava",
                "Thaumcraft:ItemShard:1:fluid.tile.lava", "Thaumcraft:ItemShard:6:fluid.tile.lava",
                "Thaumcraft:FocusFire:fluid.tile.lava", "etfuturum:netherite_scrap:fluid.tile.lava",
                "etfuturum:netherite_ingot:fluid.tile.lava", "etfuturum:netherite_helmet:fluid.tile.lava",
                "etfuturum:netherite_chestplate:fluid.tile.lava", "etfuturum:netherite_leggings:fluid.tile.lava",
                "etfuturum:netherite_boots:fluid.tile.lava", "etfuturum:netherite_pickaxe:fluid.tile.lava",
                "etfuturum:netherite_spade:fluid.tile.lava", "etfuturum:netherite_axe:fluid.tile.lava",
                "etfuturum:netherite_hoe:fluid.tile.lava", "etfuturum:netherite_sword:fluid.tile.lava",
                "etfuturum:red_netherbrick:fluid.tile.lava", "etfuturum:red_netherbrick:1:fluid.tile.lava",
                "etfuturum:red_netherbrick:2:fluid.tile.lava", "etfuturum:ancient_debris:fluid.tile.lava",
                "etfuturum:nether_gold_ore:fluid.tile.lava", "etfuturum:nether_brick_wall:fluid.tile.lava",
                "etfuturum:red_nether_brick_wall:fluid.tile.lava", "etfuturum:red_netherbrick_stairs:fluid.tile.lava",
                "etfuturum:red_netherbrick_slab:fluid.tile.lava",
                "etfuturum:deepslate_thaumcraft_ore:2:fluid.tile.lava", "minecraft:stick", "plankWood", "logWood",
                "blockCloth", "stairWood", "minecraft:wooden_pickaxe:true", "minecraft:wooden_shovel:true",
                "minecraft:wooden_sword:true", "minecraft:wooden_axe:true", "minecraft:wooden_hoe:true",
                "minecraft:hay_block", },
            "List of items that will float in fluids. See documentation on github");
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void loadExplosionListConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        invertExplosionList = config.getBoolean(
            "invertExplosionList",
            "listExplosion",
            false,
            "Whether to invert the explosion list (so items in it will be the only ones that able to explode).");
        explosionList = config.getStringList(
            "explosionList",
            "listExplosion",
            new String[] { "minecraft:obsidian", "Thaumcraft:ItemShard:6", "etfuturum:netherite_scrap",
                "etfuturum:netherite_ingot", "etfuturum:netherite_helmet", "etfuturum:netherite_chestplate",
                "etfuturum:netherite_leggings", "etfuturum:netherite_boots", "etfuturum:netherite_pickaxe",
                "etfuturum:netherite_spade", "etfuturum:netherite_axe", "etfuturum:netherite_hoe",
                "etfuturum:netherite_sword", "etfuturum:totem_of_undying", "etfuturum:ancient_debris",
                "etfuturum:netherite_block", "etfuturum:netherite_stairs", "minecraft:nether_star", },
            "List of items that are resistant to explosions");
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void loadUndestroyableListConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        invertUndestroyableList = config.getBoolean(
            "invertUndestroyableList",
            "listUndestroyable",
            false,
            "Whether to invert the undestroyable list (so items in it will be the only ones that can be destroyed).");
        undestroyableList = config.getStringList(
            "undestroyableList",
            "listUndestroyable",
            new String[] { "minecraft:bedrock", "minecraft:dragon_egg", "minecraft:command_block",
                "minecraft:golden_apple:1", },
            "List of items that are invulnerable to any type of damage");
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void loadSulfuricAcidListConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        invertSulfuricAcidList = config.getBoolean(
            "invertSulfuricAcidList",
            "listSulfuricAcid",
            false,
            "Whether to invert the sulfuric acid list (so items in it will be the only ones that can dissolve in sulfuric acid).");
        sulfuricAcidList = config.getStringList(
            "sulfuricAcidList",
            "listSulfuricAcid",
            new String[] { "minecraft:glass", "gemQuartz", "blockQuartz", "ingotGold", "ingotGold198", "billetGold198",
                "nuggetGold", "nuggetGold198", "blockGold", "pressurePlateGold", "minecraft:golden_sword",
                "minecraft:golden_shovel", "minecraft:golden_pickaxe", "minecraft:golden_axe", "minecraft:golden_hoe",
                "minecraft:golden_helmet", "minecraft:golden_chestplate", "minecraft:golden_leggings",
                "minecraft:golden_boots", "minecraft:golden_apple", "minecraft:golden_carrot", "Thaumcraft:WandCap:1",
                "Thaumcraft:ItemResource:18", "Thaumcraft:ItemNugget:31", "Thaumcraft:ArcaneDoorKey:1", "dustGold",
                "dustGold198", "gearGold", "rawGold", "hbm:tile.ladder_gold", "hbm:tile.capacitor_gold",
                "plateTripleGold", "wireFineGold", "wireDenseGold", "hbm:item.crystal_gold", "hbm:item.coil_gold_torus",
                "hbm:item.coil_gold", "plateGold", "ingotAnyHardPlastic", },
            "List of items that are resistant to sulfuric acid from hbm's ntm");
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void loadIgnitingItemsListConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        invertIgnitingItemsList = config.getBoolean(
            "invertIgnitingItemsList",
            "listIgnitingItems",
            false,
            "Whether to invert the igniting items list.");
        ignitingItemsList = config.getStringList(
            "ignitingItemsList",
            "listIgnitingItems",
            new String[] { "minecraft:torch:minecraft:fire:10", "minecraft:lava_bucket:minecraft:fire:20",
                "minecraft:blaze_powder:minecraft:fire:40", "hbm:item.egg_balefire_shard:hbm:tile.balefire:15",
                "hbm:item.egg_balefire:hbm:tile.balefire:70", "Co60:minecraft:fire:30", "Sr90:minecraft:fire:20",
                "Au198:minecraft:fire:60", "Pb209:minecraft:fire:70", "Po210:minecraft:fire:50",
                "Pu238:minecraft:fire:10", "Cf251:minecraft:fire:15", "Cf252:minecraft:fire:25",
                "Es253:minecraft:fire:30", "WhitePhosphorus:minecraft:fire:10", "billetCo60:minecraft:fire:15",
                "billetSr90:minecraft:fire:10", "billetAu198:minecraft:fire:30", "billetPb209:minecraft:fire:35",
                "billetPo210:minecraft:fire:25", "billetPu238:minecraft:fire:5", "billetCf251:minecraft:fire:7",
                "billetCf252:minecraft:fire:12", "billetEs253:minecraft:fire:15", "dustSr90:minecraft:fire:40",
                "dustAu198:minecraft:fire:80", "dustPo210:minecraft:fire:50", },
            "List of items that can ignite blocks");
        if (config.hasChanged()) {
            config.save();
        }
    }
}
