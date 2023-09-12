package com.fusionflux.portalcubed.compat.ponder;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.PonderPlugin;
import net.createmod.ponder.foundation.PonderRegistrationHelper;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class PortalCubedPonderPlugin implements PonderPlugin {
	@Override
	@NotNull
	public String getModID() {
		return PortalCubed.MOD_ID;
	}

	@Override
	public void registerScenes() {
		PonderRegistrationHelper helper = new PonderRegistrationHelper(getModID());
		//Autoportal Scenes
		ResourceLocation autoPortal = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.AUTO_PORTAL_BLOCK);
		helper.addStoryBoard(autoPortal, "autoportal/introduction_and_configuration", AutoPortal::autoportal_intro);
		helper.addStoryBoard(autoPortal, "autoportal/valid_portal_surfaces", AutoPortal::valid_portal_surfaces);
		helper.addStoryBoard(autoPortal, "autoportal/multiple_autoportals", AutoPortal::multiple_autoportals);
		helper.addStoryBoard(autoPortal, "autoportal/dyeing_autoportals", AutoPortal::dyeing_autoportals);
		//Laser Scenes
		ResourceLocation laserEmitter = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.LASER_EMITTER);
		ResourceLocation laserCatcher = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.LASER_CATCHER);
		ResourceLocation laserRelay = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.LASER_RELAY);
		ResourceLocation laserCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.REDIRECTION_CUBE);
		ResourceLocation schrodingerCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.SCHRODINGER_CUBE);
		helper.forComponents(laserEmitter, laserCatcher, laserRelay, laserCube, schrodingerCube)
		.addStoryBoard("lasers/laser_intro", Lasers::laser_intro)
		.addStoryBoard("lasers/laser_relays", Lasers::laser_relays)
		.addStoryBoard("lasers/laser_blocking", Lasers::laser_blocking)
		.addStoryBoard("lasers/reflective_surfaces", Lasers::reflective_surfaces)
		.addStoryBoard("lasers/schrodinger_cubes", Lasers::schrodinger_cubes);
	}

	public static void init() {
		PonderIndex.addPlugin(new PortalCubedPonderPlugin());
	}
}
