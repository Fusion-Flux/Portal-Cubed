package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.*;
import com.fusionflux.portalcubed.blocks.fizzler.*;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.GelBlobItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedBlocks {
    public static final Item BASE_GEL = new Item(new QuiltItemSettings().fireResistant());
    public static final PropulsionGel PROPULSION_GEL = new PropulsionGel(QuiltBlockSettings.of(Material.PLANT).randomTicks().destroyTime(0f).noOcclusion().noCollission().sound(new SoundType(1, -1, SoundEvents.HONEY_BLOCK_BREAK, SoundEvents.HONEY_BLOCK_STEP, SoundEvents.HONEY_BLOCK_PLACE, SoundEvents.HONEY_BLOCK_HIT, SoundEvents.HONEY_BLOCK_FALL)).color(MaterialColor.COLOR_ORANGE));
    public static final RepulsionGel REPULSION_GEL = new RepulsionGel(QuiltBlockSettings.copyOf(PROPULSION_GEL).mapColor(MaterialColor.COLOR_LIGHT_BLUE));
    public static final AdhesionGel ADHESION_GEL = new AdhesionGel(QuiltBlockSettings.copyOf(PROPULSION_GEL).mapColor(MaterialColor.COLOR_PURPLE));
    public static final BaseGel CONVERSION_GEL = new BaseGel(QuiltBlockSettings.copyOf(PROPULSION_GEL).mapColor(MaterialColor.METAL));
    public static final BaseGel REFLECTION_GEL = new ReflectionGel(QuiltBlockSettings.copyOf(PROPULSION_GEL).mapColor(MaterialColor.COLOR_LIGHT_GRAY));

    public static final HardLightBridgeEmitterBlock HLB_EMITTER_BLOCK = new HardLightBridgeEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final HardLightBridgeBlock HLB_BLOCK = new HardLightBridgeBlock(QuiltBlockSettings.of(Material.AIR).destroyTime(999999f).noOcclusion().explosionResistance(9999999999f).sound(new SoundType(1, 1, SoundEvents.STONE_BREAK, SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL)).color(MaterialColor.DIAMOND));

    public static final AutoPortalBlock AUTO_PORTAL_BLOCK = new AutoPortalBlock(
        QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)
    );

    // TODO: Due to remapping weirdness, QuiltMaterialBuilder couldn't be used properly here. However, the whole material system is redone in 1.20, and neurotoxin is broken anyway, so this is just a temporary patch.
    public static final NeurotoxinBlock NEUROTOXIN_BLOCK = new NeurotoxinBlock(QuiltBlockSettings.of(Material.AIR).noOcclusion().noCollission());
    public static final NeurotoxinEmitterBlock NEUROTOXIN_EMITTER = new NeurotoxinEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().noOcclusion().noCollission().sound(SoundType.STONE));
    public static final ExcursionFunnelEmitter EXCURSION_FUNNEL_EMITTER = new ExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DualExcursionFunnelEmitter DUAL_EXCURSION_FUNNEL_EMITTER = new DualExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final ReversedExcursionFunnelEmitter REVERSED_EXCURSION_FUNNEL_EMITTER = new ReversedExcursionFunnelEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final ExcursionFunnelMain EXCURSION_FUNNEL = new ExcursionFunnelMain(QuiltBlockSettings.of(Material.AIR).noOcclusion().noCollission());

    public static final TallButton TALL_BUTTON = new TallButton(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops());
    public static final OldApTallButton OLD_AP_PEDESTAL_BUTTON = new OldApTallButton(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops());

    public static final SlidingDoorBlock PORTAL2DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops());
    public static final SlidingDoorBlock OCTOPUS_DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops());
    public static final SlidingDoorBlock OLD_AP_DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops());
    public static final SlidingDoorBlock PORTAL1DOOR = new SlidingDoorBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops());

    public static final BlockEntityType<HardLightBridgeEmitterBlockEntity> HLB_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(HardLightBridgeEmitterBlockEntity::new, HLB_EMITTER_BLOCK).build();
    public static final BlockEntityType<HardLightBridgeBlockEntity> HLB_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(HardLightBridgeBlockEntity::new, HLB_BLOCK).build();

    public static final BlockEntityType<AutoPortalBlockEntity> AUTO_PORTAL_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(AutoPortalBlockEntity::new, AUTO_PORTAL_BLOCK).build();

    public static final Block FAITH_PLATE = new FaithPlateBlock(QuiltBlockSettings.of(Material.STONE).destroyTime(999999f).explosionResistance(9999999999f).sound(new SoundType(1, 1, SoundEvents.STONE_BREAK, SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL)), PortalCubedBlocks::getFaithPlateBlockEntity);
    public static final Block BETA_FAITH_PLATE = new FaithPlateBlock(QuiltBlockSettings.copyOf(FAITH_PLATE), PortalCubedBlocks::getBetaFaithPlateBlockEntity);
    public static final BlockEntityType<FaithPlateBlockEntity> FAITH_PLATE_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(FaithPlateBlockEntity::new, FAITH_PLATE).build();
    public static final BlockEntityType<BetaFaithPlateBlockEntity> BETA_FAITH_PLATE_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(BetaFaithPlateBlockEntity::new, BETA_FAITH_PLATE).build();

    public static final Block FAITH_PLATE_TARGET = new FaithPlateTargetBlock(QuiltBlockSettings.of(Material.PLANT).destroyTime(0).noOcclusion().noCollission().color(MaterialColor.COLOR_CYAN));

    public static final BlockEntityType<NeurotoxinBlockEntity> NEUROTOXIN_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(NeurotoxinBlockEntity::new, NEUROTOXIN_BLOCK).build();
    public static final BlockEntityType<NeurotoxinEmitterBlockEntity> NEUROTOXIN_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(NeurotoxinEmitterBlockEntity::new, NEUROTOXIN_EMITTER).build();
    public static final BlockEntityType<ExcursionFunnelEmitterBlockEntity> EXCURSION_FUNNEL_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(ExcursionFunnelEmitterBlockEntity::new, EXCURSION_FUNNEL_EMITTER).build();
    public static final BlockEntityType<ReversedExcursionFunnelEmitterBlockEntity> REVERSED_EXCURSION_FUNNEL_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(ReversedExcursionFunnelEmitterBlockEntity::new, REVERSED_EXCURSION_FUNNEL_EMITTER).build();
    public static final BlockEntityType<DualExcursionFunnelEmitterBlockEntity> DUAL_EXCURSION_FUNNEL_EMITTER_ENTITY = QuiltBlockEntityTypeBuilder.create(DualExcursionFunnelEmitterBlockEntity::new, DUAL_EXCURSION_FUNNEL_EMITTER).build();
    public static final BlockEntityType<ExcursionFunnelMainBlockEntity> EXCURSION_FUNNEL_ENTITY = QuiltBlockEntityTypeBuilder.create(ExcursionFunnelMainBlockEntity::new, EXCURSION_FUNNEL).build();

    public static final PowerBlock POWER_BLOCK = new PowerBlock(QuiltBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.8F).noLootTable().noOcclusion());

    public static final Block VELOCITY_HELPER = new VelocityHelperBlock(QuiltBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.8F).noLootTable().noOcclusion());
    public static final BlockEntityType<VelocityHelperBlockEntity> VELOCITY_HELPER_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(VelocityHelperBlockEntity::new, VELOCITY_HELPER).build();

    public static final Block CATAPULT = new CatapultBlock(QuiltBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.8F).noLootTable().noOcclusion());
    public static final BlockEntityType<CatapultBlockEntity> CATAPULT_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(CatapultBlockEntity::new, CATAPULT).build();

    public static final FizzlerBlock FIZZLER = new FizzlerBlock(QuiltBlockSettings.of(Material.PORTAL).noCollission().strength(-1, 3600000));
    public static final FizzlerEmitter FIZZLER_EMITTER = new FizzlerEmitter(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE), FIZZLER);
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

    public static final LaserEmitterBlock LASER_EMITTER = new LaserEmitterBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE));
    public static final BlockEntityType<LaserEmitterBlockEntity> LASER_EMITTER_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(LaserEmitterBlockEntity::new, LASER_EMITTER).build();

    public static final LaserCatcherBlock LASER_CATCHER = new LaserCatcherBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE));
    public static final LaserRelayBlock LASER_RELAY = new LaserRelayBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE));
    public static final BlockEntityType<LaserNodeBlockEntity> LASER_NODE_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(LaserNodeBlockEntity::new, LASER_CATCHER, LASER_RELAY).build();

    public static final FloorButtonBlock FLOOR_BUTTON = new FloorButtonBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final BlockEntityType<FloorButtonBlockEntity> FLOOR_BUTTON_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(FloorButtonBlockEntity::new, FLOOR_BUTTON).build();

    public static final OldApFloorButtonBlock OLD_AP_FLOOR_BUTTON = new OldApFloorButtonBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final BlockEntityType<OldApFloorButtonBlockEntity> OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(OldApFloorButtonBlockEntity::new, OLD_AP_FLOOR_BUTTON).build();

    public static final RocketTurretBlock ROCKET_TURRET = new RocketTurretBlock(QuiltBlockSettings.of(Material.STONE).strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final BlockEntityType<RocketTurretBlockEntity> ROCKET_TURRET_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(RocketTurretBlockEntity::new, ROCKET_TURRET).build();

    public static final TagKey<Block> BULLET_HOLE_CONCRETE = TagKey.create(Registries.BLOCK, id("bullet_hole_concrete"));
    public static final TagKey<Block> BULLET_HOLE_GLASS = TagKey.create(Registries.BLOCK, id("bullet_hole_glass"));
    public static final TagKey<Block> BULLET_HOLE_METAL = TagKey.create(Registries.BLOCK, id("bullet_hole_metal"));
    public static final TagKey<Block> CANT_PLACE_PORTAL_ON = TagKey.create(Registries.BLOCK, id("cant_place_portal_on"));
    public static final TagKey<Block> PORTALABLE_IN_ADVENTURE = TagKey.create(Registries.BLOCK, id("portalable_in_adventure"));
    public static final TagKey<Block> PORTAL_NONSOLID = TagKey.create(Registries.BLOCK, id("portal_nonsolid"));
    public static final TagKey<Block> PORTAL_SOLID = TagKey.create(Registries.BLOCK, id("portal_solid"));
    public static final TagKey<Block> PORTALABLE_GELS = TagKey.create(Registries.BLOCK, id("portalable_gels"));

    public static void registerBlocks() {
        Registry.register(BuiltInRegistries.ITEM, id("base_gel"), BASE_GEL);

        Registry.register(BuiltInRegistries.BLOCK, id("propulsion_gel"), PROPULSION_GEL);
        Registry.register(BuiltInRegistries.ITEM, id("propulsion_gel"), new GelBlobItem(PROPULSION_GEL, PortalCubedEntities.PROPULSION_GEL_BLOB, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("repulsion_gel"), REPULSION_GEL);
        Registry.register(BuiltInRegistries.ITEM, id("repulsion_gel"), new GelBlobItem(REPULSION_GEL, PortalCubedEntities.REPULSION_GEL_BLOB, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("adhesion_gel"), ADHESION_GEL);
        Registry.register(BuiltInRegistries.ITEM, id("adhesion_gel"), new GelBlobItem(ADHESION_GEL, PortalCubedEntities.ADHESION_GEL_BLOB, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("conversion_gel"), CONVERSION_GEL);
        Registry.register(BuiltInRegistries.ITEM, id("conversion_gel"), new GelBlobItem(CONVERSION_GEL, PortalCubedEntities.CONVERSION_GEL_BLOB, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("reflection_gel"), REFLECTION_GEL);
        Registry.register(BuiltInRegistries.ITEM, id("reflection_gel"), new GelBlobItem(REFLECTION_GEL, PortalCubedEntities.REFLECTION_GEL_BLOB, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("portal_2_door"), PORTAL2DOOR);
        Registry.register(BuiltInRegistries.ITEM, id("portal_2_door"), new BlockItem(PORTAL2DOOR, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("octopus_door"), OCTOPUS_DOOR);
        Registry.register(BuiltInRegistries.ITEM, id("octopus_door"), new BlockItem(OCTOPUS_DOOR, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("old_ap_door"), OLD_AP_DOOR);
        Registry.register(BuiltInRegistries.ITEM, id("old_ap_door"), new BlockItem(OLD_AP_DOOR, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("portal_1_door"), PORTAL1DOOR);
        Registry.register(BuiltInRegistries.ITEM, id("portal_1_door"), new BlockItem(PORTAL1DOOR, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("light_bridge_emitter_entity"), HLB_EMITTER_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("light_bridge_emitter"), HLB_EMITTER_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, id("light_bridge_emitter"), new BlockItem(HLB_EMITTER_BLOCK, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("light_bridge_entity"), HLB_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("light_bridge"), HLB_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("neurotoxin_entity"), NEUROTOXIN_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("neurotoxin"), NEUROTOXIN_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("neurotoxin_emitter_entity"), NEUROTOXIN_EMITTER_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("neurotoxin_emitter"), NEUROTOXIN_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("neurotoxin_emitter"), new BlockItem(NEUROTOXIN_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("auto_portal_entity"), AUTO_PORTAL_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("auto_portal"), AUTO_PORTAL_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, id("auto_portal"), new BlockItem(AUTO_PORTAL_BLOCK, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("excursion_funnel_emitter_entity"), EXCURSION_FUNNEL_EMITTER_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("excursion_funnel_emitter"), EXCURSION_FUNNEL_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("excursion_funnel_emitter"), new BlockItem(EXCURSION_FUNNEL_EMITTER, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("excursion_funnel_entity"), EXCURSION_FUNNEL_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("excursion_funnel"), EXCURSION_FUNNEL);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("reversed_excursion_funnel_emitter_entity"), REVERSED_EXCURSION_FUNNEL_EMITTER_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("reversed_excursion_funnel_emitter"), REVERSED_EXCURSION_FUNNEL_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("reversed_excursion_funnel_emitter"), new BlockItem(REVERSED_EXCURSION_FUNNEL_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("dual_excursion_funnel_emitter_entity"), DUAL_EXCURSION_FUNNEL_EMITTER_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("dual_excursion_funnel_emitter"), DUAL_EXCURSION_FUNNEL_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("dual_excursion_funnel_emitter"), new BlockItem(DUAL_EXCURSION_FUNNEL_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("faith_plate"), FAITH_PLATE_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("beta_faith_plate"), BETA_FAITH_PLATE_BLOCK_ENTITY);

        Registry.register(BuiltInRegistries.BLOCK, id("faith_plate"), FAITH_PLATE);
        Registry.register(BuiltInRegistries.ITEM, id("faith_plate"), new BlockItem(FAITH_PLATE, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("beta_faith_plate"), BETA_FAITH_PLATE);
        Registry.register(BuiltInRegistries.ITEM, id("beta_faith_plate"), new BlockItem(BETA_FAITH_PLATE, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("faith_plate_target"), FAITH_PLATE_TARGET);
        Registry.register(BuiltInRegistries.ITEM, id("faith_plate_target"), new BlockItem(FAITH_PLATE_TARGET, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("laser_emitter_entity"), LASER_EMITTER_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("laser_emitter"), LASER_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("laser_emitter"), new BlockItem(LASER_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("laser_catcher"), LASER_CATCHER);
        Registry.register(BuiltInRegistries.ITEM, id("laser_catcher"), new BlockItem(LASER_CATCHER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("laser_relay"), LASER_RELAY);
        Registry.register(BuiltInRegistries.ITEM, id("laser_relay"), new BlockItem(LASER_RELAY, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("laser_node"), LASER_NODE_BLOCK_ENTITY);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("floor_button_block_entity"), FLOOR_BUTTON_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("floor_button"), FLOOR_BUTTON);
        Registry.register(BuiltInRegistries.ITEM, id("floor_button"), new BlockItem(FLOOR_BUTTON, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("old_ap_floor_button_block_entity"), OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("old_ap_floor_button"), OLD_AP_FLOOR_BUTTON);
        Registry.register(BuiltInRegistries.ITEM, id("old_ap_floor_button"), new BlockItem(OLD_AP_FLOOR_BUTTON, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("rocket_turret"), ROCKET_TURRET_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK, id("rocket_turret"), ROCKET_TURRET);
        Registry.register(BuiltInRegistries.ITEM, id("rocket_turret"), new BlockItem(ROCKET_TURRET, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("power_block"), POWER_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, id("power_block"), new BlockItem(POWER_BLOCK, new Item.Properties().rarity(Rarity.EPIC)));

        Registry.register(BuiltInRegistries.BLOCK, id("velocity_helper"), VELOCITY_HELPER);
        Registry.register(BuiltInRegistries.ITEM, id("velocity_helper"), new BlockItem(VELOCITY_HELPER, new Item.Properties().rarity(Rarity.EPIC)));
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("velocity_helper"), VELOCITY_HELPER_BLOCK_ENTITY);

        Registry.register(BuiltInRegistries.BLOCK, id("catapult"), CATAPULT);
        Registry.register(BuiltInRegistries.ITEM, id("catapult"), new BlockItem(CATAPULT, new Item.Properties().rarity(Rarity.EPIC)));
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("catapult"), CATAPULT_BLOCK_ENTITY);

        Registry.register(BuiltInRegistries.BLOCK, id("fizzler"), FIZZLER);
        Registry.register(BuiltInRegistries.BLOCK, id("fizzler_emitter"), FIZZLER_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("fizzler_emitter"), new BlockItem(FIZZLER_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("portal_1_fizzler"), PORTAL_1_FIZZLER);
        Registry.register(BuiltInRegistries.BLOCK, id("portal_1_fizzler_emitter"), PORTAL_1_FIZZLER_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("portal_1_fizzler_emitter"), new BlockItem(PORTAL_1_FIZZLER_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("old_aperture_fizzler"), OLD_APERTURE_FIZZLER);
        Registry.register(BuiltInRegistries.BLOCK, id("old_aperture_fizzler_emitter"), OLD_APERTURE_FIZZLER_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("old_aperture_fizzler_emitter"), new BlockItem(OLD_APERTURE_FIZZLER_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("laser_fizzler"), LASER_FIZZLER);
        Registry.register(BuiltInRegistries.BLOCK, id("laser_fizzler_emitter"), LASER_FIZZLER_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("laser_fizzler_emitter"), new BlockItem(LASER_FIZZLER_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("death_fizzler"), DEATH_FIZZLER);
        Registry.register(BuiltInRegistries.BLOCK, id("death_fizzler_emitter"), DEATH_FIZZLER_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("death_fizzler_emitter"), new BlockItem(DEATH_FIZZLER_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("old_aperture_death_fizzler"), OLD_APERTURE_DEATH_FIZZLER);
        Registry.register(BuiltInRegistries.BLOCK, id("old_aperture_death_fizzler_emitter"), OLD_APERTURE_DEATH_FIZZLER_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("old_aperture_death_fizzler_emitter"), new BlockItem(OLD_APERTURE_DEATH_FIZZLER_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("matter_inquisition_field"), MATTER_INQUISITION_FIELD);
        Registry.register(BuiltInRegistries.BLOCK, id("matter_inquisition_field_emitter"), MATTER_INQUISITION_FIELD_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("matter_inquisition_field_emitter"), new BlockItem(MATTER_INQUISITION_FIELD_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("physics_repulsion_field"), PHYSICS_REPULSION_FIELD);
        Registry.register(BuiltInRegistries.BLOCK, id("physics_repulsion_field_emitter"), PHYSICS_REPULSION_FIELD_EMITTER);
        Registry.register(BuiltInRegistries.ITEM, id("physics_repulsion_field_emitter"), new BlockItem(PHYSICS_REPULSION_FIELD_EMITTER, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("pedestal_button"), TALL_BUTTON);
        Registry.register(BuiltInRegistries.ITEM, id("pedestal_button"), new BlockItem(TALL_BUTTON, new Item.Properties()));

        Registry.register(BuiltInRegistries.BLOCK, id("old_ap_pedestal_button"), OLD_AP_PEDESTAL_BUTTON);
        Registry.register(BuiltInRegistries.ITEM, id("old_ap_pedestal_button"), new BlockItem(OLD_AP_PEDESTAL_BUTTON, new Item.Properties()));

        PortalBlocksLoader.initCommon();
    }

    private static BlockEntityType<FaithPlateBlockEntity> getFaithPlateBlockEntity() {
        return FAITH_PLATE_BLOCK_ENTITY;
    }

    private static BlockEntityType<BetaFaithPlateBlockEntity> getBetaFaithPlateBlockEntity() {
        return BETA_FAITH_PLATE_BLOCK_ENTITY;
    }
}
