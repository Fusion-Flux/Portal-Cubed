package com.fusionflux.fluxtech.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluxTechSounds {
    public static final Identifier GEL_BOUNCE = new Identifier("fluxtech:gelbounce");
    public static final Identifier GEL_RUN = new Identifier("fluxtech:gelrun");
    public static final Identifier HPD_BLAST = new Identifier("fluxtech:hpdblast");
    public static SoundEvent GEL_BOUNCE_EVENT = new SoundEvent(GEL_BOUNCE);
    public static SoundEvent GEL_RUN_EVENT = new SoundEvent(GEL_RUN);
    public static SoundEvent BLAST_EVENT = new SoundEvent(HPD_BLAST);


    public static void registerSounds() {
        Registry.register(Registry.SOUND_EVENT, GEL_BOUNCE, GEL_BOUNCE_EVENT);
        Registry.register(Registry.SOUND_EVENT, GEL_RUN, GEL_RUN_EVENT);
        Registry.register(Registry.SOUND_EVENT, HPD_BLAST, BLAST_EVENT);
    }
}
