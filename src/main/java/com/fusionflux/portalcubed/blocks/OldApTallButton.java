package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.sounds.SoundEvent;

public class OldApTallButton extends TallButtonVariant {
	public OldApTallButton(Properties settings) {
		super(settings);
	}

	@Override
	public SoundEvent getClickSound(boolean powered) {
		return powered ? PortalCubedSounds.OLD_AP_PEDESTAL_BUTTON_PRESS_EVENT : PortalCubedSounds.OLD_AP_PEDESTAL_BUTTON_RELEASE_EVENT;
	}
}
