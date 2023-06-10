package com.fusionflux.portalcubed.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import org.quiltmc.qsl.tag.api.QuiltTagKey;
import org.quiltmc.qsl.tag.api.TagType;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedSounds {
    public static final ResourceLocation ERROR = id("error");

    public static final ResourceLocation GEL_BOUNCE = id("gel_bounce");
    public static final ResourceLocation GEL_RUN = id("gel_run");
    public static final ResourceLocation GEL_SPLAT = id("gel_splat");
    public static final ResourceLocation PORTAL_GUN_PRIMARY = id("portal_gun_primary");
    public static final ResourceLocation PORTAL_GUN_SECONDARY = id("portal_gun_secondary");
    public static final ResourceLocation PORTAL_INVALID_SURFACE = id("portal_invalid_surface");
    public static final ResourceLocation NOTHING_TO_GRAB = id("nothing_to_grab");
    public static final ResourceLocation PORTAL_AMBIANCE = id("portal_ambience");
    public static final ResourceLocation PORTAL_ENTER = id("portal_enter");
    public static final ResourceLocation PORTAL_EXIT = id("portal_exit");
    public static final ResourceLocation PORTAL_OPEN = id("portal_open");
    public static final ResourceLocation PORTAL_CLOSE = id("portal_close");
    public static final ResourceLocation PORTAL_FIZZLE = id("portal_fizzle");
    public static final ResourceLocation CUBE_HIT_HIGH = id("cube_hit_high");
    public static final ResourceLocation CUBE_HIT_LOW = id("cube_hit_low");
    public static final ResourceLocation CUBE_SCRAPE = id("cube_scrape");
    public static final ResourceLocation COMPANION_CUBE_AMBIANCE = id("companion_cube_ambiance");
    public static final ResourceLocation MATERIAL_EMANCIPATION = id("material_emancipation");
    public static final ResourceLocation CATAPULT_LAUNCH = id("catapult_launch");

    public static final ResourceLocation PEDESTAL_BUTTON_PRESS = id("pedestal_button_press");
    public static final ResourceLocation PEDESTAL_BUTTON_RELEASE = id("pedestal_button_release");
    public static final ResourceLocation OLD_AP_PEDESTAL_BUTTON_PRESS = id("old_ap_pedestal_button_press");
    public static final ResourceLocation OLD_AP_PEDESTAL_BUTTON_RELEASE = id("old_ap_pedestal_button_release");
    public static final ResourceLocation FLOOR_BUTTON_PRESS = id("floor_button_press");
    public static final ResourceLocation FLOOR_BUTTON_RELEASE = id("floor_button_release");
    public static final ResourceLocation OLD_AP_FLOOR_BUTTON_PRESS = id("old_ap_floor_button_press");
    public static final ResourceLocation OLD_AP_FLOOR_BUTTON_RELEASE = id("old_ap_floor_button_release");

    public static final ResourceLocation ROCKET_FIRE = id("rocket/fire");
    public static final ResourceLocation ROCKET_FLY = id("rocket/fly");
    public static final ResourceLocation ROCKET_LOCKED = id("rocket/locked");
    public static final ResourceLocation ROCKET_LOCKING = id("rocket/locking");
    public static final ResourceLocation ROCKET_EXPLODE = id("rocket/explode");
    public static final ResourceLocation ROCKET_GOO = id("rocket/goo");

    public static final ResourceLocation LASER_EMITTER_ACTIVATE = id("laser/emitter_activate");
    public static final ResourceLocation LASER_BEAM_MUSIC = id("laser/beam_music");
    public static final ResourceLocation LASER_NODE_MUSIC = id("laser/node_music");
    public static final ResourceLocation LASER_NODE_ACTIVATE = id("laser/node_activate");
    public static final ResourceLocation LASER_NODE_DEACTIVATE = id("laser/node_deactivate");
    public static final ResourceLocation LASER_TRIPLE_LASER_SOUND_DEMO_1 = id("laser/triple_laser_sound_demo_1");
    public static final ResourceLocation LASER_TRIPLE_LASER_SOUND_DEMO_2 = id("laser/triple_laser_sound_demo_2");
    public static final ResourceLocation LASER_TRIPLE_LASER_SOUND_DEMO_3 = id("laser/triple_laser_sound_demo_3");

    public static final ResourceLocation PELLET_BOUNCE = id("pellet/bounce");
    public static final ResourceLocation PELLET_EXPLODE = id("pellet/explode");
    public static final ResourceLocation PELLET_SPAWN = id("pellet/spawn");
    public static final ResourceLocation PELLET_TRAVEL = id("pellet/travel");

    public static final ResourceLocation TBEAM_ENTER = id("tbeam/enter");
    public static final ResourceLocation TBEAM_TRAVEL = id("tbeam/travel");

    public static final ResourceLocation BULLET_CONCRETE = id("bullet/concrete");
    public static final ResourceLocation BULLET_GLASS = id("bullet/glass");
    public static final ResourceLocation BULLET_METAL = id("bullet/metal");

    public static final ResourceLocation RADIO_MUSIC = id("radio");
    public static final ResourceLocation EXILE_SONG = id("exile_vilify");
    public static final ResourceLocation CURIOSITY_CORE_SOUND = id("curiosity_core");
    public static final ResourceLocation ANGER_CORE_SOUND = id("anger_core");
    public static final ResourceLocation CAKE_CORE_SOUND = id("cake_core");

    public static final ResourceLocation SPACE_CORE_SOUND = id("space_core");
    public static final ResourceLocation FACT_CORE_SOUND = id("fact_core");
    public static final ResourceLocation ADVENTURE_CORE_SOUND = id("adventure_core");

    public static final ResourceLocation SEWAGE_STEP = id("sewage_step");

    public static final ResourceLocation CROWBAR_SWOOSH = id("crowbar_swoosh");

    public static final SoundEvent ERROR_EVENT = SoundEvent.createVariableRangeEvent(ERROR);

    public static final SoundEvent GEL_BOUNCE_EVENT = SoundEvent.createVariableRangeEvent(GEL_BOUNCE);
    public static final SoundEvent GEL_RUN_EVENT = SoundEvent.createVariableRangeEvent(GEL_RUN);
    public static final SoundEvent GEL_SPLAT_EVENT = SoundEvent.createVariableRangeEvent(GEL_SPLAT);
    public static final SoundEvent PORTAL_AMBIENT_EVENT = SoundEvent.createVariableRangeEvent(PORTAL_AMBIANCE);
    public static final SoundEvent FIRE_EVENT_PRIMARY = SoundEvent.createVariableRangeEvent(PORTAL_GUN_PRIMARY);
    public static final SoundEvent FIRE_EVENT_SECONDARY = SoundEvent.createVariableRangeEvent(PORTAL_GUN_SECONDARY);
    public static final SoundEvent INVALID_PORTAL_EVENT = SoundEvent.createVariableRangeEvent(PORTAL_INVALID_SURFACE);
    public static final SoundEvent NOTHING_TO_GRAB_EVENT = SoundEvent.createVariableRangeEvent(NOTHING_TO_GRAB);
    public static final SoundEvent ENTITY_ENTER_PORTAL = SoundEvent.createVariableRangeEvent(PORTAL_ENTER);
    public static final SoundEvent ENTITY_EXIT_PORTAL = SoundEvent.createVariableRangeEvent(PORTAL_EXIT);
    public static final SoundEvent ENTITY_PORTAL_OPEN = SoundEvent.createVariableRangeEvent(PORTAL_OPEN);
    public static final SoundEvent ENTITY_PORTAL_CLOSE = SoundEvent.createVariableRangeEvent(PORTAL_CLOSE);
    public static final SoundEvent ENTITY_PORTAL_FIZZLE = SoundEvent.createVariableRangeEvent(PORTAL_FIZZLE);
    public static final SoundEvent CUBE_HIGH_HIT_EVENT = SoundEvent.createVariableRangeEvent(CUBE_HIT_HIGH);
    public static final SoundEvent CUBE_LOW_HIT_EVENT = SoundEvent.createVariableRangeEvent(CUBE_HIT_LOW);
    public static final SoundEvent CUBE_SCRAPE_EVENT = SoundEvent.createVariableRangeEvent(CUBE_SCRAPE);
    public static final SoundEvent COMPANION_CUBE_AMBIANCE_EVENT = SoundEvent.createVariableRangeEvent(COMPANION_CUBE_AMBIANCE);
    public static final SoundEvent MATERIAL_EMANCIPATION_EVENT = SoundEvent.createVariableRangeEvent(MATERIAL_EMANCIPATION);
    public static final SoundEvent CATAPULT_LAUNCH_EVENT = SoundEvent.createVariableRangeEvent(CATAPULT_LAUNCH);

    public static final SoundEvent PEDESTAL_BUTTON_PRESS_EVENT = SoundEvent.createVariableRangeEvent(PEDESTAL_BUTTON_PRESS);
    public static final SoundEvent PEDESTAL_BUTTON_RELEASE_EVENT = SoundEvent.createVariableRangeEvent(PEDESTAL_BUTTON_RELEASE);
    public static final SoundEvent OLD_AP_PEDESTAL_BUTTON_PRESS_EVENT = SoundEvent.createVariableRangeEvent(OLD_AP_PEDESTAL_BUTTON_PRESS);
    public static final SoundEvent OLD_AP_PEDESTAL_BUTTON_RELEASE_EVENT = SoundEvent.createVariableRangeEvent(OLD_AP_PEDESTAL_BUTTON_RELEASE);
    public static final SoundEvent FLOOR_BUTTON_PRESS_EVENT = SoundEvent.createVariableRangeEvent(FLOOR_BUTTON_PRESS);
    public static final SoundEvent FLOOR_BUTTON_RELEASE_EVENT = SoundEvent.createVariableRangeEvent(FLOOR_BUTTON_RELEASE);
    public static final SoundEvent OLD_AP_FLOOR_BUTTON_PRESS_EVENT = SoundEvent.createVariableRangeEvent(OLD_AP_FLOOR_BUTTON_PRESS);
    public static final SoundEvent OLD_AP_FLOOR_BUTTON_RELEASE_EVENT = SoundEvent.createVariableRangeEvent(OLD_AP_FLOOR_BUTTON_RELEASE);

    public static final SoundEvent ROCKET_FIRE_EVENT = SoundEvent.createVariableRangeEvent(ROCKET_FIRE);
    public static final SoundEvent ROCKET_FLY_EVENT = SoundEvent.createVariableRangeEvent(ROCKET_FLY);
    public static final SoundEvent ROCKET_LOCKED_EVENT = SoundEvent.createVariableRangeEvent(ROCKET_LOCKED);
    public static final SoundEvent ROCKET_LOCKING_EVENT = SoundEvent.createVariableRangeEvent(ROCKET_LOCKING);
    public static final SoundEvent ROCKET_EXPLODE_EVENT = SoundEvent.createVariableRangeEvent(ROCKET_EXPLODE);
    public static final SoundEvent ROCKET_GOO_EVENT = SoundEvent.createVariableRangeEvent(ROCKET_GOO);

    public static final SoundEvent LASER_EMITTER_ACTIVATE_EVENT = SoundEvent.createVariableRangeEvent(LASER_EMITTER_ACTIVATE);
    public static final SoundEvent LASER_BEAM_MUSIC_EVENT = SoundEvent.createVariableRangeEvent(LASER_BEAM_MUSIC);
    public static final SoundEvent LASER_NODE_MUSIC_EVENT = SoundEvent.createVariableRangeEvent(LASER_NODE_MUSIC);
    public static final SoundEvent LASER_NODE_ACTIVATE_EVENT = SoundEvent.createVariableRangeEvent(LASER_NODE_ACTIVATE);
    public static final SoundEvent LASER_NODE_DEACTIVATE_EVENT = SoundEvent.createVariableRangeEvent(LASER_NODE_DEACTIVATE);
    public static final SoundEvent LASER_TRIPLE_LASER_SOUND_DEMO_1_EVENT = SoundEvent.createVariableRangeEvent(LASER_TRIPLE_LASER_SOUND_DEMO_1);
    public static final SoundEvent LASER_TRIPLE_LASER_SOUND_DEMO_2_EVENT = SoundEvent.createVariableRangeEvent(LASER_TRIPLE_LASER_SOUND_DEMO_2);
    public static final SoundEvent LASER_TRIPLE_LASER_SOUND_DEMO_3_EVENT = SoundEvent.createVariableRangeEvent(LASER_TRIPLE_LASER_SOUND_DEMO_3);

    public static final SoundEvent PELLET_BOUNCE_EVENT = SoundEvent.createVariableRangeEvent(PELLET_BOUNCE);
    public static final SoundEvent PELLET_EXPLODE_EVENT = SoundEvent.createVariableRangeEvent(PELLET_EXPLODE);
    public static final SoundEvent PELLET_SPAWN_EVENT = SoundEvent.createVariableRangeEvent(PELLET_SPAWN);
    public static final SoundEvent PELLET_TRAVEL_EVENT = SoundEvent.createVariableRangeEvent(PELLET_TRAVEL);

    public static final SoundEvent TBEAM_ENTER_EVENT = SoundEvent.createVariableRangeEvent(TBEAM_ENTER);
    public static final SoundEvent TBEAM_TRAVEL_EVENT = SoundEvent.createVariableRangeEvent(TBEAM_TRAVEL);

    public static final SoundEvent BULLET_CONCRETE_EVENT = SoundEvent.createVariableRangeEvent(BULLET_CONCRETE);
    public static final SoundEvent BULLET_GLASS_EVENT = SoundEvent.createVariableRangeEvent(BULLET_GLASS);
    public static final SoundEvent BULLET_METAL_EVENT = SoundEvent.createVariableRangeEvent(BULLET_METAL);

    public static final SoundEvent RADIO_MUSIC_EVENT = SoundEvent.createVariableRangeEvent(RADIO_MUSIC);
    public static final SoundEvent EXILE_MUSIC_EVENT = SoundEvent.createVariableRangeEvent(EXILE_SONG);
    public static final SoundEvent CURIOSITY_CORE_EVENT = SoundEvent.createVariableRangeEvent(CURIOSITY_CORE_SOUND);
    public static final SoundEvent ANGER_CORE_EVENT = SoundEvent.createVariableRangeEvent(ANGER_CORE_SOUND);
    public static final SoundEvent CAKE_CORE_EVENT = SoundEvent.createVariableRangeEvent(CAKE_CORE_SOUND);
    public static final SoundEvent SPACE_CORE_EVENT = SoundEvent.createVariableRangeEvent(SPACE_CORE_SOUND);
    public static final SoundEvent FACT_CORE_EVENT = SoundEvent.createVariableRangeEvent(FACT_CORE_SOUND);
    public static final SoundEvent ADVENTURE_CORE_EVENT = SoundEvent.createVariableRangeEvent(ADVENTURE_CORE_SOUND);

    public static final SoundEvent SEWAGE_STEP_EVENT = SoundEvent.createVariableRangeEvent(SEWAGE_STEP);

    public static final SoundEvent CROWBAR_SWOOSH_EVENT = SoundEvent.createVariableRangeEvent(CROWBAR_SWOOSH);

    public static final TagKey<SoundEvent> NO_ERROR_SOUND = QuiltTagKey.of(Registries.SOUND_EVENT, id("no_error_sound"), TagType.CLIENT_ONLY);

    public static void registerSounds() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, ERROR, ERROR_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, GEL_BOUNCE, GEL_BOUNCE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, GEL_RUN, GEL_RUN_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, GEL_SPLAT, GEL_SPLAT_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_AMBIANCE, PORTAL_AMBIENT_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_GUN_PRIMARY, FIRE_EVENT_PRIMARY);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_GUN_SECONDARY, FIRE_EVENT_SECONDARY);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_INVALID_SURFACE, INVALID_PORTAL_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, NOTHING_TO_GRAB, NOTHING_TO_GRAB_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_ENTER, ENTITY_ENTER_PORTAL);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_EXIT, ENTITY_EXIT_PORTAL);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_OPEN, ENTITY_PORTAL_OPEN);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_CLOSE, ENTITY_PORTAL_CLOSE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PORTAL_FIZZLE, ENTITY_PORTAL_FIZZLE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CUBE_HIT_HIGH, CUBE_HIGH_HIT_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CUBE_HIT_LOW, CUBE_LOW_HIT_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CUBE_SCRAPE, CUBE_SCRAPE_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, COMPANION_CUBE_AMBIANCE, COMPANION_CUBE_AMBIANCE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, MATERIAL_EMANCIPATION, MATERIAL_EMANCIPATION_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CATAPULT_LAUNCH, CATAPULT_LAUNCH_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, PEDESTAL_BUTTON_PRESS, PEDESTAL_BUTTON_PRESS_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PEDESTAL_BUTTON_RELEASE, PEDESTAL_BUTTON_RELEASE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OLD_AP_PEDESTAL_BUTTON_PRESS, OLD_AP_PEDESTAL_BUTTON_PRESS_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OLD_AP_PEDESTAL_BUTTON_RELEASE, OLD_AP_PEDESTAL_BUTTON_RELEASE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, FLOOR_BUTTON_PRESS, FLOOR_BUTTON_PRESS_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, FLOOR_BUTTON_RELEASE, FLOOR_BUTTON_RELEASE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OLD_AP_FLOOR_BUTTON_PRESS, OLD_AP_FLOOR_BUTTON_PRESS_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OLD_AP_FLOOR_BUTTON_RELEASE, OLD_AP_FLOOR_BUTTON_RELEASE_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, ROCKET_FIRE, ROCKET_FIRE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ROCKET_FLY, ROCKET_FLY_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ROCKET_LOCKED, ROCKET_LOCKED_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ROCKET_LOCKING, ROCKET_LOCKING_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ROCKET_EXPLODE, ROCKET_EXPLODE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ROCKET_GOO, ROCKET_GOO_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, LASER_EMITTER_ACTIVATE, LASER_EMITTER_ACTIVATE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, LASER_BEAM_MUSIC, LASER_BEAM_MUSIC_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, LASER_NODE_MUSIC, LASER_NODE_MUSIC_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, LASER_NODE_ACTIVATE, LASER_NODE_ACTIVATE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, LASER_NODE_DEACTIVATE, LASER_NODE_DEACTIVATE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, LASER_TRIPLE_LASER_SOUND_DEMO_1, LASER_TRIPLE_LASER_SOUND_DEMO_1_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, LASER_TRIPLE_LASER_SOUND_DEMO_2, LASER_TRIPLE_LASER_SOUND_DEMO_2_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, LASER_TRIPLE_LASER_SOUND_DEMO_3, LASER_TRIPLE_LASER_SOUND_DEMO_3_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, PELLET_BOUNCE, PELLET_BOUNCE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PELLET_EXPLODE, PELLET_EXPLODE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PELLET_SPAWN, PELLET_SPAWN_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PELLET_TRAVEL, PELLET_TRAVEL_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, TBEAM_ENTER, TBEAM_ENTER_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, TBEAM_TRAVEL, TBEAM_TRAVEL_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, BULLET_CONCRETE, BULLET_CONCRETE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, BULLET_GLASS, BULLET_GLASS_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, BULLET_METAL, BULLET_METAL_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, RADIO_MUSIC, RADIO_MUSIC_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, EXILE_SONG, EXILE_MUSIC_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CURIOSITY_CORE_SOUND, CURIOSITY_CORE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ANGER_CORE_SOUND, ANGER_CORE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAKE_CORE_SOUND, CAKE_CORE_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, SPACE_CORE_SOUND, SPACE_CORE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, FACT_CORE_SOUND, FACT_CORE_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ADVENTURE_CORE_SOUND, ADVENTURE_CORE_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, SEWAGE_STEP, SEWAGE_STEP_EVENT);

        Registry.register(BuiltInRegistries.SOUND_EVENT, CROWBAR_SWOOSH, CROWBAR_SWOOSH_EVENT);
    }
}
