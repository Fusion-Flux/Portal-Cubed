package com.fusionflux.thinkingwithportatos.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ThinkingWithPortatosSounds {
    public static final Identifier GEL_BOUNCE = new Identifier("thinkingwithportatos:gelbounce");
    public static final Identifier GEL_RUN = new Identifier("thinkingwithportatos:gelrun");
    public static final Identifier PORTAL_GUN_PRIMARY = new Identifier("thinkingwithportatos:portalgunprimary");
    public static final Identifier PORTAL_GUN_SECONDARY = new Identifier("thinkingwithportatos:portalgunsecondary");
    public static SoundEvent GEL_BOUNCE_EVENT = new SoundEvent(GEL_BOUNCE);
    public static SoundEvent GEL_RUN_EVENT = new SoundEvent(GEL_RUN);
    public static SoundEvent FIRE_EVENT_PRIMARY = new SoundEvent(PORTAL_GUN_PRIMARY);
    public static SoundEvent FIRE_EVENT_SECONDARY = new SoundEvent(PORTAL_GUN_SECONDARY);

    public static void registerSounds() {
        Registry.register(Registry.SOUND_EVENT, GEL_BOUNCE, GEL_BOUNCE_EVENT);
        Registry.register(Registry.SOUND_EVENT, GEL_RUN, GEL_RUN_EVENT);
        Registry.register(Registry.SOUND_EVENT, PORTAL_GUN_PRIMARY, FIRE_EVENT_PRIMARY);
        Registry.register(Registry.SOUND_EVENT, PORTAL_GUN_SECONDARY, FIRE_EVENT_SECONDARY);
    }
}
