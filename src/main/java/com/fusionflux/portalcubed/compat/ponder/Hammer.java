package com.fusionflux.portalcubed.compat.ponder;
import com.fusionflux.portalcubed.blocks.OldApTallButton;
import com.fusionflux.portalcubed.blocks.TallButtonVariant;
import com.fusionflux.portalcubed.blocks.bridge.Edge;
import com.fusionflux.portalcubed.blocks.bridge.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.RadioEntity;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import com.fusionflux.portalcubed.entity.beams.ExcursionFunnelEntity;
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

import static com.fusionflux.portalcubed.items.PortalCubedItems.HAMMER;


public class Hammer {
	public static void position_setting(SceneBuilder scene, SceneBuildingUtil util) {

		//Setup
		scene.title("position_setting", "Using the Hammer");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		BlockPos newButtonPos = util.grid.at(4, 1, 1);
		BlockPos oldButtonPos = util.grid.at(4, 1, 2);
		BlockPos newButtonClickPos = util.grid.at(4, 2, 1);
		BlockPos oldButtonClickPos = util.grid.at(4, 2, 2);
		BlockPos bridgePos = util.grid.at(1, 1, 3);
		BlockPos bridgeClickPos = util.grid.at(1, 2, 3);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(80)
				.text("The Hammer can be used to configure the position and properties of various Test Elements.");
		scene.idle(100);
		scene.overlay.showText(80)
				.text("By interacting with a Hammer on the following blocks, you can change their position or orientation.");
		scene.idle(100);

		//Buttons
		scene.addKeyframe();
		scene.overlay.showText(60)
				.text("Using a Hammer on Pedestal Buttons changes its offset.");
		scene.idle(60);
		InputWindowElement newButtonConfig = new InputWindowElement(newButtonClickPos.getCenter(), Pointing.DOWN).rightClick()
				.withItem(HAMMER.getDefaultInstance());
		InputWindowElement oldButtonConfig = new InputWindowElement(oldButtonClickPos.getCenter(), Pointing.DOWN).rightClick()
				.withItem(HAMMER.getDefaultInstance());
		scene.overlay.showControls(newButtonConfig, 20);
		scene.world.modifyBlock(newButtonPos, s -> s.setValue(TallButtonVariant.OFFSET, true), false);
		scene.idle(20);
		scene.overlay.showText(100)
				.text("By default they are centered, however using the hammer you can align them to the edge of the block they are placed on.");
		scene.idle(40);
		scene.overlay.showControls(oldButtonConfig, 20);
		scene.world.modifyBlock(oldButtonPos, s -> s.setValue(OldApTallButton.OFFSET, true), false);
		scene.idle(80);

		//Light Bridge
		scene.addKeyframe();
		scene.overlay.showText(60)
				.text("Similarly, the Light Bridge Emitter's orientation can be changed using the Hammer.");
		scene.idle(80);
		scene.overlay.showText(100)
				.text("Using the Hammer rotates the Light Bridge Emitter 90° counterclockwise.");
		scene.idle(20);
		InputWindowElement bridgeConfig = new InputWindowElement(bridgeClickPos.getCenter(), Pointing.DOWN).rightClick()
				.withItem(HAMMER.getDefaultInstance());
		scene.overlay.showControls(bridgeConfig, 20);
		scene.world.modifyBlock(bridgePos, s -> s.setValue(HardLightBridgeEmitterBlock.EDGE, Edge.RIGHT), false);
		scene.idle(40);
		scene.overlay.showControls(bridgeConfig, 20);
		scene.world.modifyBlock(bridgePos, s -> s.setValue(HardLightBridgeEmitterBlock.EDGE, Edge.DOWN), false);
		scene.idle(40);
		scene.overlay.showControls(bridgeConfig, 20);
		scene.world.modifyBlock(bridgePos, s -> s.setValue(HardLightBridgeEmitterBlock.EDGE, Edge.LEFT), false);
		scene.idle(40);
		scene.overlay.showControls(bridgeConfig, 20);
		scene.world.modifyBlock(bridgePos, s -> s.setValue(HardLightBridgeEmitterBlock.EDGE, Edge.UP), false);
		scene.idle(40);

	}

