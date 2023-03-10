package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.sound.SoundEvent;

public class TallButton extends TallButtonVariant {
    public TallButton(Settings settings) {
        super(settings);
    }

    @Override
    public SoundEvent getClickSound(boolean powered) {
        return powered ? PortalCubedSounds.PEDESTAL_BUTTON_PRESS_EVENT : PortalCubedSounds.PEDESTAL_BUTTON_RELEASE_EVENT;
    }
}
