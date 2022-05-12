package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PortalCubedItems {
    public static final ArmorMaterial PortalArmor = new PortalArmor();
    //public static final GelOrb GEL_ORB = new GelOrb(new FabricItemSettings().group(PortalCubed.PortalCubedGroup).maxCount(64));
    public static final Item LONG_FALL_BOOTS = new ArmorItem(PortalArmor, EquipmentSlot.FEET, new Item.Settings().group(PortalCubed.PortalCubedGroup).fireproof());
    public static final PortalGun PORTAL_GUN = new PortalGun(new FabricItemSettings().group(PortalCubed.PortalCubedGroup).maxCount(1).fireproof());
    public static final Item PORTAL_GUN_FRAME = new Item(new FabricItemSettings().group(PortalCubed.PortalCubedGroup).maxCount(1).fireproof());
    public static final Item PORTAL_GUN_CASING = new Item(new FabricItemSettings().group(PortalCubed.PortalCubedGroup).maxCount(1).fireproof());
    public static final Item MINI_BLACKHOLE = new Item(new FabricItemSettings().group(PortalCubed.PortalCubedGroup).maxCount(1).fireproof());
    //public static final PaintGun PAINT_GUN = new PaintGun(new FabricItemSettings().group(PortalCubed.PortalCubedGroup).maxCount(1).fireproof());
    public static final SpawnEggItem STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.STORAGE_CUBE, 1, 1, new FabricItemSettings().group(PortalCubed.PortalCubedGroup).maxCount(64));
    public static final SpawnEggItem COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.COMPANION_CUBE, 1, 1, new FabricItemSettings().group(PortalCubed.PortalCubedGroup).maxCount(64));

    public static void registerItems() {
        if (PortalCubedConfig.get().enabled.enableLongFallBoots)
            Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "long_fall_boots"), LONG_FALL_BOOTS);
        // Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "gel_orb"), GEL_ORB);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "portal_gun"), PORTAL_GUN);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "portal_gun_frame"), PORTAL_GUN_FRAME);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "portal_gun_casing"), PORTAL_GUN_CASING);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "mini_blackhole"), MINI_BLACKHOLE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "storage_cube"), STORAGE_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "companion_cube"), COMPANION_CUBE);
        // Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "paint_gun"), PAINT_GUN);
    }
}
