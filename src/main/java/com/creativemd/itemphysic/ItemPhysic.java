package com.creativemd.itemphysic;

import static com.creativemd.itemphysic.ItemPhysic.MODID;
import static com.creativemd.itemphysic.ItemPhysic.NAME;
import static com.creativemd.itemphysic.ItemPhysic.VERSION;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.config.ItemConfigSystem;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryBurn;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryExplosion;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryFloat;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryIgniting;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistrySulfuricAcid;
import com.creativemd.itemphysic.list.ItemsWithMetaRegistryUndestroyable;
import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PickupPacket;
import com.creativemd.itemphysic.physics.ClientPhysic;
import com.creativemd.itemphysic.proxy.CommonProxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = MODID, version = VERSION, name = NAME)
public class ItemPhysic {

    public static final String MODID = "itemphysic";
    public static final String NAME = "ItemPhysic";
    public static final String VERSION = "1.2.7" + " kotmatross edition";
    public static final String CLIENTPROXY = "com.creativemd.itemphysic.proxy.ClientProxy";
    public static final String SERVERPROXY = "com.creativemd.itemphysic.proxy.CommonProxy";

    public static final String VERSION2 = "1.2.7";

    @Mod.Instance(MODID)
    public static ItemPhysic instance;

    @SidedProxy(clientSide = CLIENTPROXY, serverSide = SERVERPROXY)
    public static CommonProxy proxy;

    public static Configuration config;
    public static final Logger logger = LogManager.getLogger();

    public static float rotateSpeed = 1.0F;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        String configFolder = "config" + File.separator + MODID + File.separator;
        ItemPhysicConfig.loadBurnListConfig(new File(Launch.minecraftHome, configFolder + "BurnList.cfg"));
        ItemPhysicConfig.loadFloatListConfig(new File(Launch.minecraftHome, configFolder + "FloatList.cfg"));
        ItemPhysicConfig.loadExplosionListConfig(new File(Launch.minecraftHome, configFolder + "ExplosionList.cfg"));
        ItemPhysicConfig
            .loadUndestroyableListConfig(new File(Launch.minecraftHome, configFolder + "UndestroyableList.cfg"));
        ItemPhysicConfig
            .loadSulfuricAcidListConfig(new File(Launch.minecraftHome, configFolder + "SulfuricAcidList.cfg"));
        ItemPhysicConfig
            .loadIgnitingItemsListConfig(new File(Launch.minecraftHome, configFolder + "IgnitingItemsList.cfg"));

        if (!ItemTransformer.isLite) {
            enableItemDespawn = config.getBoolean(
                "enableItemDespawn",
                "Item",
                true,
                "Whether to allow items to despawn after some times. False to disable despawn.");
            despawnItem = config.getInt(
                "despawn",
                "Item",
                6000,
                0,
                2147483647,
                "Number of ticks an item takes to despawn (affected by enableItemDespawn).");
            customPickup = config.getBoolean(
                "customPickup",
                "Item",
                false,
                "Whether to enable a custom pickup mechanic with right click or sneaking (disables auto pickup).");
            customThrow = config.getBoolean(
                "customThrow",
                "Item",
                true,
                "Whether to enable a custom throwing mechanic when you hold the button.");
            showPowerText = config
                .getBoolean("showPowerText", "Item", true, "Whether to enable a \"Power\" text above HUD");
            disableCactusDamage = config
                .getBoolean("disableCactusDamage", "Item", true, "Whether to disable cactus damage for items");
            enableFallSounds = config
                .getBoolean("enableFallSounds", "Item", true, "Whether to allow items to make a sound when they fall.");
        }
        showPickupTooltip = config.getBoolean(
            "showPickupTooltip",
            "Item",
            true,
            "Whether to display the name and description of an item when hovering over it.");
        rotateSpeed = config.getFloat("rotateSpeed", "Item", 1.0F, 0, 100, "Speed of the item rotation.");
        config.save();

        event.getModMetadata().autogenerated = false;
        event.getModMetadata().name = EnumChatFormatting.RED + NAME;
        event.getModMetadata().version = EnumChatFormatting.GRAY + "" + EnumChatFormatting.BOLD + VERSION2;
        event.getModMetadata().credits = EnumChatFormatting.GOLD + "CreativeMD";

        event.getModMetadata().authorList.clear();
        event.getModMetadata().authorList.add(EnumChatFormatting.GOLD + "CreativeMD");
        event.getModMetadata().authorList.add(EnumChatFormatting.RED + "HRudyPlayZ");
        event.getModMetadata().authorList.add(EnumChatFormatting.AQUA + "Kotmatross");

