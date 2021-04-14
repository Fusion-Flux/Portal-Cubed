package com.fusionflux.thinkingwithportatos.blocks;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.blocks.blockentities.*;
import com.fusionflux.thinkingwithportatos.config.ThinkingWithPortatosConfig;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.fusionflux.thinkingwithportatos.ThinkingWithPortatos.id;

public class ThinkingWithPortatosBlocks {

    public static final PropulsionGel PROPULSION_GEL = new PropulsionGel(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final RepulsionGel REPULSION_GEL = new RepulsionGel(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final GelFlat GEL = new GelFlat(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    // public static final GelFlat GEL_FLAT = new GelFlat(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));

    public static final HardLightBridgeEmitterBlock HLB_EMITTER_BLOCK = new HardLightBridgeEmitterBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static final HardLightBridgeBlock HLB_BLOCK = new HardLightBridgeBlock(FabricBlockSettings.of(Material.PLANT).hardness(999999f).nonOpaque().luminance(10).resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_METAL_BREAK, SoundEvents.BLOCK_METAL_STEP, SoundEvents.BLOCK_METAL_PLACE, SoundEvents.BLOCK_METAL_HIT, SoundEvents.BLOCK_METAL_FALL)));

    public static final Block SMOOTH_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block CHISELED_SMOOTH_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock TOP_SMOOTH_WHITE_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock BOTTOM_SMOOTH_WHITE_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock BOTTOM_2X2_SMOOTH_WHITE_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock TOP_2X2_SMOOTH_WHITE_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static final Block SMOOTH_GREY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block CHISELED_SMOOTH_GREY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock TOP_SMOOTH_GREY_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock BOTTOM_SMOOTH_GREY_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock BOTTOM_2X2_SMOOTH_GREY_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock TOP_2X2_SMOOTH_GREY_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final SlidingDoorBlock SLIDINGDOOR = new SlidingDoorBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static final Block PADDED_GREY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block CHISELED_PADDED_GREY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock TOP_PADDED_GREY_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock BOTTOM_PADDED_GREY_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock BOTTOM_2X2_PADDED_GREY_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock TOP_2X2_PADDED_GREY_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static final Block GRITTY_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block CHISELED_GRITTY_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock TOP_GRITTY_WHITE_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock BOTTOM_GRITTY_WHITE_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock BOTTOM_2X2_GRITTY_WHITE_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock TOP_2X2_GRITTY_WHITE_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final NeurotoxinBlock NEUROTOXIN_BLOCK = new NeurotoxinBlock(FabricBlockSettings.of(Material.AIR).hardness(3.5f).nonOpaque().noCollision().sounds(BlockSoundGroup.METAL));
    public static final NeurotoxinEmitterBlock NEUROTOXIN_EMITTER = new NeurotoxinEmitterBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().noCollision().sounds(BlockSoundGroup.METAL));
    public static final ExcursionFunnelEmitter EXCURSION_FUNNEL_EMITTER = new ExcursionFunnelEmitter(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static final ExcursionFunnel EXCURSION_FUNNEL = new ExcursionFunnel(FabricBlockSettings.of(Material.AIR).nonOpaque().noCollision());
    public static BlockEntityType<HardLightBridgeEmitterBlockEntity> HLB_EMITTER_ENTITY;
    public static BlockEntityType<HardLightBridgeBlockEntity> HLB_BLOCK_ENTITY;
    public static BlockEntityType<NeurotoxinBlockEntity> NEUROTOXIN_BLOCK_ENTITY;
    public static BlockEntityType<NeurotoxinEmitterBlockEntity> NEUROTOXIN_EMITTER_ENTITY;
    public static BlockEntityType<ExcursionFunnelEmitterEntity> EXCURSION_FUNNEL_EMMITER_ENTITY;
    public static BlockEntityType<ExcursionFunnelEntity> EXCURSION_FUNNEL_ENTITY;

    public static Tag<Block> MY_TAG = TagRegistry.block(new Identifier("thinkingwithportatos", "hpd_deny_launch"));

    public static void registerBlocks() {
        if (ThinkingWithPortatosConfig.get().enabled.enableGels) {
            Registry.register(Registry.BLOCK, id("propulsion_gel"), PROPULSION_GEL);
            Registry.register(Registry.ITEM, id("propulsion_gel"), new GelBucket(PROPULSION_GEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup).maxCount(1)));
            Registry.register(Registry.BLOCK, id("repulsion_gel"), REPULSION_GEL);
            Registry.register(Registry.ITEM, id("repulsion_gel"), new GelBucket(REPULSION_GEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup).maxCount(1)));
            Registry.register(Registry.BLOCK, id("gel"), GEL);
            Registry.register(Registry.ITEM, id("gel"), new GelBucket(GEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup).maxCount(1)));
        }

        if (ThinkingWithPortatosConfig.get().enabled.enablePortal2Blocks) {
            Registry.register(Registry.BLOCK, id("sliding_door"), SLIDINGDOOR);
            Registry.register(Registry.ITEM, id("sliding_door"), new BlockItem(SLIDINGDOOR, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));

            Registry.register(Registry.BLOCK, id("smooth_white_panel"), SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("smooth_white_panel"), new BlockItem(SMOOTH_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("chiseled_smooth_white_panel"), CHISELED_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("chiseled_smooth_white_panel"), new BlockItem(CHISELED_SMOOTH_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("top_smooth_white_panel"), TOP_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("top_smooth_white_panel"), new BlockItem(TOP_SMOOTH_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("bottom_smooth_white_panel"), BOTTOM_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("bottom_smooth_white_panel"), new BlockItem(BOTTOM_SMOOTH_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("bottom_2x2_smooth_white_panel"), BOTTOM_2X2_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("bottom_2x2_smooth_white_panel"), new BlockItem(BOTTOM_2X2_SMOOTH_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("top_2x2_smooth_white_panel"), TOP_2X2_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("top_2x2_smooth_white_panel"), new BlockItem(TOP_2X2_SMOOTH_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));

            Registry.register(Registry.BLOCK, id("smooth_grey_panel"), SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, id("smooth_grey_panel"), new BlockItem(SMOOTH_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("chiseled_smooth_grey_panel"), CHISELED_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, id("chiseled_smooth_grey_panel"), new BlockItem(CHISELED_SMOOTH_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("top_smooth_grey_panel"), TOP_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, id("top_smooth_grey_panel"), new BlockItem(TOP_SMOOTH_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("bottom_smooth_grey_panel"), BOTTOM_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, id("bottom_smooth_grey_panel"), new BlockItem(BOTTOM_SMOOTH_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("bottom_2x2_smooth_grey_panel"), BOTTOM_2X2_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, id("bottom_2x2_smooth_grey_panel"), new BlockItem(BOTTOM_2X2_SMOOTH_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("top_2x2_smooth_grey_panel"), TOP_2X2_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, id("top_2x2_smooth_grey_panel"), new BlockItem(TOP_2X2_SMOOTH_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));

            Registry.register(Registry.BLOCK, id("padded_grey_panel"), PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, id("padded_grey_panel"), new BlockItem(PADDED_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("chiseled_padded_grey_panel"), CHISELED_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, id("chiseled_padded_grey_panel"), new BlockItem(CHISELED_PADDED_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("top_padded_grey_panel"), TOP_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, id("top_padded_grey_panel"), new BlockItem(TOP_PADDED_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("bottom_padded_grey_panel"), BOTTOM_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, id("bottom_padded_grey_panel"), new BlockItem(BOTTOM_PADDED_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("bottom_2x2_padded_grey_panel"), BOTTOM_2X2_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, id("bottom_2x2_padded_grey_panel"), new BlockItem(BOTTOM_2X2_PADDED_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("top_2x2_padded_grey_panel"), TOP_2X2_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, id("top_2x2_padded_grey_panel"), new BlockItem(TOP_2X2_PADDED_GREY_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));

            Registry.register(Registry.BLOCK, id("gritty_white_panel"), GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("gritty_white_panel"), new BlockItem(GRITTY_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("chiseled_gritty_white_panel"), CHISELED_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("chiseled_gritty_white_panel"), new BlockItem(CHISELED_GRITTY_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("top_gritty_white_panel"), TOP_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("top_gritty_white_panel"), new BlockItem(TOP_GRITTY_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("bottom_gritty_white_panel"), BOTTOM_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("bottom_gritty_white_panel"), new BlockItem(BOTTOM_GRITTY_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("bottom_2x2_gritty_white_panel"), BOTTOM_2X2_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("bottom_2x2_gritty_white_panel"), new BlockItem(BOTTOM_2X2_GRITTY_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            Registry.register(Registry.BLOCK, id("top_2x2_gritty_white_panel"), TOP_2X2_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, id("top_2x2_gritty_white_panel"), new BlockItem(TOP_2X2_GRITTY_WHITE_PANEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));

            HLB_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("emitter_test_entity"), BlockEntityType.Builder.create(HardLightBridgeEmitterBlockEntity::new, HLB_EMITTER_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("emitter"), HLB_EMITTER_BLOCK);
            Registry.register(Registry.ITEM, id("emitter"), new BlockItem(HLB_EMITTER_BLOCK, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            HLB_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("bridge_test_entity"), BlockEntityType.Builder.create(HardLightBridgeBlockEntity::new, HLB_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("bridge_test"), HLB_BLOCK);
            NEUROTOXIN_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_entity"), BlockEntityType.Builder.create(NeurotoxinBlockEntity::new, NEUROTOXIN_BLOCK).build(null));
            Registry.register(Registry.BLOCK, id("neurotoxin"), NEUROTOXIN_BLOCK);
            // Registry.register(Registry.ITEM, id("neurotoxin"), new BlockItem(NEUROTOXIN_BLOCK, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            NEUROTOXIN_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_emitter_entity"), BlockEntityType.Builder.create(NeurotoxinEmitterBlockEntity::new, NEUROTOXIN_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("neurotoxin_emitter"), NEUROTOXIN_EMITTER);
            Registry.register(Registry.ITEM, id("neurotoxin_emitter"), new BlockItem(NEUROTOXIN_EMITTER, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));

            EXCURSION_FUNNEL_EMMITER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_emitter_entity"), BlockEntityType.Builder.create(ExcursionFunnelEmitterEntity::new, EXCURSION_FUNNEL_EMITTER).build(null));
            Registry.register(Registry.BLOCK, id("excursion_funnel_emitter"), EXCURSION_FUNNEL_EMITTER);
            Registry.register(Registry.ITEM, id("excursion_funnel_emitter"), new BlockItem(EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));
            EXCURSION_FUNNEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_entity"), BlockEntityType.Builder.create(ExcursionFunnelEntity::new, EXCURSION_FUNNEL).build(null));
            Registry.register(Registry.BLOCK, id("excursion_funnel"), EXCURSION_FUNNEL);
        }
    }
}
