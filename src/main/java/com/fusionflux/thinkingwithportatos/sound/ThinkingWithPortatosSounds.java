package com.fusionflux.thinkingwithportatos.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ThinkingWithPortatosSounds {
    public static final Identifier GEL_BOUNCE = new Identifier("thinkingwithportatos:gelbounce");
    public static final Identifier GEL_RUN = new Identifier("thinkingwithportatos:gelrun");
    public static final Identifier PORTAL_GUN_PRIMARY = new Identifier("thinkingwithportatos:portalgunprimary");
    public static final Identifier PORTAL_GUN_SECONDARY = new Identifier("thinkingwithportatos:portalgunsecondary");
    public static final Identifier PORTAL_INVALID_SURFACE = new Identifier("thinkingwithportatos:portal_invalid_surface");
    public static final Identifier PORTAL_AMBIANCE = new Identifier("thinkingwithportatos:portalambiance");
    public static final Identifier PORTAL_ENTER = new Identifier("thinkingwithportatos:portalenter");
    public static final Identifier PORTAL_EXIT = new Identifier("thinkingwithportatos:portalexit");
    public static final Identifier PORTAL_OPEN = new Identifier("thinkingwithportatos:portal_open");
    public static final Identifier PORTAL_CLOSE = new Identifier("thinkingwithportatos:portal_close");
    public static final Identifier CUBE_HIT = new Identifier("thinkingwithportatos:cubehit");
    public static final Identifier COMPANION_CUBE_AMBIANCE = new Identifier("thinkingwithportatos:companioncubeambiance");
    public static final SoundEvent GEL_BOUNCE_EVENT = new SoundEvent(GEL_BOUNCE);
    public static final SoundEvent GEL_RUN_EVENT = new SoundEvent(GEL_RUN);
    public static final SoundEvent PORTAL_AMBIANT_EVENT = new SoundEvent(PORTAL_AMBIANCE);
    public static final SoundEvent FIRE_EVENT_PRIMARY = new SoundEvent(PORTAL_GUN_PRIMARY);
    public static final SoundEvent FIRE_EVENT_SECONDARY = new SoundEvent(PORTAL_GUN_SECONDARY);
    public static final SoundEvent INVALID_PORTAL_EVENT = new SoundEvent(PORTAL_INVALID_SURFACE);
    public static final SoundEvent ENTITY_ENTER_PORTAL = new SoundEvent(PORTAL_ENTER);
    public static final SoundEvent ENTITY_EXIT_PORTAL = new SoundEvent(PORTAL_EXIT);
    public static final SoundEvent ENTITY_PORTAL_OPEN = new SoundEvent(PORTAL_OPEN);
    public static final SoundEvent ENTITY_PORTAL_CLOSE = new SoundEvent(PORTAL_CLOSE);
    public static final SoundEvent CUBE_HIT_EVENT = new SoundEvent(CUBE_HIT);
    public static final SoundEvent COMPANION_CUBE_AMBIANCE_EVENT = new SoundEvent(COMPANION_CUBE_AMBIANCE);
    public static void registerSounds() {
        Registry.register(Registry.SOUND_EVENT, GEL_BOUNCE, GEL_BOUNCE_EVENT);
        Registry.register(Registry.SOUND_EVENT, GEL_RUN, GEL_RUN_EVENT);
        Registry.register(Registry.SOUND_EVENT, PORTAL_AMBIANCE, PORTAL_AMBIANT_EVENT);
        Registry.register(Registry.SOUND_EVENT, PORTAL_GUN_PRIMARY, FIRE_EVENT_PRIMARY);
        Registry.register(Registry.SOUND_EVENT, PORTAL_GUN_SECONDARY, FIRE_EVENT_SECONDARY);
        Registry.register(Registry.SOUND_EVENT, PORTAL_INVALID_SURFACE, INVALID_PORTAL_EVENT);
        Registry.register(Registry.SOUND_EVENT, PORTAL_ENTER, ENTITY_ENTER_PORTAL);
        Registry.register(Registry.SOUND_EVENT, PORTAL_EXIT, ENTITY_EXIT_PORTAL);
        Registry.register(Registry.SOUND_EVENT, PORTAL_OPEN, ENTITY_PORTAL_OPEN);
        Registry.register(Registry.SOUND_EVENT, PORTAL_CLOSE, ENTITY_PORTAL_CLOSE);
        Registry.register(Registry.SOUND_EVENT, CUBE_HIT, CUBE_HIT_EVENT);
        Registry.register(Registry.SOUND_EVENT, COMPANION_CUBE_AMBIANCE, COMPANION_CUBE_AMBIANCE_EVENT);
    }
}
