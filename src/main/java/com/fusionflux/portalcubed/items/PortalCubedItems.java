package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import static com.fusionflux.portalcubed.PortalCubed.TESTING_ELEMENTS_GROUP;
import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedItems {
    public static final ArmorMaterial PORTAL_ARMOR = new PortalArmor();
    public static final Item LONG_FALL_BOOTS = new ArmorItem(PORTAL_ARMOR, EquipmentSlot.FEET, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP).fireproof());
    public static final PortalGun PORTAL_GUN = new PortalGun(new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(1).fireproof());

    public static final PortalGunPrimary PORTAL_GUN_PRIMARY = new PortalGunPrimary(new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(1).fireproof());

    public static final PortalGunSecondary PORTAL_GUN_SECONDARY = new PortalGunSecondary(new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(1).fireproof());

    public static final PaintGun PAINT_GUN = new PaintGun(new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(1).fireproof());

    public static final Item PORTAL_GUN_FRAME = new Item(new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(1).fireproof());
    public static final Item PORTAL_GUN_CASING = new Item(new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(1).fireproof());
    public static final Item MINI_BLACKHOLE = new Item(new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(1).fireproof());
    public static final Item BLOCK_ITEM_ICON = new Item(new QuiltItemSettings().maxCount(1).fireproof());
    public static final SpawnEggItem STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.STORAGE_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.COMPANION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem RADIO = new SpawnEggItem(PortalCubedEntities.RADIO, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem REDIRECTION_CUBE = new SpawnEggItem(PortalCubedEntities.REDIRECTION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem OLD_AP_CUBE = new SpawnEggItem(PortalCubedEntities.OLD_AP_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem PORTAL_1_COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.PORTAL_1_COMPANION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem PORTAL_1_STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.PORTAL_1_STORAGE_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem LIL_PINEAPPLE = new SpawnEggItem(PortalCubedEntities.LIL_PINEAPPLE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));

    public static final SpawnEggItem BEANS = new SpawnEggItem(PortalCubedEntities.BEANS, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem MUG = new SpawnEggItem(PortalCubedEntities.MUG, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem JUG = new SpawnEggItem(PortalCubedEntities.JUG, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem COMPUTER = new SpawnEggItem(PortalCubedEntities.COMPUTER, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem CHAIR = new SpawnEggItem(PortalCubedEntities.CHAIR, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem HOOPY = new SpawnEggItem(PortalCubedEntities.HOOPY, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));

    public static final SpawnEggItem CORE_FRAME = new SpawnEggItem(PortalCubedEntities.CORE_FRAME, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem ANGER_CORE = new SpawnEggItem(PortalCubedEntities.ANGER_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem MORALITY_CORE = new SpawnEggItem(PortalCubedEntities.MORALITY_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem CAKE_CORE = new SpawnEggItem(PortalCubedEntities.CAKE_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem CURIOSITY_CORE = new SpawnEggItem(PortalCubedEntities.CURIOSITY_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));

    public static final SpawnEggItem SPACE_CORE = new SpawnEggItem(PortalCubedEntities.SPACE_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem FACT_CORE = new SpawnEggItem(PortalCubedEntities.FACT_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));
    public static final SpawnEggItem ADVENTURE_CORE = new SpawnEggItem(PortalCubedEntities.ADVENTURE_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64));

    public static final Item HAMMER = new Item(new QuiltItemSettings().group(TESTING_ELEMENTS_GROUP).maxCount(1));

    // TODO: Item behavior. When that's done, add it to the creative inventory.
    public static final Item ENERGY_PELLET = new EnergyPelletItem(new QuiltItemSettings().group(TESTING_ELEMENTS_GROUP));

    public static final Item STILL_ALIVE = new MusicDiscItem(15, new SoundEvent(id("disc/still_alive")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 177);
    public static final Item CARA_MIA_ADDIO = new MusicDiscItem(15, new SoundEvent(id("disc/cara_mia_addio")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 154);
    public static final Item WANT_YOU_GONE = new MusicDiscItem(15, new SoundEvent(id("disc/want_you_gone")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 142);
    public static final Item RECONSTRUCTING_MORE_SCIENCE = new MusicDiscItem(15, new SoundEvent(id("disc/reconstructing_more_science")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 157);

    public static void registerItems() {
        Registry.register(Registry.ITEM, id("long_fall_boots"), LONG_FALL_BOOTS);
        Registry.register(Registry.ITEM, id("portal_gun"), PORTAL_GUN);
        Registry.register(Registry.ITEM, id("portal_gun_primary"), PORTAL_GUN_PRIMARY);
        Registry.register(Registry.ITEM, id("portal_gun_secondary"), PORTAL_GUN_SECONDARY);

        Registry.register(Registry.ITEM, id("paint_gun"), PAINT_GUN);
        Registry.register(Registry.ITEM, id("portal_gun_frame"), PORTAL_GUN_FRAME);
        Registry.register(Registry.ITEM, id("portal_gun_casing"), PORTAL_GUN_CASING);
        Registry.register(Registry.ITEM, id("mini_blackhole"), MINI_BLACKHOLE);
        Registry.register(Registry.ITEM, id("storage_cube"), STORAGE_CUBE);
        Registry.register(Registry.ITEM, id("companion_cube"), COMPANION_CUBE);
        Registry.register(Registry.ITEM, id("redirection_cube"), REDIRECTION_CUBE);
        Registry.register(Registry.ITEM, id("old_ap_cube"), OLD_AP_CUBE);
        Registry.register(Registry.ITEM, id("portal_1_companion_cube"), PORTAL_1_COMPANION_CUBE);
        Registry.register(Registry.ITEM, id("portal_1_storage_cube"), PORTAL_1_STORAGE_CUBE);
        Registry.register(Registry.ITEM, id("lil_pineapple"), LIL_PINEAPPLE);

        Registry.register(Registry.ITEM, id("radio"), RADIO);
        Registry.register(Registry.ITEM, id("beans"), BEANS);
        Registry.register(Registry.ITEM, id("mug"), MUG);
        Registry.register(Registry.ITEM, id("jug"), JUG);
        Registry.register(Registry.ITEM, id("computer"), COMPUTER);
        Registry.register(Registry.ITEM, id("chair"), CHAIR);
        Registry.register(Registry.ITEM, id("hoopy"), HOOPY);
        Registry.register(Registry.ITEM, id("core_frame"), CORE_FRAME);
        Registry.register(Registry.ITEM, id("anger_core"), ANGER_CORE);
        Registry.register(Registry.ITEM, id("intelligence_core"), CAKE_CORE);
        Registry.register(Registry.ITEM, id("curiosity_core"), CURIOSITY_CORE);
        Registry.register(Registry.ITEM, id("morality_core"), MORALITY_CORE);

        Registry.register(Registry.ITEM, id("space_core"), SPACE_CORE);
        Registry.register(Registry.ITEM, id("fact_core"), FACT_CORE);
        Registry.register(Registry.ITEM, id("adventure_core"), ADVENTURE_CORE);

        Registry.register(Registry.ITEM, id("block_item_icon"), BLOCK_ITEM_ICON);

        Registry.register(Registry.ITEM, id("hammer"), HAMMER);
        Registry.register(Registry.ITEM, id("energy_pellet"), ENERGY_PELLET);

        Registry.register(Registry.ITEM, id("music_disc_still_alive"), STILL_ALIVE);
        Registry.register(Registry.ITEM, id("music_disc_cara_mia_addio"), CARA_MIA_ADDIO);
        Registry.register(Registry.ITEM, id("music_disc_want_you_gone"), WANT_YOU_GONE);
        Registry.register(Registry.ITEM, id("music_disc_reconstructing_more_science"), RECONSTRUCTING_MORE_SCIENCE);
    }
}