	public static void locking_props(SceneBuilder scene, SceneBuildingUtil util) {

		//Setup
		scene.title("locking_props", "Locking Props");
		scene.configureBasePlate(0, 0, 5);
		Selection waterSel = util.select.fromTo(3, 1, 4, 2, 1, 0);
		scene.world.showSection(util.select.layer(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		BlockPos cubePos = util.grid.at(2, 1, 3);
		BlockPos radioPos = util.grid.at(1, 1, 3);
		scene.idle(20);

		//Spawn props
		ElementLink<EntityElement> movingCube = scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.STORAGE_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(3.5, 1, 3.5);
			return cubeEntity;
		});
		scene.world.createEntity(w -> {
			StorageCubeEntity cubeEntity = PortalCubedEntities.STORAGE_CUBE.create(w);
			assert cubeEntity != null;
			cubeEntity.setPosRaw(2.5, 1, 3.5);
			return cubeEntity;
		});
		scene.world.createEntity(w -> {
			RadioEntity radioEntity = PortalCubedEntities.RADIO.create(w);
			assert radioEntity != null;
			radioEntity.setPosRaw(1.5, 1, 3.5);
			return radioEntity;
		});


		//Intro Text
		scene.overlay.showText(60)
				.text("The Hammer can be used to lock prop entities in place.");
		scene.idle(80);
		scene.overlay.showText(120)
				.text("This prevents them from moving or being grabbed by players, and is intended for if you plan on using a prop entity as decoration.");
		scene.idle(140);

		//Lock the taskbar, Lock the taskbar
		scene.overlay.showText(80)
				.text("Sneak right click any prop entity while holding a Hammer to lock or unlock it.");
		InputWindowElement cubeLock = new InputWindowElement(cubePos.getCenter(), Pointing.DOWN).rightClick().whileSneaking()
				.withItem(HAMMER.getDefaultInstance());
		scene.overlay.showControls(cubeLock, 20);
		scene.idle(30);
		InputWindowElement radioLock = new InputWindowElement(radioPos.getCenter(), Pointing.DOWN).rightClick().whileSneaking()
				.withItem(HAMMER.getDefaultInstance());
		scene.overlay.showControls(radioLock, 20);
		scene.idle(110);

		//Stay hydrated
		scene.world.showSection(waterSel, Direction.UP);
		scene.world.modifyEntity(movingCube, e -> e.lerpTo(3.5, 1, 0.7, 0, 0, 50, false));
		scene.idle(80);
		scene.world.hideSection(waterSel, Direction.DOWN);

		//Radio muting
		scene.addKeyframe();
		scene.idle(20);
		scene.overlay.showText(80)
				.text("Radios have an extra function that can be toggled using the Hammer.");
		scene.idle(100);
		scene.overlay.showText(100)
				.text("Interact using the Hammer without sneaking to change whether or not grabbing the radio toggles the music.");
		InputWindowElement radioToggle = new InputWindowElement(radioPos.getCenter(), Pointing.DOWN).rightClick()
				.withItem(HAMMER.getDefaultInstance());
		scene.overlay.showControls(radioToggle, 20);
		scene.idle(120);
	}

