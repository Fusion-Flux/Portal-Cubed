package com.fusionflux.fluxtech.blocks;

import com.fusionflux.fluxtech.FluxTech;
import com.fusionflux.fluxtech.blocks.blockentities.*;
import com.fusionflux.fluxtech.config.FluxTechConfig2;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluxTechBlocks {

    public static final PropulsionGel PROPULSION_GEL = new PropulsionGel(FabricBlockSettings.of(Material.WATER).hardness(0f).slipperiness(1).nonOpaque().sounds(new BlockSoundGroup(1,-1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final RepulsionGel REPULSION_GEL = new RepulsionGel(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().sounds(new BlockSoundGroup(1,-1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final Gel GEL = new Gel(FabricBlockSettings.of(Material.WATER).hardness(0f).nonOpaque().sounds(new BlockSoundGroup(1,-1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));

    public static final HardLightBridgeEmitterBlock HLB_EMITTER_BLOCK = new HardLightBridgeEmitterBlock(FabricBlockSettings.of(Material.METAL).hardness(3.5f).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static final HardLightBridgeBlock HLB_BLOCK = new HardLightBridgeBlock(FabricBlockSettings.of(Material.PLANT).hardness(999999f).nonOpaque().luminance(10).resistance(9999999999f).sounds(new BlockSoundGroup(1,1, SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, SoundEvents.BLOCK_NETHERITE_BLOCK_STEP, SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, SoundEvents.BLOCK_NETHERITE_BLOCK_HIT, SoundEvents.BLOCK_NETHERITE_BLOCK_FALL)));


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


    public static void registerBlocks() {
        if (FluxTechConfig2.get().enabled.enableGels) {
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "propulsion_gel"), PROPULSION_GEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "propulsion_gel"), new GelBucket(PROPULSION_GEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP).maxCount(1)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "repulsion_gel"), REPULSION_GEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "repulsion_gel"), new GelBucket(REPULSION_GEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP).maxCount(1)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "gel"), GEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "gel"), new GelBucket(GEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP).maxCount(1)));
        }

        if (FluxTechConfig2.get().enabled.enablePortal2Blocks) {
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "smooth_white_panel"), SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "smooth_white_panel"), new BlockItem(SMOOTH_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "chiseled_smooth_white_panel"), CHISELED_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "chiseled_smooth_white_panel"), new BlockItem(CHISELED_SMOOTH_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "top_smooth_white_panel"), TOP_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "top_smooth_white_panel"), new BlockItem(TOP_SMOOTH_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "bottom_smooth_white_panel"), BOTTOM_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "bottom_smooth_white_panel"), new BlockItem(BOTTOM_SMOOTH_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "bottom_2x2_smooth_white_panel"), BOTTOM_2X2_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "bottom_2x2_smooth_white_panel"), new BlockItem(BOTTOM_2X2_SMOOTH_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "top_2x2_smooth_white_panel"), TOP_2X2_SMOOTH_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "top_2x2_smooth_white_panel"), new BlockItem(TOP_2X2_SMOOTH_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));

            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "smooth_grey_panel"), SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "smooth_grey_panel"), new BlockItem(SMOOTH_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "chiseled_smooth_grey_panel"), CHISELED_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "chiseled_smooth_grey_panel"), new BlockItem(CHISELED_SMOOTH_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "top_smooth_grey_panel"), TOP_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "top_smooth_grey_panel"), new BlockItem(TOP_SMOOTH_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "bottom_smooth_grey_panel"), BOTTOM_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "bottom_smooth_grey_panel"), new BlockItem(BOTTOM_SMOOTH_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "bottom_2x2_smooth_grey_panel"), BOTTOM_2X2_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "bottom_2x2_smooth_grey_panel"), new BlockItem(BOTTOM_2X2_SMOOTH_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "top_2x2_smooth_grey_panel"), TOP_2X2_SMOOTH_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "top_2x2_smooth_grey_panel"), new BlockItem(TOP_2X2_SMOOTH_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));

            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "padded_grey_panel"), PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "padded_grey_panel"), new BlockItem(PADDED_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "chiseled_padded_grey_panel"), CHISELED_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "chiseled_padded_grey_panel"), new BlockItem(CHISELED_PADDED_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "top_padded_grey_panel"), TOP_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "top_padded_grey_panel"), new BlockItem(TOP_PADDED_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "bottom_padded_grey_panel"), BOTTOM_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "bottom_padded_grey_panel"), new BlockItem(BOTTOM_PADDED_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "bottom_2x2_padded_grey_panel"), BOTTOM_2X2_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "bottom_2x2_padded_grey_panel"), new BlockItem(BOTTOM_2X2_PADDED_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "top_2x2_padded_grey_panel"), TOP_2X2_PADDED_GREY_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "top_2x2_padded_grey_panel"), new BlockItem(TOP_2X2_PADDED_GREY_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));

            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "gritty_white_panel"), GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "gritty_white_panel"), new BlockItem(GRITTY_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "chiseled_gritty_white_panel"), CHISELED_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "chiseled_gritty_white_panel"), new BlockItem(CHISELED_GRITTY_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "top_gritty_white_panel"), TOP_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "top_gritty_white_panel"), new BlockItem(TOP_GRITTY_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "bottom_gritty_white_panel"), BOTTOM_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "bottom_gritty_white_panel"), new BlockItem(BOTTOM_GRITTY_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "bottom_2x2_gritty_white_panel"), BOTTOM_2X2_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "bottom_2x2_gritty_white_panel"), new BlockItem(BOTTOM_2X2_GRITTY_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
            Registry.register(Registry.BLOCK, new Identifier(FluxTech.MOD_ID, "top_2x2_gritty_white_panel"), TOP_2X2_GRITTY_WHITE_PANEL);
            Registry.register(Registry.ITEM, new Identifier(FluxTech.MOD_ID, "top_2x2_gritty_white_panel"), new BlockItem(TOP_2X2_GRITTY_WHITE_PANEL, new Item.Settings().group(FluxTech.FLUXTECH_GROUP)));
        }
    }
}
