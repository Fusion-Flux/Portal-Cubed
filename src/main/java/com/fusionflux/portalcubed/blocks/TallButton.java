package com.fusionflux.portalcubed.blocks;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class TallButton extends TallButtonVariant {
    public TallButton(Settings settings) {
        super(false, settings);
    }

    @Override
    protected SoundEvent getClickSound(boolean powered) {
        return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
    }
}
