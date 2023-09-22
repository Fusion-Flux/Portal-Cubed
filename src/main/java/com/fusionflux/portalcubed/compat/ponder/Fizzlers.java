package com.fusionflux.portalcubed.compat.ponder;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.fizzler.FizzlerBlock;
import com.fusionflux.portalcubed.blocks.fizzler.FizzlerEmitter;
import com.fusionflux.portalcubed.entity.Fizzleable;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import net.createmod.catnip.utility.Pointing;
import net.createmod.ponder.foundation.ElementLink;
import net.createmod.ponder.foundation.SceneBuilder;
import net.createmod.ponder.foundation.SceneBuildingUtil;
import net.createmod.ponder.foundation.Selection;
import net.createmod.ponder.foundation.element.EntityElement;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;


public class Fizzlers {
	public static void fizzlers(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("fizzlers", "Using Fizzlers");
		scene.configureBasePlate(0, 0, 5);
		Selection leftFizzler = util.select.fromTo(4, 1, 2, 4, 2, 2);
		BlockPos leftFizzlerClickPos = util.grid.at(4, 2, 2);
		Selection rightFizzler = util.select.fromTo(0, 1, 2, 0, 2, 2);
		BlockPos rightFizzlerClickPos = util.grid.at(0, 2, 2);
		Selection field = util.select.fromTo(3, 2, 2, 1, 1, 2);
		Selection leftRedstone = util.select.fromTo(4, 1, 0, 3, 1, 1);
		Selection rightRedstone = util.select.fromTo(1, 1, 0, 0, 1, 1);
		BlockPos lever = util.grid.at(2, 1, 0);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, false), false);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		ItemStack fizzlerEmitterItem = new ItemStack(PortalCubedBlocks.FIZZLER_EMITTER);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(100)
				.text("Fizzlers allow you to prevent players from taking chamber props into certain areas, and to clear the player's portals.");
		scene.idle(120);

		// It's fizzlin time
		scene.overlay.showText(80)
				.text("To create a Fizzler field, place 2 Fizzler Emitters facing each other and power both.");
		scene.idle(20);
		scene.world.showSection(leftFizzler, Direction.DOWN);
		InputWindowElement leftFizzlerClick = new InputWindowElement(leftFizzlerClickPos.getCenter(), Pointing.RIGHT).rightClick()
				.withItem(fizzlerEmitterItem);
		scene.overlay.showControls(leftFizzlerClick, 30);
		scene.idle(20);
		scene.world.showSection(rightFizzler, Direction.DOWN);
		InputWindowElement rightFizzlerClick = new InputWindowElement(rightFizzlerClickPos.getCenter(), Pointing.RIGHT).rightClick()
				.withItem(fizzlerEmitterItem);
		scene.overlay.showControls(rightFizzlerClick, 30);
		scene.idle(50);
		scene.world.modifyBlock(lever, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlocks(leftRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(rightRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(leftFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(rightFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, true), false);
		scene.idle(40);

		// Fizzler Effects
		scene.addKeyframe();
		scene.idle(20);
		scene.overlay.showText(100)
				.text("Players who walk through them will have their portals removed, and props that pass through them will be disintegrated.");
		ElementLink<EntityElement> cube = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2.5, 1, 0.5);
			return cubeEntity;
		});
		scene.idle(40);
		scene.world.modifyEntity(cube, e -> e.lerpTo(2.5, 1, 4.5, 0, 0, 40, false));
		scene.idle(15);
		scene.world.modifyEntity(cube, entity -> ((Fizzleable) entity).startFizzlingProgress());
		scene.idle(30);
		scene.world.modifyEntity(cube, Entity::discard);

	}

	public static void laser_fields(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("laser_fields", "Using Laser Fields");
		scene.configureBasePlate(0, 0, 5);
		Selection leftFizzler = util.select.fromTo(4, 1, 2, 4, 2, 2);
		Selection rightFizzler = util.select.fromTo(0, 1, 2, 0, 2, 2);
		Selection field = util.select.fromTo(3, 2, 2, 1, 1, 2);
		Selection leftRedstone = util.select.fromTo(4, 1, 0, 3, 1, 1);
		Selection rightRedstone = util.select.fromTo(1, 1, 0, 0, 1, 1);
		BlockPos lever = util.grid.at(2, 1, 0);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, false), false);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(100)
				.text("Laser Fields kill mobs that pass through them, but do not fizzle props or clear portals.");
		scene.idle(80);

		scene.world.modifyBlock(lever, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlocks(leftRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(rightRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(leftFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(rightFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, true), false);
		scene.idle(40);

		ElementLink<EntityElement> cube = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2, 1, 0.5);
			return cubeEntity;
		});
		ElementLink<EntityElement> sheep = scene.world.createEntity(w -> {
			Sheep sheepEntity = EntityType.SHEEP.create(w);
			assert sheepEntity != null;
			sheepEntity.setPos(3, 1, 0.5);
			return sheepEntity;
		});
		scene.idle(40);
		scene.world.modifyEntity(cube, e -> e.lerpTo(2, 1, 4.5, 0, 0, 40, false));
		scene.world.modifyEntity(sheep, e -> e.lerpTo(3, 1, 4.5, 0, 0, 40, false));
		scene.idle(15);
		scene.world.modifyEntity(sheep, Entity::kill); //This shit doesn't work
		scene.world.modifyEntity(sheep, Entity::discard);
		scene.idle(30);
	}

	public static void death_fizzlers(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("death_fizzlers", "Using Death Fizzlers");
		scene.configureBasePlate(0, 0, 5);
		Selection leftFizzler = util.select.fromTo(4, 1, 2, 4, 2, 2);
		Selection rightFizzler = util.select.fromTo(0, 1, 2, 0, 2, 2);
		Selection field = util.select.fromTo(3, 2, 2, 1, 1, 2);
		Selection leftRedstone = util.select.fromTo(4, 1, 0, 3, 1, 1);
		Selection rightRedstone = util.select.fromTo(1, 1, 0, 0, 1, 1);
		BlockPos lever = util.grid.at(2, 1, 0);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, false), false);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(80)
				.text("Death Fizzlers combine the properties of Fizzlers and Laser Fields.");
		scene.idle(60);

		scene.world.modifyBlock(lever, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlocks(leftRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(rightRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(leftFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(rightFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, true), false);
		scene.idle(40);

		scene.overlay.showText(80)
				.text("Entities that pass through them are killed, and props that pass through them get disintegrated.");

		ElementLink<EntityElement> cube = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2, 1, 0.5);
			return cubeEntity;
		});
		ElementLink<EntityElement> sheep = scene.world.createEntity(w -> {
			Sheep sheepEntity = EntityType.SHEEP.create(w);
			assert sheepEntity != null;
			sheepEntity.setPos(3, 1, 0.5);
			return sheepEntity;
		});
		scene.idle(40);
		scene.world.modifyEntity(cube, e -> e.lerpTo(2, 1, 4.5, 0, 0, 40, false));
		scene.world.modifyEntity(sheep, e -> e.lerpTo(3, 1, 4.5, 0, 0, 40, false));
		scene.idle(15);
		scene.world.modifyEntity(cube, entity -> ((Fizzleable) entity).startFizzlingProgress());
		scene.world.modifyEntity(sheep, Entity::kill); //This shit doesn't work
		scene.world.modifyEntity(sheep, Entity::discard);
		scene.idle(30);
		scene.world.modifyEntity(cube, Entity::discard);
		scene.idle(20);

	}
	public static void matter_inquisition_fields(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("matter_inquisition_fields", "Matter Inquisition Fields");
		scene.configureBasePlate(0, 0, 5);
		Selection leftFizzler = util.select.fromTo(4, 1, 2, 4, 2, 2);
		Selection rightFizzler = util.select.fromTo(0, 1, 2, 0, 2, 2);
		Selection field = util.select.fromTo(3, 2, 2, 1, 1, 2);
		Selection leftRedstone = util.select.fromTo(4, 1, 0, 3, 1, 1);
		Selection rightRedstone = util.select.fromTo(1, 1, 0, 0, 1, 1);
		BlockPos lever = util.grid.at(2, 1, 0);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, false), false);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(120)
				.text("Matter Inquisition Fields fizzle props, but do not affect other entities or portals in any way.");
		scene.idle(100);

		scene.world.modifyBlock(lever, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlocks(leftRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(rightRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(leftFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(rightFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, true), false);
		scene.idle(40);


		ElementLink<EntityElement> cube = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2, 1, 0.5);
			return cubeEntity;
		});
		ElementLink<EntityElement> sheep = scene.world.createEntity(w -> {
			Sheep sheepEntity = EntityType.SHEEP.create(w);
			assert sheepEntity != null;
			sheepEntity.setPos(3, 1, 0.5);
			return sheepEntity;
		});
		scene.idle(40);
		scene.world.modifyEntity(cube, e -> e.lerpTo(2, 1, 4.5, 0, 0, 40, false));
		scene.world.modifyEntity(sheep, e -> e.lerpTo(3, 1, 4.5, 0, 0, 40, false));
		scene.idle(15);
		scene.world.modifyEntity(cube, entity -> ((Fizzleable) entity).startFizzlingProgress());
		scene.idle(30);
		scene.world.modifyEntity(cube, Entity::discard);
	}

	public static void physics_repulsion_fields(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("physics_repulsion_fields", "Physics Repulsion Fields");
		scene.configureBasePlate(0, 0, 5);
		Selection leftFizzler = util.select.fromTo(4, 1, 2, 4, 2, 2);
		Selection rightFizzler = util.select.fromTo(0, 1, 2, 0, 2, 2);
		Selection field = util.select.fromTo(3, 2, 2, 1, 1, 2);
		Selection leftRedstone = util.select.fromTo(4, 1, 0, 3, 1, 1);
		Selection rightRedstone = util.select.fromTo(1, 1, 0, 0, 1, 1);
		BlockPos lever = util.grid.at(2, 1, 0);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, false), false);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(120)
				.text("Physics Repulsion Fields act as solid surfaces for props, but allow entities to pass through unaffected.");
		scene.idle(100);

		scene.world.modifyBlock(lever, s -> s.setValue(LeverBlock.POWERED, true), false);
		scene.world.modifyBlocks(leftRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(rightRedstone, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(leftFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(rightFizzler, s -> s.setValue(FizzlerEmitter.POWERED, true), false);
		scene.world.modifyBlocks(field, s -> s.setValue(FizzlerBlock.EW, true), false);
		scene.idle(40);


		ElementLink<EntityElement> cube = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2, 1, 0.5);
			return cubeEntity;
		});
		ElementLink<EntityElement> sheep = scene.world.createEntity(w -> {
			Sheep sheepEntity = EntityType.SHEEP.create(w);
			assert sheepEntity != null;
			sheepEntity.setPos(3, 1, 0.5);
			return sheepEntity;
		});
		scene.idle(40);
		scene.world.modifyEntity(cube, e -> e.lerpTo(2, 1, 2.2, 0, 0, 20, false));
		scene.world.modifyEntity(sheep, e -> e.lerpTo(3, 1, 4.5, 0, 0, 40, false));
		scene.idle(15);
		scene.idle(30);
	}

}
