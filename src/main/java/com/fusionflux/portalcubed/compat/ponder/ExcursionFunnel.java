package com.fusionflux.portalcubed.compat.ponder;
import com.fusionflux.portalcubed.blocks.TallButtonVariant;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
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

public class ExcursionFunnel {
	public static void excursion_funnel(SceneBuilder scene, SceneBuildingUtil util) {
		//Setup
		scene.title("excursion_funnel", "Using the Excursion Funnel");
		scene.configureBasePlate(0, 0, 5);
		BlockPos funnelButtonPos = util.grid.at(0, 1, 3);
		BlockPos funnelPoint = util.grid.at(2, 2, 3);
		Selection funnel = util.select.fromTo(2, 2, 3, 1, 1, 3);
		scene.world.showSection(util.select.layersFrom(0), Direction.DOWN);
		scene.idle(20);

		//Funnel
		scene.overlay.showText(80)
				.text("The polarity of Excursion Funnel Emitters can be changed using a Hammer.");
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
