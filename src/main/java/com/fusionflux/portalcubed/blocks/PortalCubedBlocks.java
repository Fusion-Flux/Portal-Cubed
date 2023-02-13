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
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.block.extensions.api.QuiltMaterialBuilder;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedBlocks {
    public static final Item BASE_GEL = new Item(new QuiltItemSettings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64).fireproof());
    public static final PropulsionGel PROPULSION_GEL = new PropulsionGel(QuiltBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final RepulsionGel REPULSION_GEL = new RepulsionGel(QuiltBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final GelFlat CONVERSION_GEL = new GelFlat(QuiltBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));
    public static final AdhesionGel ADHESION_GEL = new AdhesionGel(QuiltBlockSettings.of(Material.PLANT).ticksRandomly().hardness(0f).nonOpaque().noCollision().sounds(new BlockSoundGroup(1, -1, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL)));

    public static final HardLightBridgeEmitterBlock HLB_EMITTER_BLOCK = new HardLightBridgeEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final HardLightBridgeBlock HLB_BLOCK = new HardLightBridgeBlock(QuiltBlockSettings.of(Material.AIR).hardness(999999f).nonOpaque().resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL)));

    public static final AutoPortalBlock AUTO_PORTAL_BLOCK = new AutoPortalBlock(
        QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().sounds(BlockSoundGroup.STONE)
    );

    public static final NeurotoxinBlock NEUROTOXIN_BLOCK = new NeurotoxinBlock(QuiltBlockSettings.of(new QuiltMaterialBuilder(MapColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().replaceable().burnable().build()).nonOpaque().noCollision());
    public static final NeurotoxinEmitterBlock NEUROTOXIN_EMITTER = new NeurotoxinEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().nonOpaque().noCollision().sounds(BlockSoundGroup.STONE));
    public static final ExcursionFunnelEmitter EXCURSION_FUNNEL_EMITTER = new ExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final DuelExcursionFunnelEmitter DUEL_EXCURSION_FUNNEL_EMITTER = new DuelExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final ReversedExcursionFunnelEmitter REVERSED_EXCURSION_FUNNEL_EMITTER = new ReversedExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final ExcursionFunnelMain EXCURSION_FUNNEL = new ExcursionFunnelMain(QuiltBlockSettings.of(Material.AIR).nonOpaque().noCollision());

    public static final TallButton TALL_BUTTON = new TallButton(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool());
    public static final TallButton OLD_AP_PEDESTAL_BUTTON = new TallButton(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool());

    public static final SlidingDoorBlock PORTAL2DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool());
    public static final SlidingDoorBlock OCTOPUS_DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool());
    public static final SlidingDoorBlock OLD_AP_DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool());
    public static final SlidingDoorBlock PORTAL1DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool());

    public static final BlockEntityType<HardLightBridgeEmitterBlockEntity> HLB_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(HardLightBridgeEmitterBlockEntity::new, HLB_EMITTER_BLOCK).build();
    public static final BlockEntityType<HardLightBridgeBlockEntity> HLB_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(HardLightBridgeBlockEntity::new, HLB_BLOCK).build();

    public static final BlockEntityType<AutoPortalBlockEntity> AUTO_PORTAL_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(AutoPortalBlockEntity::new, AUTO_PORTAL_BLOCK).build();

    public static final FaithPlateBlock FAITH_PLATE = new FaithPlateBlock(QuiltBlockSettings.of(Material.STONE).hardness(999999f).resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL)));
    public static final BlockEntityType<FaithPlateBlockEntity> FAITH_PLATE_ENTITY = QuiltBlockEntityTypeBuilder.create(FaithPlateBlockEntity::new, FAITH_PLATE).build();

    public static final BetaFaithPlateBlock BETA_FAITH_PLATE = new BetaFaithPlateBlock(QuiltBlockSettings.of(Material.STONE).hardness(999999f).resistance(9999999999f).sounds(new BlockSoundGroup(1, 1, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL)));
    public static final BlockEntityType<BetaFaithPlateBlockEntity> BETA_FAITH_PLATE_ENTITY = QuiltBlockEntityTypeBuilder.create(BetaFaithPlateBlockEntity::new, BETA_FAITH_PLATE).build();

    public static final BlockEntityType<NeurotoxinBlockEntity> NEUROTOXIN_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(NeurotoxinBlockEntity::new, NEUROTOXIN_BLOCK).build();
    public static final BlockEntityType<NeurotoxinEmitterBlockEntity> NEUROTOXIN_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(NeurotoxinEmitterBlockEntity::new, NEUROTOXIN_EMITTER).build();
    public static final BlockEntityType<ExcursionFunnelEmitterBlockEntity> EXCURSION_FUNNEL_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(ExcursionFunnelEmitterBlockEntity::new, EXCURSION_FUNNEL_EMITTER).build();
    public static final BlockEntityType<ReversedExcursionFunnelEmitterBlockEntity> REVERSED_EXCURSION_FUNNEL_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(ReversedExcursionFunnelEmitterBlockEntity::new, REVERSED_EXCURSION_FUNNEL_EMITTER).build();
    public static final BlockEntityType<DualExcursionFunnelEmitterBlockEntity> DUAL_EXCURSION_FUNNEL_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(DualExcursionFunnelEmitterBlockEntity::new, DUEL_EXCURSION_FUNNEL_EMITTER).build();
    public static final BlockEntityType<ExcursionFunnelMainBlockEntity> EXCURSION_FUNNEL_ENTITY = QuiltBlockEntityTypeBuilder.create(ExcursionFunnelMainBlockEntity::new, EXCURSION_FUNNEL).build();

    public static final PowerBlock POWER_BLOCK = new PowerBlock(QuiltBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque());

    public static final Block VELOCITY_HELPER = new VelocityHelperBlock(QuiltBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque());
    public static final BlockEntityType<VelocityHelperBlockEntity> VELOCITY_HELPER_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(VelocityHelperBlockEntity::new, VELOCITY_HELPER).build();

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
    public static final DeathFizzlerBlock OLD_APERTURE_DEATH_FIZZLER = new DeathFizzlerBlock(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter OLD_APERTURE_DEATH_FIZZLER_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), OLD_APERTURE_DEATH_FIZZLER);
    public static final MatterInquisitionField MATTER_INQUISITION_FIELD = new MatterInquisitionField(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter MATTER_INQUISITION_FIELD_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), MATTER_INQUISITION_FIELD);
    public static final PhysicsRepulsionField PHYSICS_REPULSION_FIELD = new PhysicsRepulsionField(QuiltBlockSettings.copyOf(FIZZLER));
    public static final FizzlerEmitter PHYSICS_REPULSION_FIELD_EMITTER = new FizzlerEmitter(QuiltBlockSettings.copyOf(FIZZLER_EMITTER), PHYSICS_REPULSION_FIELD);

    public static final LaserBlock LASER = new LaserBlock(QuiltBlockSettings.of(Material.AIR).nonOpaque().noCollision());
    public static final BlockEntityType<LaserBlockEntity> LASER_ENTITY = QuiltBlockEntityTypeBuilder.create(LaserBlockEntity::new, LASER).build();

    public static final LaserEmitter LASER_EMITTER = new LaserEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().nonOpaque().sounds(BlockSoundGroup.STONE));
    public static final BlockEntityType<LaserEmitterBlockEntity> LASER_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(LaserEmitterBlockEntity::new, LASER_EMITTER).build();

    public static final LaserCatcherBlock LASER_CATCHER = new LaserCatcherBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().nonOpaque().sounds(BlockSoundGroup.STONE));
    public static final BlockEntityType<LaserCatcherBlockEntity> LASER_CATCHER_ENTITY = QuiltBlockEntityTypeBuilder.create(LaserCatcherBlockEntity::new, LASER_CATCHER).build();

    public static final LaserRelayBlock LASER_RELAY = new LaserRelayBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().nonOpaque().sounds(BlockSoundGroup.STONE));
    public static final BlockEntityType<LaserRelayBlockEntity> LASER_RELAY_ENTITY = QuiltBlockEntityTypeBuilder.create(LaserRelayBlockEntity::new, LASER_RELAY).build();

    public static final FloorButtonBlock FLOOR_BUTTON = new FloorButtonBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final BlockEntityType<FloorButtonBlockEntity> FLOOR_BUTTON_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(FloorButtonBlockEntity::new, FLOOR_BUTTON).build();

    public static final OldApFloorButtonBlock OLD_AP_FLOOR_BUTTON = new OldApFloorButtonBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final BlockEntityType<OldApFloorButtonBlockEntity> OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(OldApFloorButtonBlockEntity::new, OLD_AP_FLOOR_BUTTON).build();

    public static final RocketTurretBlock ROCKET_TURRET = new RocketTurretBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final BlockEntityType<RocketTurretBlockEntity> ROCKET_TURRET_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(RocketTurretBlockEntity::new, ROCKET_TURRET).build();

    public static final TagKey<Block> CANT_PLACE_PORTAL_ON = TagKey.of(Registry.BLOCK_KEY, id("cant_place_portal_on"));
    public static final TagKey<Block> PORTAL_NONSOLID = TagKey.of(Registry.BLOCK_KEY, id("portal_nonsolid"));
    public static final TagKey<Block> PORTAL_SOLID = TagKey.of(Registry.BLOCK_KEY, id("portal_solid"));
    public static final TagKey<Block> PORTALABLE_GELS = TagKey.of(Registry.BLOCK_KEY, id("portalable_gels"));

    public static void registerBlocks() {
        Registry.register(Registry.ITEM, id("base_gel"), BASE_GEL);

        Registry.register(Registry.BLOCK, id("propulsion_gel"), PROPULSION_GEL);
        Registry.register(Registry.ITEM, id("propulsion_gel"), new GelBlobItem(PROPULSION_GEL, PortalCubedEntities.PROPULSION_GEL_BLOB, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64)));
        Registry.register(Registry.BLOCK, id("repulsion_gel"), REPULSION_GEL);
        Registry.register(Registry.ITEM, id("repulsion_gel"), new GelBlobItem(REPULSION_GEL, PortalCubedEntities.REPULSION_GEL_BLOB, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64)));

        Registry.register(Registry.BLOCK, id("adhesion_gel"), ADHESION_GEL);
        Registry.register(Registry.ITEM, id("adhesion_gel"), new GelBlobItem(ADHESION_GEL, PortalCubedEntities.ADHESION_GEL_BLOB, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64)));

        Registry.register(Registry.BLOCK, id("conversion_gel"), CONVERSION_GEL);
        Registry.register(Registry.ITEM, id("conversion_gel"), new GelBlobItem(CONVERSION_GEL, PortalCubedEntities.CONVERSION_GEL_BLOB, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP).maxCount(64)));

        Registry.register(Registry.BLOCK, id("portal_2_door"), PORTAL2DOOR);
        Registry.register(Registry.ITEM, id("portal_2_door"), new BlockItem(PORTAL2DOOR, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("octopus_door"), OCTOPUS_DOOR);
        Registry.register(Registry.ITEM, id("octopus_door"), new BlockItem(OCTOPUS_DOOR, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("old_ap_door"), OLD_AP_DOOR);
        Registry.register(Registry.ITEM, id("old_ap_door"), new BlockItem(OLD_AP_DOOR, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("portal_1_door"), PORTAL1DOOR);
        Registry.register(Registry.ITEM, id("portal_1_door"), new BlockItem(PORTAL1DOOR, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("light_bridge_emitter_entity"), HLB_EMITTER_ENTITY);
        Registry.register(Registry.BLOCK, id("light_bridge_emitter"), HLB_EMITTER_BLOCK);
        Registry.register(Registry.ITEM, id("light_bridge_emitter"), new BlockItem(HLB_EMITTER_BLOCK, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("light_bridge_entity"), HLB_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK, id("light_bridge"), HLB_BLOCK);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_entity"), NEUROTOXIN_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK, id("neurotoxin"), NEUROTOXIN_BLOCK);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("neurotoxin_emitter_entity"), NEUROTOXIN_EMITTER_ENTITY);
        Registry.register(Registry.BLOCK, id("neurotoxin_emitter"), NEUROTOXIN_EMITTER);
        Registry.register(Registry.ITEM, id("neurotoxin_emitter"), new BlockItem(NEUROTOXIN_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("auto_portal_entity"), AUTO_PORTAL_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK, id("auto_portal"), AUTO_PORTAL_BLOCK);
        Registry.register(Registry.ITEM, id("auto_portal"), new BlockItem(AUTO_PORTAL_BLOCK, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_emitter_entity"), EXCURSION_FUNNEL_EMITTER_ENTITY);
        Registry.register(Registry.BLOCK, id("excursion_funnel_emitter"), EXCURSION_FUNNEL_EMITTER);
        Registry.register(Registry.ITEM, id("excursion_funnel_emitter"), new BlockItem(EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("excursion_funnel_entity"), EXCURSION_FUNNEL_ENTITY);
        Registry.register(Registry.BLOCK, id("excursion_funnel"), EXCURSION_FUNNEL);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("reversed_excursion_funnel_emitter_entity"), REVERSED_EXCURSION_FUNNEL_EMITTER_ENTITY);
        Registry.register(Registry.BLOCK, id("reversed_excursion_funnel_emitter"), REVERSED_EXCURSION_FUNNEL_EMITTER);
        Registry.register(Registry.ITEM, id("reversed_excursion_funnel_emitter"), new BlockItem(REVERSED_EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("duel_excursion_funnel_emitter_entity"), DUAL_EXCURSION_FUNNEL_EMITTER_ENTITY);
        Registry.register(Registry.BLOCK, id("duel_excursion_funnel_emitter"), DUEL_EXCURSION_FUNNEL_EMITTER);
        Registry.register(Registry.ITEM, id("duel_excursion_funnel_emitter"), new BlockItem(DUEL_EXCURSION_FUNNEL_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("faith_plate_entity"), FAITH_PLATE_ENTITY);
        Registry.register(Registry.BLOCK, id("faith_plate"), FAITH_PLATE);
        Registry.register(Registry.ITEM, id("faith_plate"), new BlockItem(FAITH_PLATE, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("beta_faith_plate_entity"), BETA_FAITH_PLATE_ENTITY);
        Registry.register(Registry.BLOCK, id("beta_faith_plate"), BETA_FAITH_PLATE);
        Registry.register(Registry.ITEM, id("beta_faith_plate"), new BlockItem(BETA_FAITH_PLATE, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_entity"), LASER_ENTITY);
        Registry.register(Registry.BLOCK, id("laser"), LASER);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_emitter_entity"), LASER_EMITTER_ENTITY);
        Registry.register(Registry.BLOCK, id("laser_emitter"), LASER_EMITTER);
        Registry.register(Registry.ITEM, id("laser_emitter"), new BlockItem(LASER_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_catcher_entity"), LASER_CATCHER_ENTITY);
        Registry.register(Registry.BLOCK, id("laser_catcher"), LASER_CATCHER);
        Registry.register(Registry.ITEM, id("laser_catcher"), new BlockItem(LASER_CATCHER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("laser_relay_entity"), LASER_RELAY_ENTITY);
        Registry.register(Registry.BLOCK, id("laser_relay"), LASER_RELAY);
        Registry.register(Registry.ITEM, id("laser_relay"), new BlockItem(LASER_RELAY, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("floor_button_block_entity"), FLOOR_BUTTON_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK, id("floor_button"), FLOOR_BUTTON);
        Registry.register(Registry.ITEM, id("floor_button"), new BlockItem(FLOOR_BUTTON, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("old_ap_floor_button_block_entity"), OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK, id("old_ap_floor_button"), OLD_AP_FLOOR_BUTTON);
        Registry.register(Registry.ITEM, id("old_ap_floor_button"), new BlockItem(OLD_AP_FLOOR_BUTTON, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("rocket_turret"), ROCKET_TURRET_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK, id("rocket_turret"), ROCKET_TURRET);
        Registry.register(Registry.ITEM, id("rocket_turret"), new BlockItem(ROCKET_TURRET, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("power_block"), POWER_BLOCK);
        Registry.register(Registry.ITEM, id("power_block"), new BlockItem(POWER_BLOCK, new Item.Settings().rarity(Rarity.EPIC).group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("velocity_helper"), VELOCITY_HELPER);
        Registry.register(Registry.ITEM, id("velocity_helper"), new BlockItem(VELOCITY_HELPER, new Item.Settings().rarity(Rarity.EPIC).group(PortalCubed.TESTING_ELEMENTS_GROUP)));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("velocity_helper"), VELOCITY_HELPER_BLOCK_ENTITY);

        Registry.register(Registry.BLOCK, id("fizzler"), FIZZLER);
        Registry.register(Registry.BLOCK, id("fizzler_emitter"), FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("fizzler_emitter"), new BlockItem(FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("portal_1_fizzler"), PORTAL_1_FIZZLER);
        Registry.register(Registry.BLOCK, id("portal_1_fizzler_emitter"), PORTAL_1_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("portal_1_fizzler_emitter"), new BlockItem(PORTAL_1_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("old_aperture_fizzler"), OLD_APERTURE_FIZZLER);
        Registry.register(Registry.BLOCK, id("old_aperture_fizzler_emitter"), OLD_APERTURE_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("old_aperture_fizzler_emitter"), new BlockItem(OLD_APERTURE_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("laser_fizzler"), LASER_FIZZLER);
        Registry.register(Registry.BLOCK, id("laser_fizzler_emitter"), LASER_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("laser_fizzler_emitter"), new BlockItem(LASER_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("death_fizzler"), DEATH_FIZZLER);
        Registry.register(Registry.BLOCK, id("death_fizzler_emitter"), DEATH_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("death_fizzler_emitter"), new BlockItem(DEATH_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("old_aperture_death_fizzler"), OLD_APERTURE_DEATH_FIZZLER);
        Registry.register(Registry.BLOCK, id("old_aperture_death_fizzler_emitter"), OLD_APERTURE_DEATH_FIZZLER_EMITTER);
        Registry.register(Registry.ITEM, id("old_aperture_death_fizzler_emitter"), new BlockItem(OLD_APERTURE_DEATH_FIZZLER_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("matter_inquisition_field"), MATTER_INQUISITION_FIELD);
        Registry.register(Registry.BLOCK, id("matter_inquisition_field_emitter"), MATTER_INQUISITION_FIELD_EMITTER);
        Registry.register(Registry.ITEM, id("matter_inquisition_field_emitter"), new BlockItem(MATTER_INQUISITION_FIELD_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("physics_repulsion_field"), PHYSICS_REPULSION_FIELD);
        Registry.register(Registry.BLOCK, id("physics_repulsion_field_emitter"), PHYSICS_REPULSION_FIELD_EMITTER);
        Registry.register(Registry.ITEM, id("physics_repulsion_field_emitter"), new BlockItem(PHYSICS_REPULSION_FIELD_EMITTER, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("pedestal_button"), TALL_BUTTON);
        Registry.register(Registry.ITEM, id("pedestal_button"), new BlockItem(TALL_BUTTON, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        Registry.register(Registry.BLOCK, id("old_ap_pedestal_button"), OLD_AP_PEDESTAL_BUTTON);
        Registry.register(Registry.ITEM, id("old_ap_pedestal_button"), new BlockItem(OLD_AP_PEDESTAL_BUTTON, new Item.Settings().group(PortalCubed.TESTING_ELEMENTS_GROUP)));

        PortalBlocksLoader.initCommon();
    }


}
