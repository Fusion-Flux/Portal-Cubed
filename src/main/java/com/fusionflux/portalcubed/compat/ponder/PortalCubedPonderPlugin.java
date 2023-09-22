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
		//ResourceLocations
		ResourceLocation autoPortal = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.AUTO_PORTAL_BLOCK);
		ResourceLocation excursionFunnel = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER);
		ResourceLocation laserEmitter = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.LASER_EMITTER);
		ResourceLocation laserCatcher = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.LASER_CATCHER);
		ResourceLocation laserRelay = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.LASER_RELAY);
		ResourceLocation hammer = BuiltInRegistries.ITEM.getKey(PortalCubedItems.HAMMER);
		ResourceLocation storageCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.STORAGE_CUBE);
		ResourceLocation portal1StorageCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.PORTAL_1_STORAGE_CUBE);
		ResourceLocation companionCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.COMPANION_CUBE);
		ResourceLocation portal1companionCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.PORTAL_1_COMPANION_CUBE);
		ResourceLocation oldApStorageCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.OLD_AP_CUBE);
		ResourceLocation redirectionCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.REDIRECTION_CUBE);
		ResourceLocation schrodingerCube = BuiltInRegistries.ITEM.getKey(PortalCubedItems.SCHRODINGER_CUBE);
		ResourceLocation radio = BuiltInRegistries.ITEM.getKey(PortalCubedItems.RADIO);
		ResourceLocation sentryTurret = BuiltInRegistries.ITEM.getKey(PortalCubedItems.TURRET);
		ResourceLocation beans = BuiltInRegistries.ITEM.getKey(PortalCubedItems.BEANS);
		ResourceLocation mug = BuiltInRegistries.ITEM.getKey(PortalCubedItems.MUG);
		ResourceLocation jug = BuiltInRegistries.ITEM.getKey(PortalCubedItems.JUG);
		ResourceLocation chair = BuiltInRegistries.ITEM.getKey(PortalCubedItems.CHAIR);
		ResourceLocation computer = BuiltInRegistries.ITEM.getKey(PortalCubedItems.COMPUTER);
		ResourceLocation hoopy = BuiltInRegistries.ITEM.getKey(PortalCubedItems.HOOPY);
		ResourceLocation lilPineapple = BuiltInRegistries.ITEM.getKey(PortalCubedItems.LIL_PINEAPPLE);
		ResourceLocation coreFrame = BuiltInRegistries.ITEM.getKey(PortalCubedItems.CORE_FRAME);
		ResourceLocation moralityCore = BuiltInRegistries.ITEM.getKey(PortalCubedItems.MORALITY_CORE);
		ResourceLocation curiosityCore = BuiltInRegistries.ITEM.getKey(PortalCubedItems.CURIOSITY_CORE);
		ResourceLocation cakeCore = BuiltInRegistries.ITEM.getKey(PortalCubedItems.CAKE_CORE);
		ResourceLocation angerCore = BuiltInRegistries.ITEM.getKey(PortalCubedItems.ANGER_CORE);
		ResourceLocation spaceCore = BuiltInRegistries.ITEM.getKey(PortalCubedItems.SPACE_CORE);
		ResourceLocation adventureCore = BuiltInRegistries.ITEM.getKey(PortalCubedItems.ADVENTURE_CORE);
		ResourceLocation factCore = BuiltInRegistries.ITEM.getKey(PortalCubedItems.FACT_CORE);
		ResourceLocation fizzler = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.FIZZLER_EMITTER);
		ResourceLocation portal1Fizzler = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.PORTAL_1_FIZZLER_EMITTER);
		ResourceLocation oldApFizzler = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.OLD_APERTURE_FIZZLER_EMITTER);
		ResourceLocation laserField = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.LASER_FIZZLER_EMITTER);
		ResourceLocation deathFizzler = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.DEATH_FIZZLER_EMITTER);
		ResourceLocation oldApDeathFizzler = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.OLD_APERTURE_DEATH_FIZZLER_EMITTER);
		ResourceLocation physicsRepulsionField = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.PHYSICS_REPULSION_FIELD_EMITTER);
		ResourceLocation matterInquisitionField = BuiltInRegistries.BLOCK.getKey(PortalCubedBlocks.MATTER_INQUISITION_FIELD_EMITTER);


		// Autoportal Scenes
		helper.addStoryBoard(autoPortal, "autoportal/introduction_and_configuration", AutoPortal::autoportal_intro);
		helper.addStoryBoard(autoPortal, "autoportal/valid_portal_surfaces", AutoPortal::valid_portal_surfaces);
		helper.addStoryBoard(autoPortal, "autoportal/multiple_autoportals", AutoPortal::multiple_autoportals);
		helper.addStoryBoard(autoPortal, "autoportal/dyeing_autoportals", AutoPortal::dyeing_autoportals);  //Add new section to this one about resetting colors once that is fixed

		// Excursion Funnel Scenes
		helper.addStoryBoard(excursionFunnel, "excursion_funnel/excursion_funnel", ExcursionFunnel::excursion_funnel);

		// Laser Scenes
		helper.forComponents(laserEmitter, laserCatcher, laserRelay, redirectionCube, schrodingerCube)
		.addStoryBoard("lasers/laser_intro", Lasers::laser_intro)
		.addStoryBoard("lasers/laser_relays", Lasers::laser_relays)
		.addStoryBoard("lasers/laser_blocking", Lasers::laser_blocking)
		.addStoryBoard("lasers/reflective_surfaces", Lasers::reflective_surfaces)
		.addStoryBoard("lasers/schrodinger_cubes", Lasers::schrodinger_cubes);

		// Hammer Scenes
		helper.addStoryBoard(hammer, "hammer/position_setting", Hammer::position_setting);
		helper.addStoryBoard(hammer, "hammer/changing_modes", Hammer::changing_modes);
		helper.addStoryBoard(hammer, "hammer/locking_props", Hammer::locking_props);
		//helper.addStoryBoard(hammer, "hammer/configuring_blocks", Hammer::configuring_blocks);  //Add once faith plates are reworked.  Include velocity helpers is possible

		// Prop Scenes
		helper.forComponents(storageCube, portal1StorageCube, companionCube, portal1companionCube, oldApStorageCube, redirectionCube, schrodingerCube, radio, sentryTurret, beans, mug, jug, computer, chair, hoopy, lilPineapple, coreFrame, moralityCore, curiosityCore, cakeCore, angerCore, spaceCore, adventureCore, factCore)
		//.addStoryBoard("props/weighted_buttons", Props::weighted_buttons);  //Add once buttons are reworked.
		  .addStoryBoard("fizzlers/fizzlers", Fizzlers::fizzlers)
		  .addStoryBoard("hammer/locking_props", Hammer::locking_props);
		//helper.forComponents(storageCube, portal1StorageCube, companionCube, portal1CompanionCube, oldApStorageCube, redirectionCube, schrodingerCube, radio, sentryTurret) //Props with skins
		//.addStoryBoard("props/prop_skins", Props::prop_skins); //Add once prop skins (aged/dirty variants, gel-coated props, turret skins, etc.) are added

		// Turret Scenes
		//helper.addStoryBoard(sentryTurret, "props/turrets", Props::turrets);  //Add once turrets function and can be configured

		// Fizzler Scenes
		helper.forComponents(fizzler, portal1Fizzler, oldApFizzler, laserField, deathFizzler, oldApDeathFizzler, physicsRepulsionField, matterInquisitionField)
		.addStoryBoard("fizzlers/fizzlers", Fizzlers::fizzlers)
		.addStoryBoard("fizzlers/laser_fields", Fizzlers::laser_fields)
		.addStoryBoard("fizzlers/death_fizzlers", Fizzlers::death_fizzlers)
		.addStoryBoard("fizzlers/matter_inquisition_fields", Fizzlers::matter_inquisition_fields)
		.addStoryBoard("fizzlers/physics_repulsion_fields", Fizzlers::physics_repulsion_fields);


	}

	public static void init() {
		PonderIndex.addPlugin(new PortalCubedPonderPlugin());
	}
}