        event.getModMetadata().url = EnumChatFormatting.GRAY
            + "https://github.com/kotmatross28729/ItemPhysic-Legacy-Unofficial";
        event.getModMetadata().description = EnumChatFormatting.GRAY
            + "A minecraft mod that adds physics to thrown items.";
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerEvents();
        proxy.init(this);
        if (!ItemTransformer.isLite) {
            MinecraftForge.EVENT_BUS.register(new EventHandler());
            FMLCommonHandler.instance()
                .bus()
                .register(new EventHandler());
            initFull();
        } else {
            MinecraftForge.EVENT_BUS.register(new EventHandlerLite());
            FMLCommonHandler.instance()
                .bus()
                .register(new EventHandlerLite());
        }
    }

    @Optional.Method(modid = "creativecore")
    public static void initFull() {
        CreativeCorePacket.registerPacket(DropPacket.class, "IPDrop");
        CreativeCorePacket.registerPacket(PickupPacket.class, "IPPick");

        try {
            if (!ItemTransformer.isLite && Loader.isModLoaded("ingameconfigmanager")) ItemConfigSystem.loadConfig();
        } catch (Exception e) {}
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onRender(TickEvent.RenderTickEvent evt) {
        ClientPhysic.tick = System.nanoTime();
    }

    public static boolean isHBMLoaded = false;

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("hbm")) {
            isHBMLoaded = true;
        }
        // This approach also acts like a hash function, initializing 2 lists at a late stage of loading so that the
        // lists don't have to be checked constantly
        for (String itemName : ItemPhysicConfig.burnList) {
            String modId;
            String itemNameOnly;
            int metadata = 0;
            boolean ignoremeta = false;

            String[] parts = itemName.split(":");
            if (parts.length >= 2) {
                modId = parts[0];
                itemNameOnly = parts[1];
                if (parts.length == 3) {
                    try {
                        metadata = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        ignoremeta = Boolean.parseBoolean(parts[2]);
                    }
                }
                Item item = GameRegistry.findItem(modId, itemNameOnly);
                if (item != null) {
                    ItemsWithMetaRegistryBurn.ItemWithMetaBurn Item = new ItemsWithMetaRegistryBurn.ItemWithMetaBurn(
                        item,
                        metadata,
                        ignoremeta);
                    ItemsWithMetaRegistryBurn.BurnItems.add(Item);
                }
            } else if (parts.length == 1) {
                List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                if (oredictNames.contains(itemName)) {
                    for (ItemStack oreStack : OreDictionary.getOres(itemName)) {
                        ItemsWithMetaRegistryBurn.ItemWithMetaBurn Item = new ItemsWithMetaRegistryBurn.ItemWithMetaBurn(
                            oreStack.getItem(),
                            oreStack.getItemDamage(),
                            ignoremeta);
                        ItemsWithMetaRegistryBurn.BurnItems.add(Item);
                    }
                }
            }
        }
        for (String itemName : ItemPhysicConfig.floatList) {
            String modId;
            String itemNameOnly;
            int metadata = 0;
            boolean ignoremeta = false;
            List<String> liquidsList = new ArrayList<>();
            liquidsList.add("fluid.tile.water"); // default

            String[] parts = itemName.split(":");
            if (parts.length >= 2) {
                modId = parts[0];
                itemNameOnly = parts[1];
                if (parts.length == 3) {
                    try {
                        metadata = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        ignoremeta = Boolean.parseBoolean(parts[2]);
                    }
                    if (!ignoremeta && metadata == 0) {
                        liquidsList.set(0, parts[2]); // assert that ignoremeta missing (I assure you, no one will write
                                                      // :false)
                    }
                } else if (parts.length > 3) {
                    try {
                        metadata = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        ignoremeta = Boolean.parseBoolean(parts[2]);
                    }
                    liquidsList.addAll(
                        Arrays.asList(parts)
                            .subList(3, parts.length));
                }
                Item item = GameRegistry.findItem(modId, itemNameOnly);
                if (item != null) {
                    String[] liquidsArray = liquidsList.toArray(new String[0]);
                    ItemsWithMetaRegistryFloat.ItemWithMetaFloat Item = new ItemsWithMetaRegistryFloat.ItemWithMetaFloat(
                        item,
                        metadata,
                        ignoremeta,
                        liquidsArray);
                    ItemsWithMetaRegistryFloat.FloatItems.add(Item);
                }
            } else if (parts.length == 1) {
                List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                if (oredictNames.contains(itemName)) {
                    for (ItemStack oreStack : OreDictionary.getOres(itemName)) {
                        String[] liquidsArray = liquidsList.toArray(new String[0]);
                        if (oreStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                            ItemsWithMetaRegistryFloat.ItemWithMetaFloat Item = new ItemsWithMetaRegistryFloat.ItemWithMetaFloat(
                                oreStack.getItem(),
                                oreStack.getItemDamage(),
                                true,
                                liquidsArray);
                            ItemsWithMetaRegistryFloat.FloatItems.add(Item);
                        } else {
                            ItemsWithMetaRegistryFloat.ItemWithMetaFloat Item = new ItemsWithMetaRegistryFloat.ItemWithMetaFloat(
                                oreStack.getItem(),
                                oreStack.getItemDamage(),
                                ignoremeta,
                                liquidsArray);
                            ItemsWithMetaRegistryFloat.FloatItems.add(Item);
                        }
                    }
                }
            }
        }
        for (String itemName : ItemPhysicConfig.explosionList) {
            String modId;
            String itemNameOnly;
            int metadata = 0;
            boolean ignoremeta = false;

            String[] parts = itemName.split(":");
            if (parts.length >= 2) {
                modId = parts[0];
                itemNameOnly = parts[1];
                if (parts.length == 3) {
                    try {
                        metadata = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        ignoremeta = Boolean.parseBoolean(parts[2]);
                    }
                }
                Item item = GameRegistry.findItem(modId, itemNameOnly);
                if (item != null) {
                    ItemsWithMetaRegistryExplosion.ItemsWithMetaExplosion Item = new ItemsWithMetaRegistryExplosion.ItemsWithMetaExplosion(
                        item,
                        metadata,
                        ignoremeta);
                    ItemsWithMetaRegistryExplosion.ExplosionItems.add(Item);
                }
            } else if (parts.length == 1) {
                List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                if (oredictNames.contains(itemName)) {
                    for (ItemStack oreStack : OreDictionary.getOres(itemName)) {
                        ItemsWithMetaRegistryExplosion.ItemsWithMetaExplosion Item = new ItemsWithMetaRegistryExplosion.ItemsWithMetaExplosion(
                            oreStack.getItem(),
                            oreStack.getItemDamage(),
                            ignoremeta);
                        ItemsWithMetaRegistryExplosion.ExplosionItems.add(Item);
                    }
                }
            }
        }
        for (String itemName : ItemPhysicConfig.undestroyableList) {
            String modId;
            String itemNameOnly;
            int metadata = 0;
            boolean ignoremeta = false;

            String[] parts = itemName.split(":");
            if (parts.length >= 2) {
                modId = parts[0];
                itemNameOnly = parts[1];
                if (parts.length == 3) {
                    try {
                        metadata = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        ignoremeta = Boolean.parseBoolean(parts[2]);
                    }
                }
                Item item = GameRegistry.findItem(modId, itemNameOnly);
                if (item != null) {
                    ItemsWithMetaRegistryUndestroyable.ItemWithMetaUndestroyable Item = new ItemsWithMetaRegistryUndestroyable.ItemWithMetaUndestroyable(
                        item,
                        metadata,
                        ignoremeta);
                    ItemsWithMetaRegistryUndestroyable.UndestroyableItems.add(Item);
                }
            } else if (parts.length == 1) {
                List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                if (oredictNames.contains(itemName)) {
                    for (ItemStack oreStack : OreDictionary.getOres(itemName)) {
                        ItemsWithMetaRegistryUndestroyable.ItemWithMetaUndestroyable Item = new ItemsWithMetaRegistryUndestroyable.ItemWithMetaUndestroyable(
                            oreStack.getItem(),
                            oreStack.getItemDamage(),
                            ignoremeta);
                        ItemsWithMetaRegistryUndestroyable.UndestroyableItems.add(Item);
                    }
                }
            }
        }
        if (isHBMLoaded) {
            for (String itemName : ItemPhysicConfig.sulfuricAcidList) {
                String modId;
                String itemNameOnly;
                int metadata = 0;
                boolean ignoremeta = false;

                String[] parts = itemName.split(":");
                if (parts.length >= 2) {
                    modId = parts[0];
                    itemNameOnly = parts[1];
                    if (parts.length == 3) {
                        try {
                            metadata = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            ignoremeta = Boolean.parseBoolean(parts[2]);
                        }
                    }
                    Item item = GameRegistry.findItem(modId, itemNameOnly);
                    if (item != null) {
                        ItemsWithMetaRegistrySulfuricAcid.ItemsWithMetaSulfuricAcid Item = new ItemsWithMetaRegistrySulfuricAcid.ItemsWithMetaSulfuricAcid(
                            item,
                            metadata,
                            ignoremeta);
                        ItemsWithMetaRegistrySulfuricAcid.SulfuricAcidItems.add(Item);
                    }
                } else if (parts.length == 1) {
                    List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                    if (oredictNames.contains(itemName)) {
                        for (ItemStack oreStack : OreDictionary.getOres(itemName)) {
                            ItemsWithMetaRegistrySulfuricAcid.ItemsWithMetaSulfuricAcid Item = new ItemsWithMetaRegistrySulfuricAcid.ItemsWithMetaSulfuricAcid(
                                oreStack.getItem(),
                                oreStack.getItemDamage(),
                                ignoremeta);
                            ItemsWithMetaRegistrySulfuricAcid.SulfuricAcidItems.add(Item);
                        }
                    }
                }
            }
        }
        for (String entry : ItemPhysicConfig.ignitingItemsList) {
            String modIdItem;
            String itemName;
            int metadataItem = 0;
            boolean ignoremetaItem = false;

            String modIdBlock;
            String blockName;
            int metadataBlock = 0;
            boolean ignoremetaBlock = false;

            int igniteChance = 10;

            String[] parts = entry.split(":");

            if (parts.length > 4) {
                if (parts.length == 5) {
                    modIdItem = parts[0];
                    itemName = parts[1];
                    modIdBlock = parts[2];
                    blockName = parts[3];
                    try {
                        igniteChance = Integer.parseInt(parts[4]);
                    } catch (NumberFormatException ignored) {}
                } else if (parts.length == 6) {
                    modIdItem = parts[0];
                    itemName = parts[1];
                    try {
                        metadataItem = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        ignoremetaItem = Boolean.parseBoolean(parts[2]);
                    }
                    if (metadataItem == 0 && !ignoremetaItem) {
                        modIdBlock = parts[2];
                        blockName = parts[3];
                        try {
                            metadataItem = Integer.parseInt(parts[4]);
                        } catch (NumberFormatException e) {
                            ignoremetaItem = Boolean.parseBoolean(parts[4]);
                        }
                    } else {
                        modIdBlock = parts[3];
                        blockName = parts[4];
                    }
                    try {
                        igniteChance = Integer.parseInt(parts[5]);
                    } catch (NumberFormatException ignored) {}
                } else {
                    modIdItem = parts[0];
                    itemName = parts[1];
                    try {
                        metadataItem = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        ignoremetaItem = Boolean.parseBoolean(parts[2]);
                    }
                    modIdBlock = parts[3];
                    blockName = parts[4];
                    try {
                        metadataBlock = Integer.parseInt(parts[5]);
                    } catch (NumberFormatException e) {
                        ignoremetaBlock = Boolean.parseBoolean(parts[5]);
                    }
                    try {
                        igniteChance = Integer.parseInt(parts[6]);
                    } catch (NumberFormatException ignored) {}
                }
                Item item = GameRegistry.findItem(modIdItem, itemName);
                Block block = GameRegistry.findBlock(modIdBlock, blockName);

                if (item != null && block != null) {
                    ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting ItemAndBlock = new ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting(
                        item,
                        metadataItem,
                        ignoremetaItem,
                        block,
                        metadataBlock,
                        ignoremetaBlock,
                        igniteChance);
                    ItemsWithMetaRegistryIgniting.IgnitingItems.add(ItemAndBlock);
                }
            } else if (parts.length == 4) {
                String oreDict = parts[0];
                modIdBlock = parts[1];
                blockName = parts[2];
                try {
                    igniteChance = Integer.parseInt(parts[3]);
                } catch (NumberFormatException ignored) {}

                Block block = GameRegistry.findBlock(modIdBlock, blockName);

                List<String> oredictNames = Arrays.asList(OreDictionary.getOreNames());
                if (oredictNames.contains(oreDict)) {
                    for (ItemStack oreStack : OreDictionary.getOres(oreDict)) {
                        ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting Item = new ItemsWithMetaRegistryIgniting.ItemWithMetaIgniting(
                            oreStack.getItem(),
                            oreStack.getItemDamage(),
                            ignoremetaItem,
                            block,
                            metadataBlock,
                            ignoremetaBlock,
                            igniteChance);
                        ItemsWithMetaRegistryIgniting.IgnitingItems.add(Item);
                    }
                }
            }
        }
        boolean client = FMLLaunchHandler.side()
            .isClient();
        if (client) {
            MinecraftForge.EVENT_BUS.register(new ItemPhysicHandler());
            FMLCommonHandler.instance()
                .bus()
                .register(new ItemPhysicHandler());
        }
    }

    public static boolean enableItemDespawn;
    public static int despawnItem;
    public static boolean customPickup;
    public static boolean customThrow;
    public static boolean showPowerText;
    public static boolean disableCactusDamage;
    public static boolean showPickupTooltip = true;

    public static boolean enableFallSounds = true;
}
