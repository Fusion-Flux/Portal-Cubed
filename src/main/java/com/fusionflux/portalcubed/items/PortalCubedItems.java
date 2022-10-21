package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class PortalCubedItems {
    public static final ArmorMaterial PortalArmor = new PortalArmor();
    //public static final GelOrb GEL_ORB = new GelOrb(new QuiltItemSettings().group(PortalCubed.Portal2Group).maxCount(64));
    public static final Item LONG_FALL_BOOTS = new ArmorItem(PortalArmor, EquipmentSlot.FEET, new Item.Settings().group(PortalCubed.TestingElementsGroup).fireproof());
    public static final PortalGun PORTAL_GUN = new PortalGun(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());
    public static final Item PORTAL_GUN_FRAME = new Item(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());
    public static final Item PORTAL_GUN_CASING = new Item(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());
    public static final Item MINI_BLACKHOLE = new Item(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());
    //public static final PaintGun PAINT_GUN = new PaintGun(new QuiltItemSettings().group(PortalCubed.Portal2Group).maxCount(1).fireproof());
    public static final SpawnEggItem STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.STORAGE_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.COMPANION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem RADIO = new SpawnEggItem(PortalCubedEntities.RADIO, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem REDIRECTION_CUBE = new SpawnEggItem(PortalCubedEntities.REDIRECTION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem OLD_AP_CUBE = new SpawnEggItem(PortalCubedEntities.OLDAPCUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem PORTAL_1_COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.PORTAL_1_COMPANION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem PORTAL_1_STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.PORTAL_1_STORAGE_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem LIL_PINEAPPLE = new SpawnEggItem(PortalCubedEntities.LIL_PINEAPPLE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));


    public static final SpawnEggItem BEANS = new SpawnEggItem(PortalCubedEntities.BEANS, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem MUG = new SpawnEggItem(PortalCubedEntities.MUG, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem JUG = new SpawnEggItem(PortalCubedEntities.JUG, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem COMPUTER = new SpawnEggItem(PortalCubedEntities.COMPUTER, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem CHAIR = new SpawnEggItem(PortalCubedEntities.CHAIR, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem HOOPY = new SpawnEggItem(PortalCubedEntities.HOOPY, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem ANGERCORE = new SpawnEggItem(PortalCubedEntities.ANGER_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem MORALITYCORE = new SpawnEggItem(PortalCubedEntities.MORALITY_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem CAKECORE = new SpawnEggItem(PortalCubedEntities.CAKE_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem CURIOSITYCORE = new SpawnEggItem(PortalCubedEntities.CURIOSITY_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));


    public static void registerItems() {
        if (PortalCubedConfig.enableLongFallBoots)
            Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "long_fall_boots"), LONG_FALL_BOOTS);
        // Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "gel_orb"), GEL_ORB);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "portal_gun"), PORTAL_GUN);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "portal_gun_frame"), PORTAL_GUN_FRAME);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "portal_gun_casing"), PORTAL_GUN_CASING);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "mini_blackhole"), MINI_BLACKHOLE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "storage_cube"), STORAGE_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "companion_cube"), COMPANION_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "redirection_cube"), REDIRECTION_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "old_ap_cube"), OLD_AP_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "portal_1_companion_cube"), PORTAL_1_COMPANION_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "portal_1_storage_cube"), PORTAL_1_STORAGE_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "lil_pineapple"), LIL_PINEAPPLE);

        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "radio"), RADIO);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "beans"), BEANS);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "mug"), MUG);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "jug"), JUG);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "computer"), COMPUTER);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "chair"), CHAIR);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "hoopy"), HOOPY);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "anger_core"), ANGERCORE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "cake_core"), CAKECORE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "curiosity_core"), CURIOSITYCORE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "morality_core"), MORALITYCORE);

        // Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "paint_gun"), PAINT_GUN);
    }
}
