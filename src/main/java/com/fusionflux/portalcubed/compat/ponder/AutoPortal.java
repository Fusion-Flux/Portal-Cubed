package com.fusionflux.portalcubed.compat.ponder;
import com.fusionflux.portalcubed.blocks.TallButtonVariant;
import com.fusionflux.portalcubed.entity.Portal;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.block.RedStoneWireBlock;
import static com.fusionflux.portalcubed.items.PortalCubedItems.HAMMER;

// Behold, probably the jankiest ponder known to man, written by someone who has next to no idea
// how ponder works and is cobbling this together with glitter glue and broken dreams
// works™️

public class AutoPortal {
	public static void autoportal_intro(SceneBuilder scene, SceneBuildingUtil util) {

		//Setup
		scene.title("autoportal_intro", "Using the Autoportal");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		BlockPos blueButtonPos = util.grid.at(2, 1, 3);
		BlockPos orangeButtonPos = util.grid.at(3, 1, 2);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro
		scene.overlay.showText(60)
				.text("Autoportals create portals that are not associated with a player.");
		scene.idle(80);
		scene.overlay.showText(60)
				.text("By default, an autoportal will create a primary portal.");
		scene.idle(80);

		//Press Button + Open the first portal
		scene.addKeyframe();
		scene.overlay.showText(60)
				.text("Power an autoportal to open or close its portal.");
		scene.idle(30);
		scene.world.modifyBlock(blueButtonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(1.5, 2, 3.99);
			portalEntity.setColor(1935067);
			return portalEntity;
		});
		scene.idle(30);
		scene.world.modifyBlock(blueButtonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		scene.idle(40);

		//Change the other to secondary mode and spawn the orange portal
		scene.addKeyframe();
		scene.overlay.showText(80)
				.text("Interact with a Hammer to switch the autoportal to creating secondary portals.");
		Selection orangeAutoportal = util.select.position(3, 2, 1);
		InputWindowElement hammerConfiguration = new InputWindowElement(orangeAutoportal.getCenter(), Pointing.LEFT).rightClick()
				.withItem(HAMMER.getDefaultInstance());
		scene.overlay.showControls(hammerConfiguration, 40);
		scene.idle(60);
		scene.world.modifyBlock(orangeButtonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 1.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(14842149);
			return portalEntity;
		});
		scene.idle(30);
		scene.world.modifyBlock(orangeButtonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		scene.idle(60);

		//Move a cube through the portals
		scene.addKeyframe();
		scene.overlay.showText(80)
				.text("The two closest autoportals of opposite polarity that are not already linked will connect.");
		ElementLink<EntityElement> Cube1 = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(1.5, 1, 4.5);
			return cubeEntity;
		});
		ElementLink<EntityElement> Cube2 = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2.5, 1, 1.5);
			return cubeEntity;
		});
		scene.idle(40);
		scene.world.modifyEntity(Cube2, e -> e.lerpTo(4.5, 1, 1.5, 0, 0, 10, false));
		scene.idle(5);
		scene.world.modifyEntity(Cube1, e -> e.lerpTo(1.5, 1, 2.5, 0, 0, 10, false));
		scene.idle(55);
		scene.overlay.showText(60)
				.text("Player portals will also link with unlinked autoportal portals.");
		scene.idle(40);
	}
	public static void valid_portal_surfaces(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("valid_portal_surfaces", "Valid Portal Surfaces");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		BlockPos buttonPos = util.grid.at(1, 1, 1);
		BlockPos wirePosX = util.grid.at(1, 1, 2);
		BlockPos wirePosZ = util.grid.at(2, 1, 1);
		scene.setNextUpEnabled(true);
		scene.idle(20);
		//Intro Text
		scene.overlay.showText(70)
				.text("The placement rules for player portals still apply to autoportal portals.");
		scene.idle(80);
		scene.overlay.showText(70)
				.text("You cannot use an autoportal on surfaces that do not normally support portals.");

		//Press Button and open portal
		scene.world.modifyBlock(buttonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.modifyBlock(wirePosX, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlock(wirePosZ, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 1.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(1935067);
			return portalEntity;
		});
		scene.idle(30);
		scene.world.modifyBlock(buttonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		scene.world.modifyBlock(wirePosX, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);
		scene.world.modifyBlock(wirePosZ, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);
		scene.idle(60);
	}

	public static void multiple_autoportals(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("multiple_autoportals", "Multiple Autoportals");
		scene.configureBasePlate(0, 0, 5);
		BlockPos buttonPos = util.grid.at(1, 1, 2);
		Selection leftWire = util.select.fromTo(1, 1, 0, 2, 1, 1);
		BlockPos middleWire = util.grid.at(2, 1, 2);
		Selection rightWire = util.select.fromTo(1, 1, 3, 2, 1, 4);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(80)
				.text("When using more than 2 autoportals, the 2 closest unlinked frames of opposite polarity will connect.");
		scene.idle(100);
		scene.overlay.showText(80)
				.text("If you have an odd number of active autoportals, the extra will remain unlinked.");

		//Open Portals
		scene.world.modifyBlock(buttonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.modifyBlocks(leftWire, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlock(middleWire, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(rightWire, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 0.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(14842149);
			return portalEntity;
		});
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 2.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(14842149);
			return portalEntity;
		});
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 4.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(1935067);
			return portalEntity;
		});
		scene.idle(30);
		scene.world.modifyBlock(buttonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		scene.world.modifyBlocks(leftWire, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);
		scene.world.modifyBlock(middleWire, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);
		scene.world.modifyBlocks(rightWire, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);
		scene.idle(30);
		scene.addKeyframe();
		scene.idle(30);

		//its cube'n time *proceeds to cube all over the place*
		ElementLink<EntityElement> Cube1 = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2.5, 1, 0.5);
			return cubeEntity;
		});
		ElementLink<EntityElement> Cube2 = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2.5, 1, 2.5);
			return cubeEntity;
		});
		ElementLink<EntityElement> Cube3 = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.COMPANION_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(4.5, 1, 4.5);
			return cubeEntity;
		});
		scene.idle(40);
		scene.world.modifyEntity(Cube1, e -> e.lerpTo(3.6, 1, 0.5, 0, 0, 6, false));
		scene.world.modifyEntity(Cube2, e -> e.lerpTo(4.5, 1, 2.5, 0, 0, 10, false));
		scene.idle(5);
		scene.world.modifyEntity(Cube3, e -> e.lerpTo(2.5, 1, 4.5, 0, 0, 10, false));
	}
	public static void dyeing_autoportals(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("dyeing_autoportals", "Dyeing Autoportals");
		scene.scaleSceneView(0.9f);
		scene.configureBasePlate(0, 0, 5);
		BlockPos buttonPos = util.grid.at(4, 3, 4);
		BlockPos redPrimary = util.grid.at(3, 2, 1);
		BlockPos redSecondary = util.grid.at(3, 2, -1);
		BlockPos magentaPrimary = util.grid.at(1, 2, 3);
		BlockPos magentaSecondary = util.grid.at(-1, 2, 3);
		Selection leftWire = util.select.fromTo(4, 3, 3, 4, 3, 0);
		Selection rightWire = util.select.fromTo(3, 3, 4, 0, 3, 4);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(80)
				.text("By right clicking with a dye, you can change the color of an autoportal's portals.");

		//Dye the portals
		InputWindowElement primaryRedDyeUse = new InputWindowElement(redPrimary.getCenter(), Pointing.RIGHT).rightClick()
				.withItem(DyeItem.byColor(DyeColor.RED).getDefaultInstance());
		InputWindowElement secondaryRedDyeUse = new InputWindowElement(redSecondary.getCenter(), Pointing.RIGHT).rightClick()
				.withItem(DyeItem.byColor(DyeColor.RED).getDefaultInstance());
		InputWindowElement primaryMagentaDyeUse = new InputWindowElement(magentaPrimary.getCenter(), Pointing.LEFT).rightClick()
				.withItem(DyeItem.byColor(DyeColor.MAGENTA).getDefaultInstance());
		InputWindowElement secondaryMagentaDyeUse = new InputWindowElement(magentaSecondary.getCenter(), Pointing.LEFT).rightClick()
				.withItem(DyeItem.byColor(DyeColor.MAGENTA).getDefaultInstance());
		scene.overlay.showControls(primaryRedDyeUse, 60);
		scene.overlay.showControls(secondaryRedDyeUse, 60);
		scene.overlay.showControls(primaryMagentaDyeUse, 60);
		scene.overlay.showControls(secondaryMagentaDyeUse, 60);
		scene.idle(100);

		//Create the portals
		scene.addKeyframe();
		scene.overlay.showText(80)
				.text("The primary portal will be the color of the dye, and the secondary portal will be the opposite color.");
		scene.world.modifyBlock(buttonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.modifyBlocks(leftWire, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.modifyBlocks(rightWire, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 0.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(11546150);
			return portalEntity;
		});
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 2.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(5231066);
			return portalEntity;
		});
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(2.5, 2, 3.99);
			portalEntity.setColor(13061821);
			return portalEntity;
		});
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(0.5, 2, 3.99);
			portalEntity.setColor(3715395);
			return portalEntity;
		});
		scene.idle(30);
		scene.world.modifyBlock(buttonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		scene.world.modifyBlocks(leftWire, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);
		scene.world.modifyBlocks(rightWire, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);
		scene.idle(70);
		scene.overlay.showText(80)
				.text("Autoportals will only link to other autoportals with the same dye color applied.");
		scene.idle(100);
	}
}
