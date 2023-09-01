package com.fusionflux.portalcubed.compat;

import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import net.minecraft.util.Mth;

public class LambDynamicLightsIntegration implements DynamicLightsInitializer {
	public static final int CUBE_LIGHT = 1;
	public static final int CORE_LIGHT = CUBE_LIGHT;

	@Override
	public void onInitializeDynamicLights() {
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.STORAGE_CUBE, e -> CUBE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.COMPANION_CUBE, e -> CUBE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.PORTAL_1_STORAGE_CUBE, e -> CUBE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.PORTAL_1_COMPANION_CUBE, e -> CUBE_LIGHT);

		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.ANGER_CORE, e -> CORE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.MORALITY_CORE, e -> CORE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.CAKE_CORE, e -> CORE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.CURIOSITY_CORE, e -> CORE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.SPACE_CORE, e -> CORE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.FACT_CORE, e -> CORE_LIGHT);
		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.ADVENTURE_CORE, e -> CORE_LIGHT);

		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.ENERGY_PELLET, e ->
			Math.round((e.getStartingLife() > 0 ? Mth.clamp(Mth.lerp((float)e.getLife() / e.getStartingLife(), 0.25f, 1f), 0f, 1f) : 1f) * 15)
		);

		DynamicLightHandlers.registerDynamicLightHandler(PortalCubedEntities.PORTAL, e -> 2);
	}
}
