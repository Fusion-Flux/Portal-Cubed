package com.fusionflux.portalcubed.compat.pehkui;

import com.fusionflux.portalcubed.compat.pehkui.absent.PehkuiApiAbsent;
import com.fusionflux.portalcubed.compat.pehkui.present.PehkuiApiPresent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.quiltmc.loader.api.QuiltLoader;

public interface PehkuiApi {
	PehkuiApi INSTANCE = QuiltLoader.isModLoaded("pehkui") ? PehkuiPresentHolder.getPresent() : PehkuiApiAbsent.INSTANCE;

	PehkuiScaleType getScaleType(ResourceLocation id);

	PehkuiScaleModifier getScaleModifier(ResourceLocation id);

	PehkuiScaleType registerScaleType(ResourceLocation id, PehkuiScaleModifier valueModifier);

	float getFallingScale(Entity entity);

	static ResourceLocation id(String path) {
		return new ResourceLocation("pehkui", path);
	}

	class PehkuiPresentHolder {
		private static PehkuiApi getPresent() {
			return new PehkuiApiPresent();
		}
	}
}
