package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.blockentities.*;
import com.fusionflux.portalcubed.blocks.fizzler.*;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.GelBlobItem;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
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

    public static final HardLightBridgeEmitterBlock HLB_EMITTER_BLOCK = new HardLightBridgeEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final HardLightBridgeBlock HLB_BLOCK = new HardLightBridgeBlock(QuiltBlockSettings.of(Material.AIR).hardness(999999f).nonOpaque().resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL)));

    public static final AutoPortalBlock AUTO_PORTAL_BLOCK = new AutoPortalBlock(
        QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE)
    );

    public static final NeurotoxinBlock NEUROTOXIN_BLOCK = new NeurotoxinBlock(QuiltBlockSettings.of(new QuiltMaterialBuilder(MapColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().replaceable().burnable().build()).nonOpaque().noCollision());
    public static final NeurotoxinEmitterBlock NEUROTOXIN_EMITTER = new NeurotoxinEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().nonOpaque().noCollision().sounds(BlockSoundGroup.STONE));
    public static final ExcursionFunnelEmitter EXCURSION_FUNNEL_EMITTER = new ExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final DuelExcursionFunnelEmitter DUEL_EXCURSION_FUNNEL_EMITTER = new DuelExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final ReversedExcursionFunnelEmitter REVERSED_EXCURSION_FUNNEL_EMITTER = new ReversedExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final ExcursionFunnelMain EXCURSION_FUNNEL = new ExcursionFunnelMain(QuiltBlockSettings.of(Material.AIR).nonOpaque().noCollision());

    public static final TallButton TALL_BUTTON = new TallButton(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final TallButton OLD_AP_PEDESTAL_BUTTON = new TallButton(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());

    public static final SlidingDoorBlock PORTAL2DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f,3.5f).requiresTool());
    public static final SlidingDoorBlock OLD_AP_DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool());
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
    public static BlockEntityType<ExcursionFunnelEmitterEntity> EXCURSION_FUNNEL_EMITTER_ENTITY;
    public static BlockEntityType<ReversedExcursionFunnelEmitterEntity> REVERSED_EXCURSION_FUNNEL_EMITTER_ENTITY;
    public static BlockEntityType<DuelExcursionFunnelEmitterEntity> DUEL_EXCURSION_FUNNEL_EMITTER_ENTITY;
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


    public static TagKey<Block> CANT_PLACE_PORTAL_ON = TagKey.of(Registry.BLOCK_KEY,new Identifier("portalcubed", "cant_place_portal_on"));
    public static TagKey<Block> GEL_CHECK_TAG = TagKey.of(Registry.BLOCK_KEY, new Identifier("portalcubed", "gelchecktag"));
    public static TagKey<Block> ALLOW_PORTAL_IN = TagKey.of(Registry.BLOCK_KEY,new Identifier("portalcubed", "allowinside"));

    public static void registerBlocks() {
        Registry.register(Registry.ITEM, new Identifier(PortalCubed.MOD_ID, "base_gel"), BASE_GEL);

        Registry.register(Registry.BLOCK, id("propulsion_gel"), PROPULSION_GEL);
        Registry.register(Registry.ITEM, id("propulsion_gel"), new GelBlobItem(PROPULSION_GEL, PortalCubedEntities.PROPULSION_GEL_BLOB, new Item.Settings().group(PortalCubed.TestingElementsGroup).maxCount(64)));
        Registry.register(Registry.BLOCK, id("repulsion_gel"), REPULSION_GEL);
        Registry.register(Registry.ITEM, id("repulsion_gel"), new GelBlobItem(REPULSION_GEL, PortalCubedEntities.REPULSION_GEL_BLOB, new Item.Settings().group(PortalCubed.TestingElementsGroup).maxCount(64)));

        Registry.register(Registry.BLOCK, id("adhesion_gel"), ADHESION_GEL);
        Registry.register(Registry.ITEM, id("adhesion_gel"), new GelBlobItem(ADHESION_GEL, PortalCubedEntities.ADHESION_GEL_BLOB, new Item.Settings().group(PortalCubed.TestingElementsGroup).maxCount(64)));

        Registry.register(Registry.BLOCK, id("conversion_gel"), CONVERSION_GEL);
        Registry.register(Registry.ITEM, id("conversion_gel"), new GelBlobItem(CONVERSION_GEL, PortalCubedEntities.CONVERSION_GEL_BLOB, new Item.Settings().group(PortalCubed.TestingElementsGroup).maxCount(64)));

        Registry.register(Registry.BLOCK, id("portal_2_door"), PORTAL2DOOR);
        Registry.register(Registry.ITEM, id("portal_2_door"), new BlockItem(PORTAL2DOOR, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("old_ap_door"), OLD_AP_DOOR);
        Registry.register(Registry.ITEM, id("old_ap_door"), new BlockItem(OLD_AP_DOOR, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("portal_1_door"), PORTAL1DOOR);
        Registry.register(Registry.ITEM, id("portal_1_door"), new BlockItem(PORTAL1DOOR, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        HLB_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("light_bridge_emitter_entity"), QuiltBlockEntityTypeBuilder.create(HardLightBridgeEmitterBlockEntity::new, HLB_EMITTER_BLOCK).build(null));
        Registry.register(Registry.BLOCK, id("light_bridge_emitter"), HLB_EMITTER_BLOCK);
        Registry.register(Registry.ITEM, id("light_bridge_emitter"), new BlockItem(HLB_EMITTER_BLOCK, new Item.Settings().group(PortalCubed.TestingElementsGroup)));
        HLB_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("light_bridge_entity"), QuiltBlockEntityTypeBuilder.create(HardLightBridgeBlockEntity::new, HLB_BLOCK).build(null));
        Registry.register(Registry.BLOCK, id("light_bridge"), HLB_BLOCK);
        NEUROTOXIN_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_entity"), QuiltBlockEntityTypeBuilder.create(NeurotoxinBlockEntity::new, NEUROTOXIN_BLOCK).build(null));
        Registry.register(Registry.BLOCK, id("neurotoxin"), NEUROTOXIN_BLOCK);
        NEUROTOXIN_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_emitter_entity"), QuiltBlockEntityTypeBuilder.create(NeurotoxinEmitterBlockEntity::new, NEUROTOXIN_EMITTER).build(null));
        Registry.register(Registry.BLOCK, id("neurotoxin_emitter"), NEUROTOXIN_EMITTER);
        Registry.register(Registry.ITEM, id("neurotoxin_emitter"), new BlockItem(NEUROTOXIN_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        AUTO_PORTAL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("auto_portal_entity"), QuiltBlockEntityTypeBuilder.create(AutoPortalBlockEntity::new, AUTO_PORTAL_BLOCK).build());
        Registry.register(Registry.BLOCK, id("auto_portal"), AUTO_PORTAL_BLOCK);
        Registry.register(Registry.ITEM, id("auto_portal"), new BlockItem(AUTO_PORTAL_BLOCK, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        EXCURSION_FUNNEL_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_emitter_entity"), QuiltBlockEntityTypeBuilder.create(ExcursionFunnelEmitterEntity::new, EXCURSION_FUNNEL_EMITTER).build(null));
        Registry.register(Registry.BLOCK, id("excursion_funnel_emitter"), EXCURSION_FUNNEL_EMITTER);
        Registry.register(Registry.ITEM, id("excursion_funnel_emitter"), new BlockItem(EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));
        EXCURSION_FUNNEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_entity"), QuiltBlockEntityTypeBuilder.create(ExcursionFunnelEntityMain::new, EXCURSION_FUNNEL).build(null));
        Registry.register(Registry.BLOCK, id("excursion_funnel"), EXCURSION_FUNNEL);

        REVERSED_EXCURSION_FUNNEL_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("reversed_excursion_funnel_emitter_entity"), QuiltBlockEntityTypeBuilder.create(ReversedExcursionFunnelEmitterEntity::new, REVERSED_EXCURSION_FUNNEL_EMITTER).build(null));
        Registry.register(Registry.BLOCK, id("reversed_excursion_funnel_emitter"), REVERSED_EXCURSION_FUNNEL_EMITTER);
        Registry.register(Registry.ITEM, id("reversed_excursion_funnel_emitter"), new BlockItem(REVERSED_EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        DUEL_EXCURSION_FUNNEL_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("duel_excursion_funnel_emitter_entity"), QuiltBlockEntityTypeBuilder.create(DuelExcursionFunnelEmitterEntity::new, DUEL_EXCURSION_FUNNEL_EMITTER).build(null));
        Registry.register(Registry.BLOCK, id("duel_excursion_funnel_emitter"), DUEL_EXCURSION_FUNNEL_EMITTER);
        Registry.register(Registry.ITEM, id("duel_excursion_funnel_emitter"), new BlockItem(DUEL_EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        FAITH_PLATE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("faith_plate_entity"), QuiltBlockEntityTypeBuilder.create(FaithPlateBlockEntity::new, FAITH_PLATE).build(null));
        Registry.register(Registry.BLOCK, id("faith_plate"), FAITH_PLATE);
        Registry.register(Registry.ITEM, id("faith_plate"), new BlockItem(FAITH_PLATE, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        BETA_FAITH_PLATE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("beta_faith_plate_entity"), QuiltBlockEntityTypeBuilder.create(BetaFaithPlateBlockEntity::new, BETA_FAITH_PLATE).build(null));
        Registry.register(Registry.BLOCK, id("beta_faith_plate"), BETA_FAITH_PLATE);
        Registry.register(Registry.ITEM, id("beta_faith_plate"), new BlockItem(BETA_FAITH_PLATE, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        LASER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_entity"), QuiltBlockEntityTypeBuilder.create(LaserBlockEntity::new, LASER).build(null));
        Registry.register(Registry.BLOCK, id("laser"), LASER);

        LASER_EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_emitter_entity"), QuiltBlockEntityTypeBuilder.create(LaserEmitterEntity::new, LASER_EMITTER).build(null));
        Registry.register(Registry.BLOCK, id("laser_emitter"), LASER_EMITTER);
        Registry.register(Registry.ITEM, id("laser_emitter"), new BlockItem(LASER_EMITTER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        LASER_CATCHER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_catcher_entity"), QuiltBlockEntityTypeBuilder.create(LaserCatcherEntity::new, LASER_CATCHER).build(null));
        Registry.register(Registry.BLOCK, id("laser_catcher"), LASER_CATCHER);
        Registry.register(Registry.ITEM, id("laser_catcher"), new BlockItem(LASER_CATCHER, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        LASER_RELAY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_relay_entity"), QuiltBlockEntityTypeBuilder.create(LaserRelayEntity::new, LASER_RELAY).build(null));
        Registry.register(Registry.BLOCK, id("laser_relay"), LASER_RELAY);
        Registry.register(Registry.ITEM, id("laser_relay"), new BlockItem(LASER_RELAY, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        FLOOR_BUTTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("floor_button_block_entity"), QuiltBlockEntityTypeBuilder.create(FloorButtonBlockEntity::new, FLOOR_BUTTON).build(null));
        Registry.register(Registry.BLOCK, id("floor_button"), FLOOR_BUTTON);
        Registry.register(Registry.ITEM, id("floor_button"), new BlockItem(FLOOR_BUTTON, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("old_ap_floor_button_block_entity"), QuiltBlockEntityTypeBuilder.create(OldApFloorButtonBlockEntity::new, OLD_AP_FLOOR_BUTTON).build(null));
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

        Registry.register(Registry.BLOCK, id("pedestal_button"), TALL_BUTTON);
        Registry.register(Registry.ITEM, id("pedestal_button"), new BlockItem(TALL_BUTTON, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        Registry.register(Registry.BLOCK, id("old_ap_pedestal_button"), OLD_AP_PEDESTAL_BUTTON);
        Registry.register(Registry.ITEM, id("old_ap_pedestal_button"), new BlockItem(OLD_AP_PEDESTAL_BUTTON, new Item.Settings().group(PortalCubed.TestingElementsGroup)));

        PortalBlocksLoader.initCommon();
    }


}
