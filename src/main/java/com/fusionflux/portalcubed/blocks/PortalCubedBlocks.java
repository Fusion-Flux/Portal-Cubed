package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.blockentities.*;
import com.fusionflux.portalcubed.blocks.fizzler.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.block.extensions.api.QuiltMaterialBuilder;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedBlocks {
    public static final Item BASE_GEL = new Item(new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).maxCount(64).fireproof());
    public static final PropulsionGel PROPULSION_GEL = new PropulsionGel(QuiltBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final RepulsionGel REPULSION_GEL = new RepulsionGel(QuiltBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final GelFlat CONVERSION_GEL = new GelFlat(QuiltBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final AdhesionGel ADHESION_GEL = new AdhesionGel(QuiltBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    // public static final GelFlat GEL_FLAT = new GelFlat(QuiltBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));

    public static final HardLightBridgeEmitterBlock HLB_EMITTER_BLOCK = new HardLightBridgeEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final HardLightBridgeBlock HLB_BLOCK = new HardLightBridgeBlock(QuiltBlockSettings.of(Material.AIR).hardness(999999f).nonOpaque().resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL)));

    public static final AutoPortalBlock AUTO_PORTAL_BLOCK = new AutoPortalBlock(
        QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE)
    );

    public static final Block WHITE_CHECKERED_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block WHITE_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block WHITE_HALF_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock WHITE_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock WHITE_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock WHITE_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock WHITE_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock WHITE_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock WHITE_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static final Block PORTAL_1_WHITE_CHECKERED_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block PORTAL_1_WHITE_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block PORTAL_1_WHITE_HALF_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock PORTAL_1_WHITE_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock PORTAL_1_WHITE_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());


    public static final Block OLD_AP_WHITE_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block OLD_AP_WHITE_CHECKERED_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_WHITE_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_WHITE_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_WHITE_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_WHITE_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static final Block AGED_WHITE_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block AGED_WHITE_HALF_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock AGED_WHITE_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock AGED_WHITE_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_WHITE_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_WHITE_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_WHITE_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_WHITE_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());


    public static final Block PADDED_GRAY_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block PADDED_GRAY_HALF_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock PADDED_GRAY_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock PADDED_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock PADDED_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock PADDED_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());


    public static final Block AGED_PADDED_GRAY_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block AGED_PADDED_GRAY_HALF_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock AGED_PADDED_GRAY_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock AGED_PADDED_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_PADDED_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_PADDED_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static final Block SMOOTH_GRAY_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block SMOOTH_GRAY_HALF_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock SMOOTH_GRAY_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock SMOOTH_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock SMOOTH_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());


    public static final Block AGED_SMOOTH_GRAY_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block AGED_SMOOTH_GRAY_HALF_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock AGED_SMOOTH_GRAY_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock AGED_SMOOTH_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static final Block OLD_AP_GREEN_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock OLD_AP_GREEN_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock OLD_AP_GREEN_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_GREEN_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_GREEN_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_GREEN_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_GREEN_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static final Block OLD_AP_BLUE_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock OLD_AP_BLUE_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock OLD_AP_BLUE_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_BLUE_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_BLUE_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_BLUE_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock OLD_AP_BLUE_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static final OldApBlock _1x1_SINGLE_CROSSBAR = new OldApBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque());
    public static final OldApBlock _1x1_DOUBLE_CROSSBAR = new OldApBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque());
    public static final OldApDirectionalBlock _2X2_DOUBLE_CROSSBAR_TOP_LEFT = new OldApDirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque());
    public static final OldApDirectionalBlock _2X2_DOUBLE_CROSSBAR_BOTTOM_LEFT = new OldApDirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque());
    public static final OldApDirectionalBlock _2X2_DOUBLE_CROSSBAR_TOP_RIGHT = new OldApDirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque());
    public static final OldApDirectionalBlock _2X2_DOUBLE_CROSSBAR_BOTTOM_RIGHT = new OldApDirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque());

    public static final Block PORTAL_1_SMOOTH_GRAY_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final Block PORTAL_1_SMOOTH_GRAY_HALF_PANEL = new Block(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock PORTAL_1_SMOOTH_GRAY_2X1_PANEL_TOP = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final PillarBlock PORTAL_1_SMOOTH_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock PORTAL_1_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock PORTAL_1_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock PORTAL_1_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final DirectionalBlock PORTAL_1_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());


    public static final NeurotoxinBlock NEUROTOXIN_BLOCK = new NeurotoxinBlock(QuiltBlockSettings.of(new QuiltMaterialBuilder(MapColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().replaceable().burnable().build()).nonOpaque().noCollision());
    public static final NeurotoxinEmitterBlock NEUROTOXIN_EMITTER = new NeurotoxinEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque().noCollision().sounds(BlockSoundGroup.STONE));
    public static final ExcursionFunnelEmitter EXCURSION_FUNNEL_EMITTER = new ExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final DuelExcursionFunnelEmitter DUEL_EXCURSION_FUNNEL_EMITTER = new DuelExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final ReversedExcursionFunnelEmitter REVERSED_EXCURSION_FUNNEL_EMITTER = new ReversedExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final ExcursionFunnelMain EXCURSION_FUNNEL = new ExcursionFunnelMain(QuiltBlockSettings.of(Material.AIR).nonOpaque().noCollision());

    public static final TallButton TALL_BUTTON = new TallButton(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final TallButton OLD_AP_PEDESTAL_BUTTON = new TallButton(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static final SlidingDoorBlock PORTAL2DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final SlidingDoorBlock OLDAPDOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final SlidingDoorBlock PORTAL1DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static BlockEntityType<HardLightBridgeEmitterBlockEntity> HLB_EMITTER_ENTITY;
    public static BlockEntityType<HardLightBridgeBlockEntity> HLB_BLOCK_ENTITY;

    public static BlockEntityType<AutoPortalBlockEntity> AUTO_PORTAL_BLOCK_ENTITY;

    public static final FaithPlateBlock FAITH_PLATE = new FaithPlateBlock(QuiltBlockSettings.of(Material.STONE).hardness(999999f).resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL)));
    public static BlockEntityType<FaithPlateBlockEntity> FAITH_PLATE_ENTITY;

    public static final BetaFaithPlateBlock BETA_FAITH_PLATE = new BetaFaithPlateBlock(QuiltBlockSettings.of(Material.STONE).hardness(999999f).resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL)));
    public static BlockEntityType<BetaFaithPlateBlockEntity> BETA_FAITH_PLATE_ENTITY;

    public static BlockEntityType<NeurotoxinBlockEntity> NEUROTOXIN_BLOCK_ENTITY;
    public static BlockEntityType<NeurotoxinEmitterBlockEntity> NEUROTOXIN_EMITTER_ENTITY;
    public static BlockEntityType<ExcursionFunnelEmitterEntity> EXCURSION_FUNNEL_EMMITER_ENTITY;
    public static BlockEntityType<ReversedExcursionFunnelEmitterEntity> REVERSED_EXCURSION_FUNNEL_EMMITER_ENTITY;
    public static BlockEntityType<DuelExcursionFunnelEmitterEntity> DUEL_EXCURSION_FUNNEL_EMMITER_ENTITY;
    public static BlockEntityType<ExcursionFunnelEntityMain> EXCURSION_FUNNEL_ENTITY;

    public static final PowerBlock POWER_BLOCK = new PowerBlock(QuiltBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque());

    public static final FizzlerBlock FIZZLER = new FizzlerBlock(QuiltBlockSettings.of(Material.PORTAL).noCollision().strength(-1, 3600000));
    public static final FizzlerEmitter FIZZLER_EMITTER = new FizzlerEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().nonOpaque().sounds(BlockSoundGroup.STONE), FIZZLER);
    public static final FizzlerBlock PORTAL_1_FIZZLER = new FizzlerBlock(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter PORTAL_1_FIZZLER_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), PORTAL_1_FIZZLER);
    public static final FizzlerBlock OLD_APERTURE_FIZZLER = new FizzlerBlock(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter OLD_APERTURE_FIZZLER_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), OLD_APERTURE_FIZZLER);
    public static final DeathFizzlerBlock DEATH_FIZZLER = new DeathFizzlerBlock(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter DEATH_FIZZLER_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), DEATH_FIZZLER);
    public static final LaserFizzlerBlock LASER_FIZZLER = new LaserFizzlerBlock(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter LASER_FIZZLER_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), LASER_FIZZLER);
    public static final LaserFizzlerBlock OLD_APERTURE_LASER_FIELD = new LaserFizzlerBlock(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter OLD_APERTURE_LASER_FIELD_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), OLD_APERTURE_LASER_FIELD);
    public static final MatterInquisitionField MATTER_INQUISITION_FIELD = new MatterInquisitionField(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter MATTER_INQUISITION_FIELD_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), MATTER_INQUISITION_FIELD);
    public static final PhysicsRepulsionField PHYSICS_REPULSION_FIELD = new PhysicsRepulsionField(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter PHYSICS_REPULSION_FIELD_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), PHYSICS_REPULSION_FIELD);

    public static final LaserBlock LASER = new LaserBlock(QuiltBlockSettings.of(Material.AIR).nonOpaque().noCollision());
    public static BlockEntityType<LaserBlockEntity> LASER_ENTITY;

    public static final LaserEmitter LASER_EMITTER = new LaserEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque().sounds(BlockSoundGroup.STONE));
    public static BlockEntityType<LaserEmitterEntity> LASER_EMITTER_ENTITY;

    public static final LaserCatcherBlock LASER_CATCHER = new LaserCatcherBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque().sounds(BlockSoundGroup.STONE));
    public static BlockEntityType<LaserCatcherEntity> LASER_CATCHER_ENTITY;

    public static final LaserRelayBlock LASER_RELAY = new LaserRelayBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque().sounds(BlockSoundGroup.STONE));
    public static BlockEntityType<LaserRelayEntity> LASER_RELAY_ENTITY;

    public static final FloorButtonBlock FLOOR_BUTTON = new FloorButtonBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static BlockEntityType<FloorButtonBlockEntity> FLOOR_BUTTON_BLOCK_ENTITY;

    public static final OldApFloorButtonBlock OLD_AP_FLOOR_BUTTON = new OldApFloorButtonBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static BlockEntityType<OldApFloorButtonBlockEntity> OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY;

    //public static final GrilTest GRILTEST = new GrilTest(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque().noCollision().sounds(BlockSoundGroup.STONE));

    public static FlowableFluid STILL_TOXIC_GOO;
    public static FlowableFluid FLOWING_TOXIC_GOO;
    public static Item TOXIC_GOO_BUCKET;
    public static Block TOXIC_GOO;


    public static TagKey<Block> CANT_PLACE_PORTAL_ON = TagKey.of(Registry.BLOCK_KEY,new Identifier("portalcubed", "cant_place_portal_on"));
    public static TagKey<Block> GELCHECKTAG = TagKey.of(Registry.BLOCK_KEY,new Identifier("portalcubed", "gelchecktag"));
    public static TagKey<Block> ALLOW_PORTAL_IN = TagKey.of(Registry.BLOCK_KEY,new Identifier("portalcubed", "allowinside"));
    public static TagKey<Block> IMMOVABLE_BLOCKS = TagKey.of(Registry.BLOCK_KEY,new Identifier("portalcubed", "immovable_blocks"));
    public static void registerBlocks() {

            Registry.register(Registry.ITEM, new Identifier(PortalCubed.MODID, "base_gel"), BASE_GEL);

            Registry.register(Registry.BLOCK, id("propulsion_gel"), PROPULSION_GEL);
            Registry.register(Registry.ITEM, id("propulsion_gel"), new BlockItem(PROPULSION_GEL, new Item.Settings().group(PortalCubed.TestingElementsGroup).maxCount(64)));
            Registry.register(Registry.BLOCK, id("repulsion_gel"), REPULSION_GEL);
            Registry.register(Registry.ITEM, id("repulsion_gel"), new BlockItem(REPULSION_GEL, new Item.Settings().group(PortalCubed.TestingElementsGroup).maxCount(64)));

            Registry.register(Registry.BLOCK, id("adhesion_gel"), ADHESION_GEL);
            Registry.register(Registry.ITEM, id("adhesion_gel"), new BlockItem(ADHESION_GEL, new Item.Settings().group(PortalCubed.TestingElementsGroup).maxCount(64)));

            Registry.register(Registry.BLOCK, id("conversion_gel"), CONVERSION_GEL);
            Registry.register(Registry.ITEM, id("conversion_gel"), new BlockItem(CONVERSION_GEL, new Item.Settings().group(PortalCubed.TestingElementsGroup).maxCount(64)));

            Registry.register(Registry.BLOCK, id("portal_2_door"), PORTAL2DOOR);
            Registry.register(Registry.ITEM, id("portal_2_door"), new BlockItem(PORTAL2DOOR, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            Registry.register(Registry.BLOCK, id("old_ap_door"), OLDAPDOOR);
            Registry.register(Registry.ITEM, id("old_ap_door"), new BlockItem(OLDAPDOOR, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            Registry.register(Registry.BLOCK, id("portal_1_door"), PORTAL1DOOR);
            Registry.register(Registry.ITEM, id("portal_1_door"), new BlockItem(PORTAL1DOOR, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            Registry.register(Registry.BLOCK, id("white_panel"), WHITE_PANEL);
            Registry.register(Registry.ITEM, id("white_panel"), new BlockItem(WHITE_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("white_half_panel"), WHITE_HALF_PANEL);
            Registry.register(Registry.ITEM, id("white_half_panel"), new BlockItem(WHITE_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("white_checkered_panel"), WHITE_CHECKERED_PANEL);
            Registry.register(Registry.ITEM, id("white_checkered_panel"), new BlockItem(WHITE_CHECKERED_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("white_2x1_panel_bottom"), WHITE_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("white_2x1_panel_bottom"), new BlockItem(WHITE_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("white_2x1_panel_top"), WHITE_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("white_2x1_panel_top"), new BlockItem(WHITE_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("white_2x2_panel_bottom_left"), WHITE_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("white_2x2_panel_bottom_left"), new BlockItem(WHITE_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("white_2x2_panel_bottom_right"), WHITE_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("white_2x2_panel_bottom_right"), new BlockItem(WHITE_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("white_2x2_panel_top_left"), WHITE_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("white_2x2_panel_top_left"), new BlockItem(WHITE_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("white_2x2_panel_top_right"), WHITE_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("white_2x2_panel_top_right"), new BlockItem(WHITE_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("aged_white_panel"), AGED_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("aged_white_panel"), new BlockItem(AGED_WHITE_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_half_panel"), AGED_WHITE_HALF_PANEL);
            Registry.register(Registry.ITEM, id("aged_white_half_panel"), new BlockItem(AGED_WHITE_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x1_panel_bottom"), AGED_WHITE_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("aged_white_2x1_panel_bottom"), new BlockItem(AGED_WHITE_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x1_panel_top"), AGED_WHITE_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("aged_white_2x1_panel_top"), new BlockItem(AGED_WHITE_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x2_panel_bottom_left"), AGED_WHITE_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("aged_white_2x2_panel_bottom_left"), new BlockItem(AGED_WHITE_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x2_panel_bottom_right"), AGED_WHITE_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("aged_white_2x2_panel_bottom_right"), new BlockItem(AGED_WHITE_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x2_panel_top_left"), AGED_WHITE_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("aged_white_2x2_panel_top_left"), new BlockItem(AGED_WHITE_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x2_panel_top_right"), AGED_WHITE_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("aged_white_2x2_panel_top_right"), new BlockItem(AGED_WHITE_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("padded_gray_panel"), PADDED_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("padded_gray_panel"), new BlockItem(PADDED_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_half_panel"), PADDED_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("padded_gray_half_panel"), new BlockItem(PADDED_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x1_panel_bottom"), PADDED_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("padded_gray_2x1_panel_bottom"), new BlockItem(PADDED_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x1_panel_top"), PADDED_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("padded_gray_2x1_panel_top"), new BlockItem(PADDED_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x2_panel_bottom_left"), PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("padded_gray_2x2_panel_bottom_left"), new BlockItem(PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x2_panel_bottom_right"), PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("padded_gray_2x2_panel_bottom_right"), new BlockItem(PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x2_panel_top_left"), PADDED_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("padded_gray_2x2_panel_top_left"), new BlockItem(PADDED_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x2_panel_top_right"), PADDED_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("padded_gray_2x2_panel_top_right"), new BlockItem(PADDED_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("aged_padded_gray_panel"), AGED_PADDED_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("aged_padded_gray_panel"), new BlockItem(AGED_PADDED_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_half_panel"), AGED_PADDED_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("aged_padded_gray_half_panel"), new BlockItem(AGED_PADDED_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x1_panel_bottom"), AGED_PADDED_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x1_panel_bottom"), new BlockItem(AGED_PADDED_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x1_panel_top"), AGED_PADDED_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x1_panel_top"), new BlockItem(AGED_PADDED_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x2_panel_bottom_left"), AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x2_panel_bottom_left"), new BlockItem(AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x2_panel_bottom_right"), AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x2_panel_bottom_right"), new BlockItem(AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x2_panel_top_left"), AGED_PADDED_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x2_panel_top_left"), new BlockItem(AGED_PADDED_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x2_panel_top_right"), AGED_PADDED_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x2_panel_top_right"), new BlockItem(AGED_PADDED_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("smooth_gray_panel"), SMOOTH_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("smooth_gray_panel"), new BlockItem(SMOOTH_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_half_panel"), SMOOTH_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("smooth_gray_half_panel"), new BlockItem(SMOOTH_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x1_panel_bottom"), SMOOTH_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("smooth_gray_2x1_panel_bottom"), new BlockItem(SMOOTH_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x1_panel_top"), SMOOTH_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("smooth_gray_2x1_panel_top"), new BlockItem(SMOOTH_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x2_panel_bottom_left"), SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("smooth_gray_2x2_panel_bottom_left"), new BlockItem(SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x2_panel_bottom_right"), SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("smooth_gray_2x2_panel_bottom_right"), new BlockItem(SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x2_panel_top_left"), SMOOTH_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("smooth_gray_2x2_panel_top_left"), new BlockItem(SMOOTH_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x2_panel_top_right"), SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("smooth_gray_2x2_panel_top_right"), new BlockItem(SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("aged_smooth_gray_panel"), AGED_SMOOTH_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_panel"), new BlockItem(AGED_SMOOTH_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_half_panel"), AGED_SMOOTH_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_half_panel"), new BlockItem(AGED_SMOOTH_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x1_panel_bottom"), AGED_SMOOTH_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x1_panel_bottom"), new BlockItem(AGED_SMOOTH_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x1_panel_top"), AGED_SMOOTH_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x1_panel_top"), new BlockItem(AGED_SMOOTH_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x2_panel_bottom_left"), AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x2_panel_bottom_left"), new BlockItem(AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x2_panel_bottom_right"), AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x2_panel_bottom_right"), new BlockItem(AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x2_panel_top_left"), AGED_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x2_panel_top_left"), new BlockItem(AGED_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x2_panel_top_right"), AGED_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x2_panel_top_right"), new BlockItem(AGED_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("old_ap_white_2x2_panel_bottom_left"), OLD_AP_WHITE_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("old_ap_white_2x2_panel_bottom_left"), new BlockItem(OLD_AP_WHITE_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_white_2x2_panel_bottom_right"), OLD_AP_WHITE_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("old_ap_white_2x2_panel_bottom_right"), new BlockItem(OLD_AP_WHITE_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_white_2x2_panel_top_left"), OLD_AP_WHITE_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("old_ap_white_2x2_panel_top_left"), new BlockItem(OLD_AP_WHITE_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_white_2x2_panel_top_right"), OLD_AP_WHITE_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("old_ap_white_2x2_panel_top_right"), new BlockItem(OLD_AP_WHITE_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_white_checkered_panel"), OLD_AP_WHITE_CHECKERED_PANEL);
            Registry.register(Registry.ITEM, id("old_ap_white_checkered_panel"), new BlockItem(OLD_AP_WHITE_CHECKERED_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_white_panel"), OLD_AP_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("old_ap_white_panel"), new BlockItem(OLD_AP_WHITE_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("old_ap_green_panel"), OLD_AP_GREEN_PANEL);
            Registry.register(Registry.ITEM, id("old_ap_green_panel"), new BlockItem(OLD_AP_GREEN_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_green_2x1_panel_bottom"), OLD_AP_GREEN_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("old_ap_green_2x1_panel_bottom"), new BlockItem(OLD_AP_GREEN_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_green_2x1_panel_top"), OLD_AP_GREEN_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("old_ap_green_2x1_panel_top"), new BlockItem(OLD_AP_GREEN_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_green_2x2_panel_bottom_left"), OLD_AP_GREEN_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("old_ap_green_2x2_panel_bottom_left"), new BlockItem(OLD_AP_GREEN_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_green_2x2_panel_bottom_right"), OLD_AP_GREEN_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("old_ap_green_2x2_panel_bottom_right"), new BlockItem(OLD_AP_GREEN_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_green_2x2_panel_top_left"), OLD_AP_GREEN_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("old_ap_green_2x2_panel_top_left"), new BlockItem(OLD_AP_GREEN_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_green_2x2_panel_top_right"), OLD_AP_GREEN_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("old_ap_green_2x2_panel_top_right"), new BlockItem(OLD_AP_GREEN_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("old_ap_blue_panel"), OLD_AP_BLUE_PANEL);
            Registry.register(Registry.ITEM, id("old_ap_blue_panel"), new BlockItem(OLD_AP_BLUE_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_blue_2x1_panel_bottom"), OLD_AP_BLUE_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("old_ap_blue_2x1_panel_bottom"), new BlockItem(OLD_AP_BLUE_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_blue_2x1_panel_top"), OLD_AP_BLUE_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("old_ap_blue_2x1_panel_top"), new BlockItem(OLD_AP_BLUE_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_blue_2x2_panel_bottom_left"), OLD_AP_BLUE_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("old_ap_blue_2x2_panel_bottom_left"), new BlockItem(OLD_AP_BLUE_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_blue_2x2_panel_bottom_right"), OLD_AP_BLUE_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("old_ap_blue_2x2_panel_bottom_right"), new BlockItem(OLD_AP_BLUE_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_blue_2x2_panel_top_left"), OLD_AP_BLUE_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("old_ap_blue_2x2_panel_top_left"), new BlockItem(OLD_AP_BLUE_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("old_ap_blue_2x2_panel_top_right"), OLD_AP_BLUE_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("old_ap_blue_2x2_panel_top_right"), new BlockItem(OLD_AP_BLUE_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

            Registry.register(Registry.BLOCK, id("1x1_single_crossbar"), _1x1_SINGLE_CROSSBAR);
            Registry.register(Registry.ITEM, id("1x1_single_crossbar"), new BlockItem(_1x1_SINGLE_CROSSBAR, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("1x1_double_crossbar"), _1x1_DOUBLE_CROSSBAR);
            Registry.register(Registry.ITEM, id("1x1_double_crossbar"), new BlockItem(_1x1_DOUBLE_CROSSBAR, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("2x2_double_crossbar_bottom_left"), _2X2_DOUBLE_CROSSBAR_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("2x2_double_crossbar_bottom_left"), new BlockItem(_2X2_DOUBLE_CROSSBAR_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("2x2_double_crossbar_bottom_right"), _2X2_DOUBLE_CROSSBAR_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("2x2_double_crossbar_bottom_right"), new BlockItem(_2X2_DOUBLE_CROSSBAR_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("2x2_double_crossbar_top_left"), _2X2_DOUBLE_CROSSBAR_TOP_LEFT);
            Registry.register(Registry.ITEM, id("2x2_double_crossbar_top_left"), new BlockItem(_2X2_DOUBLE_CROSSBAR_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("2x2_double_crossbar_top_right"), _2X2_DOUBLE_CROSSBAR_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("2x2_double_crossbar_top_right"), new BlockItem(_2X2_DOUBLE_CROSSBAR_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));


        Registry.register(Registry.BLOCK, id("portal_1_white_panel"), PORTAL_1_WHITE_PANEL);
        Registry.register(Registry.ITEM, id("portal_1_white_panel"), new BlockItem(PORTAL_1_WHITE_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
        Registry.register(Registry.BLOCK, id("portal_1_white_half_panel"), PORTAL_1_WHITE_HALF_PANEL);
        Registry.register(Registry.ITEM, id("portal_1_white_half_panel"), new BlockItem(PORTAL_1_WHITE_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
        Registry.register(Registry.BLOCK, id("portal_1_white_checkered_panel"), PORTAL_1_WHITE_CHECKERED_PANEL);
        Registry.register(Registry.ITEM, id("portal_1_white_checkered_panel"), new BlockItem(PORTAL_1_WHITE_CHECKERED_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
        Registry.register(Registry.BLOCK, id("portal_1_white_2x1_panel_bottom"), PORTAL_1_WHITE_2X1_PANEL_BOTTOM);
        Registry.register(Registry.ITEM, id("portal_1_white_2x1_panel_bottom"), new BlockItem(PORTAL_1_WHITE_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
        Registry.register(Registry.BLOCK, id("portal_1_white_2x1_panel_top"), PORTAL_1_WHITE_2X1_PANEL_TOP);
        Registry.register(Registry.ITEM, id("portal_1_white_2x1_panel_top"), new BlockItem(PORTAL_1_WHITE_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));

        Registry.register(Registry.BLOCK, id("portal_1_smooth_gray_panel"), PORTAL_1_SMOOTH_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("portal_1_smooth_gray_panel"), new BlockItem(PORTAL_1_SMOOTH_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("portal_1_smooth_gray_half_panel"), PORTAL_1_SMOOTH_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("portal_1_smooth_gray_half_panel"), new BlockItem(PORTAL_1_SMOOTH_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("portal_1_smooth_gray_2x1_panel_bottom"), PORTAL_1_SMOOTH_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("portal_1_smooth_gray_2x1_panel_bottom"), new BlockItem(PORTAL_1_SMOOTH_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("portal_1_smooth_gray_2x1_panel_top"), PORTAL_1_SMOOTH_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("portal_1_smooth_gray_2x1_panel_top"), new BlockItem(PORTAL_1_SMOOTH_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("portal_1_smooth_gray_2x2_panel_bottom_left"), PORTAL_1_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("portal_1_smooth_gray_2x2_panel_bottom_left"), new BlockItem(PORTAL_1_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("portal_1_smooth_gray_2x2_panel_bottom_right"), PORTAL_1_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("portal_1_smooth_gray_2x2_panel_bottom_right"), new BlockItem(PORTAL_1_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("portal_1_smooth_gray_2x2_panel_top_left"), PORTAL_1_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("portal_1_smooth_gray_2x2_panel_top_left"), new BlockItem(PORTAL_1_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            Registry.register(Registry.BLOCK, id("portal_1_smooth_gray_2x2_panel_top_right"), PORTAL_1_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("portal_1_smooth_gray_2x2_panel_top_right"), new BlockItem(PORTAL_1_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));


            //Registry.register(Registry.BLOCK, id("light_cube"), LIGHT_CUBE);

            HLB_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("light_bridge_emitter_entity"), FabricBlockEntityTypeBuilder.create(HardLightBridgeEmitterBlockEntity::new, HLB_EMITTER_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("light_bridge_emitter"), HLB_EMITTER_BLOCK);
            Registry.register(Registry.ITEM, id("light_bridge_emitter"), new BlockItem(HLB_EMITTER_BLOCK, new Item.Settings().group(PortalCubed.TestingElementsGroup)));
            HLB_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("light_bridge_entity"), FabricBlockEntityTypeBuilder.create(HardLightBridgeBlockEntity::new, HLB_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("light_bridge"), HLB_BLOCK);
            NEUROTOXIN_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_entity"), FabricBlockEntityTypeBuilder.create(NeurotoxinBlockEntity::new, NEUROTOXIN_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("neurotoxin"), NEUROTOXIN_BLOCK);
            // Registry.register(Registry.ITEM, id("neurotoxin"), new BlockItem(NEUROTOXIN_BLOCK, new Item.Settings().group(PortalCubed.PortalBlocksGroup)));
            NEUROTOXIN_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_emitter_entity"), FabricBlockEntityTypeBuilder.create(NeurotoxinEmitterBlockEntity::new, NEUROTOXIN_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("neurotoxin_emitter"), NEUROTOXIN_EMITTER);
            Registry.register(Registry.ITEM, id("neurotoxin_emitter"), new BlockItem(NEUROTOXIN_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            AUTO_PORTAL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("auto_portal_entity"), QuiltBlockEntityTypeBuilder.create(AutoPortalBlockEntity::new, AUTO_PORTAL_BLOCK).build());
            Registry.register(Registry.BLOCK, id("auto_portal"), AUTO_PORTAL_BLOCK);
            Registry.register(Registry.ITEM, id("auto_portal"), new BlockItem(AUTO_PORTAL_BLOCK, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            EXCURSION_FUNNEL_EMMITER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_emitter_entity"), FabricBlockEntityTypeBuilder.create(ExcursionFunnelEmitterEntity::new, EXCURSION_FUNNEL_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("excursion_funnel_emitter"), EXCURSION_FUNNEL_EMITTER);
            Registry.register(Registry.ITEM, id("excursion_funnel_emitter"), new BlockItem(EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));
            EXCURSION_FUNNEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_entity"), FabricBlockEntityTypeBuilder.create(ExcursionFunnelEntityMain::new, EXCURSION_FUNNEL).build(null));
            Registry.register(Registry.BLOCK, id("excursion_funnel"), EXCURSION_FUNNEL);

            REVERSED_EXCURSION_FUNNEL_EMMITER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("reversed_excursion_funnel_emitter_entity"), FabricBlockEntityTypeBuilder.create(ReversedExcursionFunnelEmitterEntity::new, REVERSED_EXCURSION_FUNNEL_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("reversed_excursion_funnel_emitter"), REVERSED_EXCURSION_FUNNEL_EMITTER);
            Registry.register(Registry.ITEM, id("reversed_excursion_funnel_emitter"), new BlockItem(REVERSED_EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));


        DUEL_EXCURSION_FUNNEL_EMMITER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("duel_excursion_funnel_emitter_entity"), FabricBlockEntityTypeBuilder.create(DuelExcursionFunnelEmitterEntity::new, DUEL_EXCURSION_FUNNEL_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("duel_excursion_funnel_emitter"), DUEL_EXCURSION_FUNNEL_EMITTER);
            Registry.register(Registry.ITEM, id("duel_excursion_funnel_emitter"), new BlockItem(DUEL_EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        FAITH_PLATE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("faith_plate_entity"), FabricBlockEntityTypeBuilder.create(FaithPlateBlockEntity::new, FAITH_PLATE).build(null));
        Registry.register(Registry.BLOCK, id("faith_plate"), FAITH_PLATE);
        Registry.register(Registry.ITEM, id("faith_plate"), new BlockItem(FAITH_PLATE, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        BETA_FAITH_PLATE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("beta_faith_plate_entity"), FabricBlockEntityTypeBuilder.create(BetaFaithPlateBlockEntity::new, BETA_FAITH_PLATE).build(null));
        Registry.register(Registry.BLOCK, id("beta_faith_plate"), BETA_FAITH_PLATE);
        Registry.register(Registry.ITEM, id("beta_faith_plate"), new BlockItem(BETA_FAITH_PLATE, new Item.Settings().group(PortalCubed.TestingElementsGroup)));


        LASER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_entity"), FabricBlockEntityTypeBuilder.create(LaserBlockEntity::new, LASER).build(null));
            Registry.register(Registry.BLOCK, id("laser"), LASER);

            LASER_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_emitter_entity"), FabricBlockEntityTypeBuilder.create(LaserEmitterEntity::new, LASER_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("laser_emitter"), LASER_EMITTER);
            Registry.register(Registry.ITEM, id("laser_emitter"), new BlockItem(LASER_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            LASER_CATCHER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_catcher_entity"), FabricBlockEntityTypeBuilder.create(LaserCatcherEntity::new, LASER_CATCHER).build(null));
            Registry.register(Registry.BLOCK, id("laser_catcher"), LASER_CATCHER);
            Registry.register(Registry.ITEM, id("laser_catcher"), new BlockItem(LASER_CATCHER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            LASER_RELAY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_relay_entity"), FabricBlockEntityTypeBuilder.create(LaserRelayEntity::new, LASER_RELAY).build(null));
            Registry.register(Registry.BLOCK, id("laser_relay"), LASER_RELAY);
            Registry.register(Registry.ITEM, id("laser_relay"), new BlockItem(LASER_RELAY, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            FLOOR_BUTTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("floor_button_block_entity"), FabricBlockEntityTypeBuilder.create(FloorButtonBlockEntity::new, FLOOR_BUTTON).build(null));
            Registry.register(Registry.BLOCK, id("floor_button"), FLOOR_BUTTON);
            Registry.register(Registry.ITEM, id("floor_button"), new BlockItem(FLOOR_BUTTON, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("old_ap_floor_button_block_entity"), FabricBlockEntityTypeBuilder.create(OldApFloorButtonBlockEntity::new, OLD_AP_FLOOR_BUTTON).build(null));
            Registry.register(Registry.BLOCK, id("old_ap_floor_button"), OLD_AP_FLOOR_BUTTON);
            Registry.register(Registry.ITEM, id("old_ap_floor_button"), new BlockItem(OLD_AP_FLOOR_BUTTON, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("power_block"), POWER_BLOCK);
        Registry.register(Registry.ITEM, id("power_block"), new BlockItem(POWER_BLOCK, new Item.Settings().rarity(Rarity.EPIC).group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("fizzler"), FIZZLER);
        Registry.register(Registry.BLOCK, id("fizzler_emitter"), FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("fizzler_emitter"), new BlockItem(FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("portal_1_fizzler"), PORTAL_1_FIZZLER);
        Registry.register(Registry.BLOCK, id("portal_1_fizzler_emitter"), PORTAL_1_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("portal_1_fizzler_emitter"), new BlockItem(PORTAL_1_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("old_aperture_fizzler"), OLD_APERTURE_FIZZLER);
        Registry.register(Registry.BLOCK, id("old_aperture_fizzler_emitter"), OLD_APERTURE_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("old_aperture_fizzler_emitter"), new BlockItem(OLD_APERTURE_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("death_fizzler"), DEATH_FIZZLER);
        Registry.register(Registry.BLOCK, id("death_fizzler_emitter"), DEATH_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("death_fizzler_emitter"), new BlockItem(DEATH_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("laser_fizzler"), LASER_FIZZLER);
        Registry.register(Registry.BLOCK, id("laser_fizzler_emitter"), LASER_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("laser_fizzler_emitter"), new BlockItem(LASER_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("old_aperture_laser_field"), OLD_APERTURE_LASER_FIELD);
        Registry.register(Registry.BLOCK, id("old_aperture_laser_field_emitter"), OLD_APERTURE_LASER_FIELD_EMITTER);
        Registry.register(Registry.ITEM, id("old_aperture_laser_field_emitter"), new BlockItem(OLD_APERTURE_LASER_FIELD_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("matter_inquisition_field"), MATTER_INQUISITION_FIELD);
        Registry.register(Registry.BLOCK, id("matter_inquisition_field_emitter"), MATTER_INQUISITION_FIELD_EMITTER);
        Registry.register(Registry.ITEM, id("matter_inquisition_field_emitter"), new BlockItem(MATTER_INQUISITION_FIELD_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("physics_repulsion_field"), PHYSICS_REPULSION_FIELD);
        Registry.register(Registry.BLOCK, id("physics_repulsion_field_emitter"), PHYSICS_REPULSION_FIELD_EMITTER);
        Registry.register(Registry.ITEM, id("physics_repulsion_field_emitter"), new BlockItem(PHYSICS_REPULSION_FIELD_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));


        //STILL_TOXIC_GOO = Registry.register(Registry.FLUID, id("toxic_goo"), new ToxicGooFluid.Still());
            //FLOWING_TOXIC_GOO = Registry.register(Registry.FLUID, id("flowing_toxic_goo"), new ToxicGooFluid.Flowing());
            //TOXIC_GOO_BUCKET = Registry.register(Registry.ITEM, id("toxic_goo_bucket"), new BucketItem(STILL_TOXIC_GOO, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(PortalCubed.PortalBlocksGroup)));
            //TOXIC_GOO = Registry.register(Registry.BLOCK, id("toxic_goo"), new CustomFluidBlock(STILL_TOXIC_GOO, QuiltBlockSettings.copy(Blocks.WATER)){});

            Registry.register(Registry.BLOCK, id("pedestal_button"), TALL_BUTTON);
            Registry.register(Registry.ITEM, id("pedestal_button"), new BlockItem(TALL_BUTTON, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            Registry.register(Registry.BLOCK, id("old_ap_pedestal_button"), OLD_AP_PEDESTAL_BUTTON);
            Registry.register(Registry.ITEM, id("old_ap_pedestal_button"), new BlockItem(OLD_AP_PEDESTAL_BUTTON, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

            //Registry.register(Registry.BLOCK, id("griltest"), GRILTEST);
            //Registry.register(Registry.ITEM, id("griltest"), new BlockItem(GRILTEST, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

    }


}
