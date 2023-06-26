package com.fusionflux.portalcubed.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class ExcursionFunnelEnterSoundInstance extends AbstractTickableSoundInstance {
    private int ticks = 0;

    public ExcursionFunnelEnterSoundInstance() {
        super(PortalCubedSounds.TBEAM_ENTER_EVENT, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.attenuation = Attenuation.NONE;
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks > 80) {
            volume = 1f - 0.05f * (ticks - 80);
            if (volume <= 0) {
                stop();
            }
        }
    }
}