	public static void changing_modes(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("changing_modes", "Changing Modes");
		scene.configureBasePlate(0, 0, 5);
		BlockPos portalButtonPos = util.grid.at(3, 1, 2);
		BlockPos funnelButtonPos = util.grid.at(0, 1, 3);
		BlockPos autoportal = util.grid.at(3, 3, 1);
		BlockPos funnelPoint = util.grid.at(2, 2, 3);
		Selection funnel = util.select.fromTo(2, 2, 3, 1, 1, 3);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.setNextUpEnabled(true);
		scene.idle(20);

		//Intro Text
		scene.overlay.showText(80)
				.text("Some test elements have different modes that can be selected using the Hammer.");
		scene.idle(100);

		//Autoportal
		scene.addKeyframe();
		scene.overlay.showText(120)
				.text("The Autoportal can be switched between creating primary and secondary portals using the Hammer.");
		scene.idle(20);
		scene.world.modifyBlock(portalButtonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		ElementLink<EntityElement> bluePortal = scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 1.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(1935067);
			return portalEntity;
		});
		scene.idle(30);
		scene.world.modifyBlock(portalButtonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		scene.idle(40);
		scene.world.modifyBlock(portalButtonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.modifyEntity(bluePortal, Entity::discard);
		scene.idle(30);
		scene.world.modifyBlock(portalButtonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		InputWindowElement autoportalConfig = new InputWindowElement(autoportal.getCenter(), Pointing.DOWN).rightClick()
				.withItem(HAMMER.getDefaultInstance());
		scene.overlay.showControls(autoportalConfig, 20);
		scene.idle(40);
		scene.world.modifyBlock(portalButtonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.createEntity(w -> {
			Portal portalEntity = PortalCubedEntities.PORTAL.create(w);
			assert portalEntity != null;
			portalEntity.setPosRaw(3.99, 2, 1.5);
			portalEntity.setRotation(com.mojang.math.Axis.YP.rotationDegrees(90));
			portalEntity.setColor(14842149);
			return portalEntity;
		});
		scene.idle(30);
		scene.world.modifyBlock(portalButtonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		scene.idle(40);

		//Funnel
		scene.addKeyframe();
		scene.overlay.showText(80)
				.text("The polarity of Excursion Funnel Emitters can also be changed this way.");
		scene.idle(100);
		scene.overlay.showText(60)
				.text("There are 3 modes - Forward, Reversed, and Dual.");
		scene.idle(80);


		scene.overlay.showText(130)
				.text("When using Forward and Reversed, the funnel will activate upon receiving redstone power and the beam will move entities in the selected direction.");
		scene.idle(40);

		scene.world.modifyBlock(funnelButtonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.FORWARD_ON), false);
		ElementLink<EntityElement> forwardFunnel = scene.world.createEntity(w -> {
			ExcursionFunnelEntity excursionFunnel = PortalCubedEntities.EXCURSION_FUNNEL.create(w);
			assert excursionFunnel != null;
			excursionFunnel.setPosRaw(2, 2, 4);
			excursionFunnel.setLength(4f);
			return excursionFunnel;
		});
		scene.idle(30);
		scene.world.modifyEntity(forwardFunnel, Entity::discard);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.FORWARD_OFF), false);
		scene.world.modifyBlock(funnelButtonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		scene.idle(20);
		InputWindowElement funnelConfig = new InputWindowElement(funnelPoint.getCenter(), Pointing.DOWN).rightClick()
				.withItem(HAMMER.getDefaultInstance());
		scene.overlay.showControls(funnelConfig, 20);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.REVERSED_OFF), false);
		scene.idle(20);
		scene.world.modifyBlock(funnelButtonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.REVERSED_ON), false);
		ElementLink<EntityElement> reversedFunnel = scene.world.createEntity(w -> {
			ExcursionFunnelEntity excursionFunnel = PortalCubedEntities.EXCURSION_FUNNEL.create(w);
			assert excursionFunnel != null;
			excursionFunnel.setPosRaw(2, 2, 4);
			excursionFunnel.setLength(4f);
			excursionFunnel.setReversed(true);
			return excursionFunnel;
		});
		scene.idle(30);
		scene.world.modifyEntity(reversedFunnel, Entity::discard);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.REVERSED_OFF), false);
		scene.world.modifyBlock(funnelButtonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);

		scene.idle(40);
		scene.overlay.showText(60)
				.text("In Dual mode, the funnel is always active and changes polarity when powered.");
		scene.overlay.showControls(funnelConfig, 20);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.FORWARD_ON), false);
		ElementLink<EntityElement> dualFunnelForward1 = scene.world.createEntity(w -> {
			ExcursionFunnelEntity excursionFunnel = PortalCubedEntities.EXCURSION_FUNNEL.create(w);
			assert excursionFunnel != null;
			excursionFunnel.setPosRaw(2, 2, 4);
			excursionFunnel.setLength(4f);
			return excursionFunnel;
		});
		scene.idle(40);
		scene.world.modifyBlock(funnelButtonPos, s -> s.setValue(TallButtonVariant.POWERED, true), false);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.REVERSED_ON), false);
		scene.world.modifyEntity(dualFunnelForward1, Entity::discard);
		ElementLink<EntityElement> dualFunnelReversed = scene.world.createEntity(w -> {
			ExcursionFunnelEntity excursionFunnel = PortalCubedEntities.EXCURSION_FUNNEL.create(w);
			assert excursionFunnel != null;
			excursionFunnel.setPosRaw(2, 2, 4);
			excursionFunnel.setLength(4f);
			excursionFunnel.setReversed(true);
			return excursionFunnel;
		});
		scene.idle(30);
		scene.world.modifyEntity(dualFunnelReversed, Entity::discard);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.FORWARD_ON), false);
		scene.world.modifyBlock(funnelButtonPos, s -> s.setValue(TallButtonVariant.POWERED, false), false);
		ElementLink<EntityElement> dualFunnelForward2 = scene.world.createEntity(w -> {
			ExcursionFunnelEntity excursionFunnel = PortalCubedEntities.EXCURSION_FUNNEL.create(w);
			assert excursionFunnel != null;
			excursionFunnel.setPosRaw(2, 2, 4);
			excursionFunnel.setLength(4f);
			return excursionFunnel;
		});
		scene.idle(40);
		scene.overlay.showControls(funnelConfig, 20);
		scene.world.modifyEntity(dualFunnelForward2, Entity::discard);
		scene.world.modifyBlocks(funnel, s -> s.setValue(ExcursionFunnelEmitterBlock.MODE, ExcursionFunnelEmitterBlock.Mode.FORWARD_OFF), false);
		scene.idle(50);

	}
}
