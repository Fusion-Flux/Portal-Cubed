package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.DispenserBlock;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.tag.api.QuiltTagKey;
import org.quiltmc.qsl.tag.api.TagType;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedItems {
    public static final ArmorMaterial PORTAL_ARMOR = new PortalArmor();
    public static final Item LONG_FALL_BOOTS = new ArmorItem(PORTAL_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant());
    public static final PortalGun PORTAL_GUN = new PortalGun(new QuiltItemSettings().stacksTo(1).fireResistant());

    public static final PortalGunPrimary PORTAL_GUN_PRIMARY = new PortalGunPrimary(new QuiltItemSettings().stacksTo(1).fireResistant());

    public static final PortalGunSecondary PORTAL_GUN_SECONDARY = new PortalGunSecondary(new QuiltItemSettings().stacksTo(1).fireResistant());

    public static final PaintGun PAINT_GUN = new PaintGun(new QuiltItemSettings().stacksTo(1).fireResistant());

    public static final Item PORTAL_GUN_FRAME = new Item(new QuiltItemSettings().stacksTo(1).fireResistant());
    public static final Item PORTAL_GUN_CASING = new Item(new QuiltItemSettings().stacksTo(1).fireResistant());
    public static final Item MINI_BLACKHOLE = new Item(new QuiltItemSettings().stacksTo(1).fireResistant());
    public static final Item BLOCK_ITEM_ICON = new Item(new QuiltItemSettings().stacksTo(1).fireResistant());
    public static final SpawnEggItem STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.STORAGE_CUBE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.COMPANION_CUBE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem RADIO = new SpawnEggItem(PortalCubedEntities.RADIO, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem REDIRECTION_CUBE = new SpawnEggItem(PortalCubedEntities.REDIRECTION_CUBE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem OLD_AP_CUBE = new SpawnEggItem(PortalCubedEntities.OLD_AP_CUBE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem PORTAL_1_COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.PORTAL_1_COMPANION_CUBE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem PORTAL_1_STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.PORTAL_1_STORAGE_CUBE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem LIL_PINEAPPLE = new SpawnEggItem(PortalCubedEntities.LIL_PINEAPPLE, 1, 1, new QuiltItemSettings());

    public static final SpawnEggItem BEANS = new SpawnEggItem(PortalCubedEntities.BEANS, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem MUG = new SpawnEggItem(PortalCubedEntities.MUG, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem JUG = new SpawnEggItem(PortalCubedEntities.JUG, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem COMPUTER = new SpawnEggItem(PortalCubedEntities.COMPUTER, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem CHAIR = new SpawnEggItem(PortalCubedEntities.CHAIR, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem HOOPY = new SpawnEggItem(PortalCubedEntities.HOOPY, 1, 1, new QuiltItemSettings());

    public static final SpawnEggItem CORE_FRAME = new SpawnEggItem(PortalCubedEntities.CORE_FRAME, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem ANGER_CORE = new SpawnEggItem(PortalCubedEntities.ANGER_CORE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem MORALITY_CORE = new SpawnEggItem(PortalCubedEntities.MORALITY_CORE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem CAKE_CORE = new SpawnEggItem(PortalCubedEntities.CAKE_CORE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem CURIOSITY_CORE = new SpawnEggItem(PortalCubedEntities.CURIOSITY_CORE, 1, 1, new QuiltItemSettings());

    public static final SpawnEggItem SPACE_CORE = new SpawnEggItem(PortalCubedEntities.SPACE_CORE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem FACT_CORE = new SpawnEggItem(PortalCubedEntities.FACT_CORE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem ADVENTURE_CORE = new SpawnEggItem(PortalCubedEntities.ADVENTURE_CORE, 1, 1, new QuiltItemSettings());
    public static final SpawnEggItem TURRET = new SpawnEggItem(PortalCubedEntities.TURRET, 1, 1, new QuiltItemSettings());


    public static final Item HAMMER = new Item(new QuiltItemSettings().stacksTo(1));

    public static final EnergyPelletItem ENERGY_PELLET = new EnergyPelletItem(new QuiltItemSettings(), false);
    public static final EnergyPelletItem SUPER_PELLET = new EnergyPelletItem(new QuiltItemSettings(), true);

    public static final CrowbarItem CROWBAR = new CrowbarItem(new QuiltItemSettings());

    public static final Item STILL_ALIVE = new RecordItem(15, SoundEvent.createVariableRangeEvent(id("disc/still_alive")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 177);
    public static final Item CARA_MIA_ADDIO = new RecordItem(15, SoundEvent.createVariableRangeEvent(id("disc/cara_mia_addio")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 154);
    public static final Item WANT_YOU_GONE = new RecordItem(15, SoundEvent.createVariableRangeEvent(id("disc/want_you_gone")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 142);
    public static final Item RECONSTRUCTING_MORE_SCIENCE = new RecordItem(15, SoundEvent.createVariableRangeEvent(id("disc/reconstructing_more_science")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 157);

    public static final TagKey<Item> HOLDS_OBJECT = QuiltTagKey.of(Registries.ITEM, id("holds_object"), TagType.CLIENT_ONLY);
    public static final TagKey<Item> LAYS_ON_FLOOR = QuiltTagKey.of(Registries.ITEM, id("lays_on_floor"), TagType.CLIENT_ONLY);

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, id("long_fall_boots"), LONG_FALL_BOOTS);
        Registry.register(BuiltInRegistries.ITEM, id("portal_gun"), PORTAL_GUN);
        Registry.register(BuiltInRegistries.ITEM, id("portal_gun_primary"), PORTAL_GUN_PRIMARY);
        Registry.register(BuiltInRegistries.ITEM, id("portal_gun_secondary"), PORTAL_GUN_SECONDARY);

        Registry.register(BuiltInRegistries.ITEM, id("paint_gun"), PAINT_GUN);
        Registry.register(BuiltInRegistries.ITEM, id("portal_gun_frame"), PORTAL_GUN_FRAME);
        Registry.register(BuiltInRegistries.ITEM, id("portal_gun_casing"), PORTAL_GUN_CASING);
        Registry.register(BuiltInRegistries.ITEM, id("mini_blackhole"), MINI_BLACKHOLE);
        Registry.register(BuiltInRegistries.ITEM, id("storage_cube"), STORAGE_CUBE);
        Registry.register(BuiltInRegistries.ITEM, id("companion_cube"), COMPANION_CUBE);
        Registry.register(BuiltInRegistries.ITEM, id("redirection_cube"), REDIRECTION_CUBE);
        Registry.register(BuiltInRegistries.ITEM, id("old_ap_cube"), OLD_AP_CUBE);
        Registry.register(BuiltInRegistries.ITEM, id("portal_1_companion_cube"), PORTAL_1_COMPANION_CUBE);
        Registry.register(BuiltInRegistries.ITEM, id("portal_1_storage_cube"), PORTAL_1_STORAGE_CUBE);
        Registry.register(BuiltInRegistries.ITEM, id("lil_pineapple"), LIL_PINEAPPLE);

        Registry.register(BuiltInRegistries.ITEM, id("radio"), RADIO);
        Registry.register(BuiltInRegistries.ITEM, id("beans"), BEANS);
        Registry.register(BuiltInRegistries.ITEM, id("mug"), MUG);
        Registry.register(BuiltInRegistries.ITEM, id("jug"), JUG);
        Registry.register(BuiltInRegistries.ITEM, id("computer"), COMPUTER);
        Registry.register(BuiltInRegistries.ITEM, id("chair"), CHAIR);
        Registry.register(BuiltInRegistries.ITEM, id("hoopy"), HOOPY);
        Registry.register(BuiltInRegistries.ITEM, id("core_frame"), CORE_FRAME);
        Registry.register(BuiltInRegistries.ITEM, id("anger_core"), ANGER_CORE);
        Registry.register(BuiltInRegistries.ITEM, id("intelligence_core"), CAKE_CORE);
        Registry.register(BuiltInRegistries.ITEM, id("curiosity_core"), CURIOSITY_CORE);
        Registry.register(BuiltInRegistries.ITEM, id("morality_core"), MORALITY_CORE);

        Registry.register(BuiltInRegistries.ITEM, id("space_core"), SPACE_CORE);
        Registry.register(BuiltInRegistries.ITEM, id("fact_core"), FACT_CORE);
        Registry.register(BuiltInRegistries.ITEM, id("adventure_core"), ADVENTURE_CORE);
        Registry.register(BuiltInRegistries.ITEM, id("turret"), TURRET);

        Registry.register(BuiltInRegistries.ITEM, id("block_item_icon"), BLOCK_ITEM_ICON);

        Registry.register(BuiltInRegistries.ITEM, id("hammer"), HAMMER);
        Registry.register(BuiltInRegistries.ITEM, id("energy_pellet"), ENERGY_PELLET);
        Registry.register(BuiltInRegistries.ITEM, id("super_pellet"), SUPER_PELLET);
        Registry.register(BuiltInRegistries.ITEM, id("crowbar"), CROWBAR);

        Registry.register(BuiltInRegistries.ITEM, id("music_disc_still_alive"), STILL_ALIVE);
        Registry.register(BuiltInRegistries.ITEM, id("music_disc_cara_mia_addio"), CARA_MIA_ADDIO);
        Registry.register(BuiltInRegistries.ITEM, id("music_disc_want_you_gone"), WANT_YOU_GONE);
        Registry.register(BuiltInRegistries.ITEM, id("music_disc_reconstructing_more_science"), RECONSTRUCTING_MORE_SCIENCE);

        DispenserBlock.registerBehavior(ENERGY_PELLET, ENERGY_PELLET.createDispenserBehavior());
        DispenserBlock.registerBehavior(SUPER_PELLET, SUPER_PELLET.createDispenserBehavior());
    }
}
