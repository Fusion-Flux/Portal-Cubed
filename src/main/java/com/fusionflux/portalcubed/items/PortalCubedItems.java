package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedItems {
    public static final ArmorMaterial PortalArmor = new PortalArmor();
    public static final Item LONG_FALL_BOOTS = new ArmorItem(PortalArmor, EquipmentSlot.FEET, new Item.Settings().group(PortalCubed.TestingElementsGroup).fireproof());
    public static final PortalGun PORTAL_GUN = new PortalGun(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());

    public static final PaintGun PAINT_GUN = new PaintGun(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());

    public static final Item PORTAL_GUN_FRAME = new Item(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());
    public static final Item PORTAL_GUN_CASING = new Item(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());
    public static final Item MINI_BLACKHOLE = new Item(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(1).fireproof());
    public static final Item BLOCK_ITEM_ICON = new Item(new QuiltItemSettings().maxCount(1).fireproof());
    public static final SpawnEggItem STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.STORAGE_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.COMPANION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem RADIO = new SpawnEggItem(PortalCubedEntities.RADIO, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem REDIRECTION_CUBE = new SpawnEggItem(PortalCubedEntities.REDIRECTION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem OLD_AP_CUBE = new SpawnEggItem(PortalCubedEntities.OLD_AP_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem PORTAL_1_COMPANION_CUBE = new SpawnEggItem(PortalCubedEntities.PORTAL_1_COMPANION_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem PORTAL_1_STORAGE_CUBE = new SpawnEggItem(PortalCubedEntities.PORTAL_1_STORAGE_CUBE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem LIL_PINEAPPLE = new SpawnEggItem(PortalCubedEntities.LIL_PINEAPPLE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));


    public static final SpawnEggItem BEANS = new SpawnEggItem(PortalCubedEntities.BEANS, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem MUG = new SpawnEggItem(PortalCubedEntities.MUG, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem JUG = new SpawnEggItem(PortalCubedEntities.JUG, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem COMPUTER = new SpawnEggItem(PortalCubedEntities.COMPUTER, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem CHAIR = new SpawnEggItem(PortalCubedEntities.CHAIR, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem HOOPY = new SpawnEggItem(PortalCubedEntities.HOOPY, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem ANGER_CORE = new SpawnEggItem(PortalCubedEntities.ANGER_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem MORALITY_CORE = new SpawnEggItem(PortalCubedEntities.MORALITY_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem CAKE_CORE = new SpawnEggItem(PortalCubedEntities.CAKE_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem CURIOSITY_CORE = new SpawnEggItem(PortalCubedEntities.CURIOSITY_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));

    public static final SpawnEggItem SPACE_CORE = new SpawnEggItem(PortalCubedEntities.SPACE_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem FACT_CORE = new SpawnEggItem(PortalCubedEntities.FACT_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));
    public static final SpawnEggItem ADVENTURE_CORE = new SpawnEggItem(PortalCubedEntities.ADVENTURE_CORE, 1, 1, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64));

    public static final Item SUBJECT_NAME_HERE = new MusicDiscItem(15, new SoundEvent(id("disc/subject_name_here")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 105);
    public static final Item TASTE_OF_BLOOD = new MusicDiscItem(15, new SoundEvent(id("disc/taste_of_blood")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 187);
    public static final Item ANDROID_HELL = new MusicDiscItem(15, new SoundEvent(id("disc/android_hell")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 226);
    public static final Item SELF_ESTEEM_FUND = new MusicDiscItem(15, new SoundEvent(id("disc/self_esteem_fund")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 211);
    public static final Item PROCEDURAL_JIGGLE_BONE = new MusicDiscItem(15, new SoundEvent(id("disc/procedural_jiggle_bone")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 275);
    public static final Item NO_CAKE_FOR_YOU = new MusicDiscItem(15, new SoundEvent(id("disc/no_cake_for_you")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 246);
    public static final Item _4000_DEGREES_KELVIN = new MusicDiscItem(15, new SoundEvent(id("disc/4000_degrees_kelvin")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 62);
    public static final Item STOP_WHAT_YOU_ARE_DOING = new MusicDiscItem(15, new SoundEvent(id("disc/stop_what_you_are_doing")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 241);
    public static final Item PARTY_ESCORT = new MusicDiscItem(15, new SoundEvent(id("disc/party_escort")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 262);
    public static final Item YOURE_NOT_A_GOOD_PERSON = new MusicDiscItem(15, new SoundEvent(id("disc/youre_not_a_good_person")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 85);
    public static final Item YOU_CANT_ESCAPE_YOU_KNOW = new MusicDiscItem(15, new SoundEvent(id("disc/you_cant_escape_you_know")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 385);
    public static final Item STILL_ALIVE = new MusicDiscItem(15, new SoundEvent(id("disc/still_alive")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 177);
    public static final Item CARA_MIA_ADDIO = new MusicDiscItem(15, new SoundEvent(id("disc/cara_mia_addio")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 154);
    public static final Item WANT_YOU_GONE = new MusicDiscItem(15, new SoundEvent(id("disc/want_you_gone")), new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE), 142);

    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "long_fall_boots"), LONG_FALL_BOOTS);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "portal_gun"), PORTAL_GUN);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "paint_gun"), PAINT_GUN);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "portal_gun_frame"), PORTAL_GUN_FRAME);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "portal_gun_casing"), PORTAL_GUN_CASING);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "mini_blackhole"), MINI_BLACKHOLE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "storage_cube"), STORAGE_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "companion_cube"), COMPANION_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "redirection_cube"), REDIRECTION_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "old_ap_cube"), OLD_AP_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "portal_1_companion_cube"), PORTAL_1_COMPANION_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "portal_1_storage_cube"), PORTAL_1_STORAGE_CUBE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "lil_pineapple"), LIL_PINEAPPLE);

        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "radio"), RADIO);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "beans"), BEANS);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "mug"), MUG);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "jug"), JUG);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "computer"), COMPUTER);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "chair"), CHAIR);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "hoopy"), HOOPY);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "anger_core"), ANGER_CORE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "intelligence_core"), CAKE_CORE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "curiosity_core"), CURIOSITY_CORE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "morality_core"), MORALITY_CORE);

        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "space_core"), SPACE_CORE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "fact_core"), FACT_CORE);
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "adventure_core"), ADVENTURE_CORE);

        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "block_item_icon"), BLOCK_ITEM_ICON);

        Registry.register(Registry.ITEM, id("music_disc_subject_name_here"), SUBJECT_NAME_HERE);
        Registry.register(Registry.ITEM, id("music_disc_taste_of_blood"), TASTE_OF_BLOOD);
        Registry.register(Registry.ITEM, id("music_disc_android_hell"), ANDROID_HELL);
        Registry.register(Registry.ITEM, id("music_disc_self_esteem_fund"), SELF_ESTEEM_FUND);
        Registry.register(Registry.ITEM, id("music_disc_procedural_jiggle_bone"), PROCEDURAL_JIGGLE_BONE);
        Registry.register(Registry.ITEM, id("music_disc_no_cake_for_you"), NO_CAKE_FOR_YOU);
        Registry.register(Registry.ITEM, id("music_disc_4000_degrees_kelvin"), _4000_DEGREES_KELVIN);
        Registry.register(Registry.ITEM, id("music_disc_stop_what_you_are_doing"), STOP_WHAT_YOU_ARE_DOING);
        Registry.register(Registry.ITEM, id("music_disc_party_escort"), PARTY_ESCORT);
        Registry.register(Registry.ITEM, id("music_disc_youre_not_a_good_person"), YOURE_NOT_A_GOOD_PERSON);
        Registry.register(Registry.ITEM, id("music_disc_you_cant_escape_you_know"), YOU_CANT_ESCAPE_YOU_KNOW);
        Registry.register(Registry.ITEM, id("music_disc_still_alive"), STILL_ALIVE);
        Registry.register(Registry.ITEM, id("music_disc_cara_mia_addio"), CARA_MIA_ADDIO);
        Registry.register(Registry.ITEM, id("music_disc_want_you_gone"), WANT_YOU_GONE);
    }
}
