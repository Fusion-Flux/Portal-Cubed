package com.fusionflux.portalcubed.compat.ponder;

import com.fusionflux.portalcubed.blocks.LaserCatcherBlock;
import com.fusionflux.portalcubed.blocks.LaserEmitterBlock;
import com.fusionflux.portalcubed.blocks.LaserRelayBlock;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import net.createmod.catnip.utility.Pointing;
import net.createmod.ponder.foundation.ElementLink;
import net.createmod.ponder.foundation.SceneBuilder;
import net.createmod.ponder.foundation.SceneBuildingUtil;
import net.createmod.ponder.foundation.element.EntityElement;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;


public class Lasers {
	public static void laser_intro(SceneBuilder scene, SceneBuildingUtil util) {

		//Setup
		scene.title("laser_intro", "Using Lasers");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		BlockPos emitterPos = util.grid.at(4, 1, 1);
		BlockPos catcherPos = util.grid.at(1, 1, 4);
		BlockPos leverPos = util.grid.at(4, 1, 0);
		scene.idle(60);

		//Intro Text + turn on the laser
		scene.world.modifyBlock(leverPos, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlock(emitterPos, s -> s.setValue(LaserEmitterBlock.POWERED, true), false);
		scene.overlay.showText(60)
				.text("Thermal Discouragement Beam Emitters output a laser when powered.");
		scene.idle(80);
		scene.overlay.showText(80)
				.text("The beam damages entities that make contact with it and briefly sets them on fire.");
		scene.idle(100);
		scene.addKeyframe();

		//Spawn the redirection cube
		scene.overlay.showText(80)
				.text("There are a few ways the beam can be redirected, including usage of the Redirection Cube.");
		scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.REDIRECTION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(1.5, 1, 1.5);
			cubeEntity.setYRot(0f);
			return cubeEntity;
		});
		scene.world.modifyBlock(catcherPos, s -> s.setValue(LaserCatcherBlock.ENABLED, true), false);
		scene.idle(100);
		scene.overlay.showText(100)
				.text("The catcher lights up and spins when a laser is directed into it, while also outputting redstone power.");
		scene.idle(100);

	}

	public static void laser_relays(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("laser_relays", "Laser Relays");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		scene.idle(20);
		scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.REDIRECTION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(1.5, 1, 1.5);
			cubeEntity.setYRot(0f);
			return cubeEntity;
		});
		BlockPos emitterPos = util.grid.at(4, 1, 1);
		BlockPos leverPos = util.grid.at(4, 1, 0);
		BlockPos relay1Pos = util.grid.at(2, 1, 1);
		BlockPos relay2Pos = util.grid.at(1, 1, 3);
		BlockPos lampPos = util.grid.at(2, 1, 3);
		scene.idle(20);

		//I'm firin' my laser!
		scene.world.modifyBlock(leverPos, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlock(emitterPos, s -> s.setValue(LaserEmitterBlock.POWERED, true), false);
		scene.world.modifyBlock(relay1Pos, s -> s.setValue(LaserRelayBlock.ENABLED, true), false);
		scene.world.modifyBlock(relay2Pos, s -> s.setValue(LaserRelayBlock.ENABLED, true), false);
		scene.world.modifyBlock(lampPos, s -> s.setValue(RedstoneLampBlock.LIT, true), false);
		scene.overlay.showText(120)
				.text("Laser Relays can be attached to any surface and power the surrounding blocks when a laser passes through them.");
		scene.idle(120);
	}

	public static void laser_blocking(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("laser_blocking", "Laser Blocking");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		BlockPos emitter1Pos = util.grid.at(0, 1, 1);
		BlockPos emitter2Pos = util.grid.at(0, 1, 3);
		BlockPos leverPos = util.grid.at(0, 2, 2);
		BlockPos catcher1Pos = util.grid.at(4, 1, 1);
		scene.idle(20);

		//Pull the lever Kronk
		scene.overlay.showText(140)
				.text("Lasers can pass through most blocks that the player's camera can.  This includes things like glass and foliage.");
		scene.world.modifyBlock(leverPos, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlock(emitter1Pos, s -> s.setValue(LaserEmitterBlock.POWERED, true), false);
		scene.world.modifyBlock(emitter2Pos, s -> s.setValue(LaserEmitterBlock.POWERED, true), false);
		scene.world.modifyBlock(catcher1Pos, s -> s.setValue(LaserRelayBlock.ENABLED, true), false);
		scene.idle(160);
		scene.overlay.showText(100)
				.text("Most normal blocks simply stop the laser, however some reflect it instead.");
		scene.idle(100);
	}

	public static void reflective_surfaces(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("reflective_surfaces", "Reflective Surfaces");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		BlockPos emitterPos = util.grid.at(4, 1, 1);
		BlockPos leverPos = util.grid.at(4, 1, 0);
		scene.idle(20);
		scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.REDIRECTION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2.5, 1, 1.5);
			cubeEntity.yRotO = 210;
			cubeEntity.setYRot(210);
			return cubeEntity;
		});
		scene.idle(20);
		//Text
		scene.overlay.showText(60)
				.text("Some blocks reflect lasers instead of stopping them.");
		scene.world.modifyBlock(leverPos, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlock(emitterPos, s -> s.setValue(LaserEmitterBlock.POWERED, true), false);
		scene.idle(80);
		scene.overlay.showText(60)
				.text("For example, when a laser hits reflection gel it bounces off.");
		scene.idle(80);
		scene.overlay.showText(100)
				.text("Some vanilla blocks also have this property, including amethyst blocks, tinted glass, and most metal and gem blocks.");
		scene.idle(100);
	}

	public static void schrodinger_cubes(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("schrodinger_cubes", "Schrödinger Cubes");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		BlockPos cubeOne = util.grid.at(1, 1, 1);
		BlockPos cubeTwo = util.grid.at(3, 1, 3);
		scene.idle(20);
		ElementLink<EntityElement> cube1 = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.SCHRODINGER_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(1.5, 1, 1.5);
			cubeEntity.yRotO = 80;
			cubeEntity.setYRot(80);
			return cubeEntity;
		});
		ElementLink<EntityElement> cube2 = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.SCHRODINGER_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(3.5, 1, 3.5);
			cubeEntity.yRotO = 180;
			cubeEntity.setYRot(180);
			return cubeEntity;
		});
		scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.SCHRODINGER_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(3.5, 1, 2.5);
			cubeEntity.yRotO = 180;
			cubeEntity.setYRot(180);
			return cubeEntity;
		});

		//Text
		scene.idle(20);
		scene.overlay.showText(60)
				.text("Schrödinger Cubes are a special kind of Redirection Cube.");
		scene.idle(80);
		scene.overlay.showText(80)
				.text("When a laser passes into a Schrödinger Cube, it comes out of the nearest other Schrodinger Cube.");
		scene.idle(100);
		scene.addKeyframe();

		//Name tag linking
		scene.idle(20);
		InputWindowElement cubeOneTag = new InputWindowElement(cubeOne.getCenter(), Pointing.DOWN).rightClick()
				.withItem(new ItemStack(Items.NAME_TAG));
		InputWindowElement cubeTwoTag = new InputWindowElement(cubeTwo.getCenter(), Pointing.DOWN).rightClick()
				.withItem(new ItemStack(Items.NAME_TAG));
		scene.world.modifyEntity(cube1, e -> e.setCustomName(Component.literal("Gerald")));
		scene.world.modifyEntity(cube2, e -> e.setCustomName(Component.literal("Gerald")));  //no idea if this works or how to test if it works until lasers get fixed in ponders
		scene.overlay.showControls(cubeOneTag, 60);
		scene.overlay.showControls(cubeTwoTag, 60);
		scene.overlay.showText(80)
				.text("To link 2 or more Schrödinger Cubes regardless of proximity and other cubes, apply a name tag to the cubes you want to link.");
		scene.idle(100);

	}
}
