package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.blockentities.*;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
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
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedBlocks {

    public static final PropulsionGel PROPULSION_GEL = new PropulsionGel(FabricBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final RepulsionGel REPULSION_GEL = new RepulsionGel(FabricBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final GelFlat GEL = new GelFlat(FabricBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    // public static final GelFlat GEL_FLAT = new GelFlat(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));

    public static final HardLightBridgeEmitterBlock HLB_EMITTER_BLOCK = new HardLightBridgeEmitterBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static final HardLightBridgeBlock HLB_BLOCK = new HardLightBridgeBlock(FabricBlockSettings.of(Material.PLANT).hardness(999999f).nonOpaque().luminance(10).resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_METAL_BREAK, SoundEvents.BLOCK_METAL_STEP, SoundEvents.BLOCK_METAL_PLACE, SoundEvents.BLOCK_METAL_HIT, SoundEvents.BLOCK_METAL_FALL)));

    public static final Block WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block WHITE_HALF_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock WHITE_2X1_PANEL_TOP = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock WHITE_2X1_PANEL_BOTTOM = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock WHITE_2X2_PANEL_TOP_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock WHITE_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock WHITE_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock WHITE_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static final Block AGED_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block AGED_WHITE_HALF_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock AGED_WHITE_2X1_PANEL_TOP = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock AGED_WHITE_2X1_PANEL_BOTTOM = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_WHITE_2X2_PANEL_TOP_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_WHITE_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_WHITE_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_WHITE_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));


    public static final Block PADDED_GRAY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block PADDED_GRAY_HALF_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock PADDED_GRAY_2X1_PANEL_TOP = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock PADDED_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock PADDED_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock PADDED_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));


    public static final Block AGED_PADDED_GRAY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block AGED_PADDED_GRAY_HALF_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock AGED_PADDED_GRAY_2X1_PANEL_TOP = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock AGED_PADDED_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_PADDED_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_PADDED_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static final Block SMOOTH_GRAY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block SMOOTH_GRAY_HALF_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock SMOOTH_GRAY_2X1_PANEL_TOP = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock SMOOTH_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock SMOOTH_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));


    public static final Block AGED_SMOOTH_GRAY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block AGED_SMOOTH_GRAY_HALF_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock AGED_SMOOTH_GRAY_2X1_PANEL_TOP = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock AGED_SMOOTH_GRAY_2X1_PANEL_BOTTOM = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));


    public static final NeurotoxinBlock NEUROTOXIN_BLOCK = new NeurotoxinBlock(FabricBlockSettings.of(new FabricMaterialBuilder(MapColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().replaceable().burnable().build()).nonOpaque().noCollision());
    public static final NeurotoxinEmitterBlock NEUROTOXIN_EMITTER = new NeurotoxinEmitterBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().noCollision().sounds(BlockSoundGroup.METAL));
    public static final ExcursionFunnelEmitter EXCURSION_FUNNEL_EMITTER = new ExcursionFunnelEmitter(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static final ExcursionFunnel EXCURSION_FUNNEL = new ExcursionFunnel(FabricBlockSettings.of(Material.AIR).nonOpaque().noCollision());
    public static final LightBlock LIGHT_CUBE = new LightBlock(FabricBlockSettings.of(Material.AIR).luminance(15).noCollision().air().hardness(3.5f));

    public static final TallButton TALL_BUTTON = new TallButton(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static final SlidingDoorBlock SLIDINGDOOR = new SlidingDoorBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static BlockEntityType<HardLightBridgeEmitterBlockEntity> HLB_EMITTER_ENTITY;
    public static BlockEntityType<HardLightBridgeBlockEntity> HLB_BLOCK_ENTITY;
    public static BlockEntityType<NeurotoxinBlockEntity> NEUROTOXIN_BLOCK_ENTITY;
    public static BlockEntityType<NeurotoxinEmitterBlockEntity> NEUROTOXIN_EMITTER_ENTITY;
    public static BlockEntityType<ExcursionFunnelEmitterEntity> EXCURSION_FUNNEL_EMMITER_ENTITY;
    public static BlockEntityType<ExcursionFunnelEntity> EXCURSION_FUNNEL_ENTITY;

    public static FlowableFluid STILL_TOXIC_GOO;
    public static FlowableFluid FLOWING_TOXIC_GOO;
    public static Item TOXIC_GOO_BUCKET;
    public static Block TOXIC_GOO;

    public static Tag<Block> CANT_PLACE_PORTAL_ON = TagRegistry.block(new Identifier("portalcubed", "cant_place_portal_on"));
    public static Tag<Block> GELCHECKTAG = TagRegistry.block(new Identifier("portalcubed", "gelchecktag"));
    public static Tag<Block> ALLOW_PORTAL_IN = TagRegistry.block(new Identifier("portalcubed", "allowinside"));
    public static Tag<Block> IMMOVABLE_BLOCKS = TagRegistry.block(new Identifier("portalcubed", "immovable_blocks"));
    public static void registerBlocks() {
        if (PortalCubedConfig.get().enabled.enableGels) {
            Registry.register(Registry.BLOCK, id("propulsion_gel"), PROPULSION_GEL);
            Registry.register(Registry.ITEM, id("propulsion_gel"), new GelBucket(PROPULSION_GEL, new Item.Settings().group(PortalCubed.PortalCubedGroup).maxCount(1)));
            Registry.register(Registry.BLOCK, id("repulsion_gel"), REPULSION_GEL);
            Registry.register(Registry.ITEM, id("repulsion_gel"), new GelBucket(REPULSION_GEL, new Item.Settings().group(PortalCubed.PortalCubedGroup).maxCount(1)));
            Registry.register(Registry.BLOCK, id("gel"), GEL);
            Registry.register(Registry.ITEM, id("gel"), new GelBucket(GEL, new Item.Settings().group(PortalCubed.PortalCubedGroup).maxCount(1)));
        }

        if (PortalCubedConfig.get().enabled.enablePortal2Blocks) {
            Registry.register(Registry.BLOCK, id("sliding_door"), SLIDINGDOOR);
            Registry.register(Registry.ITEM, id("sliding_door"), new BlockItem(SLIDINGDOOR, new Item.Settings().group(PortalCubed.PortalCubedGroup)));

            Registry.register(Registry.BLOCK, id("white_panel"), WHITE_PANEL);
            Registry.register(Registry.ITEM, id("white_panel"), new BlockItem(WHITE_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("white_half_panel"), WHITE_HALF_PANEL);
            Registry.register(Registry.ITEM, id("white_half_panel"), new BlockItem(WHITE_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("white_2x1_panel_top"), WHITE_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("white_2x1_panel_top"), new BlockItem(WHITE_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("white_2x1_panel_bottom"), WHITE_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("white_2x1_panel_bottom"), new BlockItem(WHITE_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("white_2x2_panel_top_left"), WHITE_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("white_2x2_panel_top_left"), new BlockItem(WHITE_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("white_2x2_panel_top_right"), WHITE_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("white_2x2_panel_top_right"), new BlockItem(WHITE_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("white_2x2_panel_bottom_left"), WHITE_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("white_2x2_panel_bottom_left"), new BlockItem(WHITE_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("white_2x2_panel_bottom_right"), WHITE_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("white_2x2_panel_bottom_right"), new BlockItem(WHITE_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));

            Registry.register(Registry.BLOCK, id("aged_white_panel"), AGED_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("aged_white_panel"), new BlockItem(AGED_WHITE_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_half_panel"), AGED_WHITE_HALF_PANEL);
            Registry.register(Registry.ITEM, id("aged_white_half_panel"), new BlockItem(AGED_WHITE_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x1_panel_top"), AGED_WHITE_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("aged_white_2x1_panel_top"), new BlockItem(AGED_WHITE_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x1_panel_bottom"), AGED_WHITE_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("aged_white_2x1_panel_bottom"), new BlockItem(AGED_WHITE_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x2_panel_top_left"), AGED_WHITE_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("aged_white_2x2_panel_top_left"), new BlockItem(AGED_WHITE_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x2_panel_top_right"), AGED_WHITE_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("aged_white_2x2_panel_top_right"), new BlockItem(AGED_WHITE_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x2_panel_bottom_left"), AGED_WHITE_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("aged_white_2x2_panel_bottom_left"), new BlockItem(AGED_WHITE_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_white_2x2_panel_bottom_right"), AGED_WHITE_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("aged_white_2x2_panel_bottom_right"), new BlockItem(AGED_WHITE_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));

            Registry.register(Registry.BLOCK, id("padded_gray_panel"), PADDED_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("padded_gray_panel"), new BlockItem(PADDED_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_half_panel"), PADDED_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("padded_gray_half_panel"), new BlockItem(PADDED_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x1_panel_top"), PADDED_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("padded_gray_2x1_panel_top"), new BlockItem(PADDED_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x1_panel_bottom"), PADDED_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("padded_gray_2x1_panel_bottom"), new BlockItem(PADDED_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x2_panel_top_left"), PADDED_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("padded_gray_2x2_panel_top_left"), new BlockItem(PADDED_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x2_panel_top_right"), PADDED_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("padded_gray_2x2_panel_top_right"), new BlockItem(PADDED_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x2_panel_bottom_left"), PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("padded_gray_2x2_panel_bottom_left"), new BlockItem(PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("padded_gray_2x2_panel_bottom_right"), PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("padded_gray_2x2_panel_bottom_right"), new BlockItem(PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));


            Registry.register(Registry.BLOCK, id("aged_padded_gray_panel"), AGED_PADDED_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("aged_padded_gray_panel"), new BlockItem(AGED_PADDED_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_half_panel"), AGED_PADDED_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("aged_padded_gray_half_panel"), new BlockItem(AGED_PADDED_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x1_panel_top"), AGED_PADDED_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x1_panel_top"), new BlockItem(AGED_PADDED_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x1_panel_bottom"), AGED_PADDED_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x1_panel_bottom"), new BlockItem(AGED_PADDED_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x2_panel_top_left"), AGED_PADDED_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x2_panel_top_left"), new BlockItem(AGED_PADDED_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x2_panel_top_right"), AGED_PADDED_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x2_panel_top_right"), new BlockItem(AGED_PADDED_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x2_panel_bottom_left"), AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x2_panel_bottom_left"), new BlockItem(AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_padded_gray_2x2_panel_bottom_right"), AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("aged_padded_gray_2x2_panel_bottom_right"), new BlockItem(AGED_PADDED_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));

            Registry.register(Registry.BLOCK, id("smooth_gray_panel"), SMOOTH_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("smooth_gray_panel"), new BlockItem(SMOOTH_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_half_panel"), SMOOTH_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("smooth_gray_half_panel"), new BlockItem(SMOOTH_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x1_panel_top"), SMOOTH_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("smooth_gray_2x1_panel_top"), new BlockItem(SMOOTH_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x1_panel_bottom"), SMOOTH_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("smooth_gray_2x1_panel_bottom"), new BlockItem(SMOOTH_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x2_panel_top_left"), SMOOTH_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("smooth_gray_2x2_panel_top_left"), new BlockItem(SMOOTH_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x2_panel_top_right"), SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("smooth_gray_2x2_panel_top_right"), new BlockItem(SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x2_panel_bottom_left"), SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("smooth_gray_2x2_panel_bottom_left"), new BlockItem(SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("smooth_gray_2x2_panel_bottom_right"), SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("smooth_gray_2x2_panel_bottom_right"), new BlockItem(SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));


            Registry.register(Registry.BLOCK, id("aged_smooth_gray_panel"), AGED_SMOOTH_GRAY_PANEL);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_panel"), new BlockItem(AGED_SMOOTH_GRAY_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_half_panel"), AGED_SMOOTH_GRAY_HALF_PANEL);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_half_panel"), new BlockItem(AGED_SMOOTH_GRAY_HALF_PANEL, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x1_panel_top"), AGED_SMOOTH_GRAY_2X1_PANEL_TOP);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x1_panel_top"), new BlockItem(AGED_SMOOTH_GRAY_2X1_PANEL_TOP, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x1_panel_bottom"), AGED_SMOOTH_GRAY_2X1_PANEL_BOTTOM);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x1_panel_bottom"), new BlockItem(AGED_SMOOTH_GRAY_2X1_PANEL_BOTTOM, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x2_panel_top_left"), AGED_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x2_panel_top_left"), new BlockItem(AGED_SMOOTH_GRAY_2X2_PANEL_TOP_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x2_panel_top_right"), AGED_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x2_panel_top_right"), new BlockItem(AGED_SMOOTH_GRAY_2X2_PANEL_TOP_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x2_panel_bottom_left"), AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x2_panel_bottom_left"), new BlockItem(AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_LEFT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            Registry.register(Registry.BLOCK, id("aged_smooth_gray_2x2_panel_bottom_right"), AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT);
            Registry.register(Registry.ITEM, id("aged_smooth_gray_2x2_panel_bottom_right"), new BlockItem(AGED_SMOOTH_GRAY_2X2_PANEL_BOTTOM_RIGHT, new Item.Settings().group(PortalCubed.PortalCubedGroup)));



            Registry.register(Registry.BLOCK, id("light_cube"), LIGHT_CUBE);

            HLB_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("light_bridge_emitter_entity"), FabricBlockEntityTypeBuilder.create(HardLightBridgeEmitterBlockEntity::new, HLB_EMITTER_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("light_bridge_emitter"), HLB_EMITTER_BLOCK);
            Registry.register(Registry.ITEM, id("light_bridge_emitter"), new BlockItem(HLB_EMITTER_BLOCK, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            HLB_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("light_bridge_entity"), FabricBlockEntityTypeBuilder.create(HardLightBridgeBlockEntity::new, HLB_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("light_bridge"), HLB_BLOCK);
            NEUROTOXIN_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_entity"), FabricBlockEntityTypeBuilder.create(NeurotoxinBlockEntity::new, NEUROTOXIN_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("neurotoxin"), NEUROTOXIN_BLOCK);
            // Registry.register(Registry.ITEM, id("neurotoxin"), new BlockItem(NEUROTOXIN_BLOCK, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            NEUROTOXIN_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_emitter_entity"), FabricBlockEntityTypeBuilder.create(NeurotoxinEmitterBlockEntity::new, NEUROTOXIN_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("neurotoxin_emitter"), NEUROTOXIN_EMITTER);
            Registry.register(Registry.ITEM, id("neurotoxin_emitter"), new BlockItem(NEUROTOXIN_EMITTER, new Item.Settings().group(PortalCubed.PortalCubedGroup)));

            EXCURSION_FUNNEL_EMMITER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_emitter_entity"), FabricBlockEntityTypeBuilder.create(ExcursionFunnelEmitterEntity::new, EXCURSION_FUNNEL_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("excursion_funnel_emitter"), EXCURSION_FUNNEL_EMITTER);
            Registry.register(Registry.ITEM, id("excursion_funnel_emitter"), new BlockItem(EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.PortalCubedGroup)));
            EXCURSION_FUNNEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_entity"), FabricBlockEntityTypeBuilder.create(ExcursionFunnelEntity::new, EXCURSION_FUNNEL).build(null));
            Registry.register(Registry.BLOCK, id("excursion_funnel"), EXCURSION_FUNNEL);
            //STILL_TOXIC_GOO = Registry.register(Registry.FLUID, id("toxic_goo"), new ToxicGooFluid.Still());
            //FLOWING_TOXIC_GOO = Registry.register(Registry.FLUID, id("flowing_toxic_goo"), new ToxicGooFluid.Flowing());
            //TOXIC_GOO_BUCKET = Registry.register(Registry.ITEM, id("toxic_goo_bucket"), new BucketItem(STILL_TOXIC_GOO, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(PortalCubed.PortalCubedGroup)));
            //TOXIC_GOO = Registry.register(Registry.BLOCK, id("toxic_goo"), new CustomFluidBlock(STILL_TOXIC_GOO, FabricBlockSettings.copy(Blocks.WATER)){});

            Registry.register(Registry.BLOCK, id("tall_button"), TALL_BUTTON);
            Registry.register(Registry.ITEM, id("tall_button"), new BlockItem(TALL_BUTTON, new Item.Settings().group(PortalCubed.PortalCubedGroup)));


        }
    }


}
