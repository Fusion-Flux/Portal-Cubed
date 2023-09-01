package com.fusionflux.portalcubed.compat.pehkui.absent;

import com.fusionflux.portalcubed.compat.pehkui.PehkuiApi;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleModifier;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public enum PehkuiApiAbsent implements PehkuiApi {
	INSTANCE;

	@Override
	public PehkuiScaleType getScaleType(ResourceLocation id) {
		return PehkuiScaleTypeAbsent.INSTANCE;
	}

	@Override
	public PehkuiScaleModifier getScaleModifier(ResourceLocation id) {
		return PehkuiScaleModifierAbsent.INSTANCE;
	}

	@Override
	public PehkuiScaleType registerScaleType(ResourceLocation id, PehkuiScaleModifier valueModifier) {
		return PehkuiScaleTypeAbsent.INSTANCE;
	}

	@Override
	public float getFallingScale(Entity entity) {
		return 1f;
	}
}
