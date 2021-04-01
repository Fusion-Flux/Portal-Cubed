package com.fusionflux.thinkingwithportatos.blocks;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.blocks.blockentities.*;
import com.fusionflux.thinkingwithportatos.config.ThinkingWithPortatosConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;

import static com.fusionflux.thinkingwithportatos.ThinkingWithPortatos.id;

public class ThinkingWithPortatosBlocks {

    public static final PropulsionGel PROPULSION_GEL = new PropulsionGel(FabricBlockSettings.of(Material.WATER).hardness(0f).slipperiness(1).nonOpaque().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final RepulsionGel REPULSION_GEL = new RepulsionGel(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final GelFlat GEL = new GelFlat(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
   // public static final GelFlat GEL_FLAT = new GelFlat(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));


    public static final HardLightBridgeEmitterBlock HLB_EMITTER_BLOCK = new HardLightBridgeEmitterBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static final HardLightBridgeBlock HLB_BLOCK = new HardLightBridgeBlock(FabricBlockSettings.of(Material.PLANT).hardness(999999f).nonOpaque().luminance(10).resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, SoundEvents.BLOCK_NETHERITE_BLOCK_STEP, SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, SoundEvents.BLOCK_NETHERITE_BLOCK_HIT, SoundEvents.BLOCK_NETHERITE_BLOCK_FALL)));

    public static final Block SMOOTH_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block CHISELED_SMOOTH_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock TOP_SMOOTH_WHITE_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock BOTTOM_SMOOTH_WHITE_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock BOTTOM_2X2_SMOOTH_WHITE_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock TOP_2X2_SMOOTH_WHITE_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static final Block SMOOTH_GREY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final Block CHISELED_SMOOTH_GREY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final PillarBlock TOP_SMOOTH_GREY_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final PillarBlock BOTTOM_SMOOTH_GREY_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final DirectionalBlock BOTTOM_2X2_SMOOTH_GREY_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final DirectionalBlock TOP_2X2_SMOOTH_GREY_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));

    public static final Block PADDED_GREY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final Block CHISELED_PADDED_GREY_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final PillarBlock TOP_PADDED_GREY_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final PillarBlock BOTTOM_PADDED_GREY_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final DirectionalBlock BOTTOM_2X2_PADDED_GREY_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));
    public static final DirectionalBlock TOP_2X2_PADDED_GREY_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).sounds(BlockSoundGroup.NETHERITE));

    public static final Block GRITTY_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final Block CHISELED_GRITTY_WHITE_PANEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock TOP_GRITTY_WHITE_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final PillarBlock BOTTOM_GRITTY_WHITE_PANEL = new PillarBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock BOTTOM_2X2_GRITTY_WHITE_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));
    public static final DirectionalBlock TOP_2X2_GRITTY_WHITE_PANEL = new DirectionalBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f));

    public static BlockEntityType<HardLightBridgeEmitterBlockEntity> HLB_EMITTER_ENTITY;
    public static BlockEntityType<HardLightBridgeBlockEntity> HLB_BLOCK_ENTITY;

    public static final NeurotoxinBlock NEUROTOXIN_BLOCK = new NeurotoxinBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static BlockEntityType<NeurotoxinBlockEntity> NEUROTOXIN_BLOCK_ENTITY;

    public static void registerBlocks() {

        NEUROTOXIN_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_entity"), BlockEntityType.Builder.create(NeurotoxinBlockEntity::new, NEUROTOXIN_BLOCK).build(null));
        Registry.register(Registry.BLOCK, id("neurotoxin"), NEUROTOXIN_BLOCK);
        Registry.register(Registry.ITEM, id("neurotoxin"), new BlockItem(NEUROTOXIN_BLOCK, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup)));

        if (ThinkingWithPortatosConfig.get().enabled.enableGels) {
            Registry.register(Registry.BLOCK, id("propulsion_gel"), PROPULSION_GEL);
            Registry.register(Registry.ITEM, id("propulsion_gel"), new GelBucket(PROPULSION_GEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup).maxCount(1)));
            Registry.register(Registry.BLOCK, id("repulsion_gel"), REPULSION_GEL);
            Registry.register(Registry.ITEM, id("repulsion_gel"), new GelBucket(REPULSION_GEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup).maxCount(1)));
            Registry.register(Registry.BLOCK, id("gel"), GEL);
            Registry.register(Registry.ITEM, id("gel"), new GelBucket(GEL, new Item.Settings().group(ThinkingWithPortatos.ThinkingWithPortatosGroup).maxCount(1)));
        }

        if (ThinkingWithPortatosConfig.get().enabled.enablePortal2Blocks) {
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
        }

    }

    @Environment(EnvType.CLIENT)
    public static void registerRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.HLB_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.HLB_EMITTER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK, RenderLayer.getTranslucent());
    }
}
