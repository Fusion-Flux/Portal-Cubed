package com.fusionflux.fluxtech.items;

import com.fusionflux.fluxtech.FluxTech;
import com.fusionflux.fluxtech.config.FluxTechConfig2;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluxTechItems {
    public static final ArmorMaterial FluxTechArmor = new FluxTechArmor();
    public static final Item LONG_FALL_BOOTS = new ArmorItem(FluxTechArmor, EquipmentSlot.FEET, new Item.Settings().group(FluxTech.FLUXTECH_GROUP).fireproof());
    public static final PortalGun PORTAL_GUN = new PortalGun(new FabricItemSettings().group(FluxTech.FLUXTECH_GROUP).maxCount(1).fireproof());


    public static void registerItems() {
        if (FluxTechConfig2.get().enabled.enableLongFallBoots)
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "long_fall_boots"), LONG_FALL_BOOTS);
        Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "portal_gun"), PORTAL_GUN);

    }


}
