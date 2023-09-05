package com.fusionflux.portalcubed.compat.ponder;

import net.createmod.ponder.foundation.SceneBuilder;
import net.createmod.ponder.foundation.SceneBuildingUtil;

import net.minecraft.world.entity.EntityType;

public class TestScene {
	public static void test(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("test", "Test Scene");
		scene.world.createEntity(EntityType.PARROT::create);
		scene.debug.debugSchematic();
	}
}
