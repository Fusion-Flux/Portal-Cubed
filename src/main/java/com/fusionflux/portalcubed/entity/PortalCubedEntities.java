package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedEntities {

    public static final EntityType<ExperimentalPortal> EXPERIMENTAL_PORTAL = QuiltEntityTypeBuilder.create(SpawnGroup.MISC, ExperimentalPortal::new)
            .setDimensions(EntityDimensions.changing(1F, 1F))
            .build();

    public static final EntityType<StorageCubeEntity> STORAGE_CUBE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, StorageCubeEntity::new)
            .setDimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<CompanionCubeEntity> COMPANION_CUBE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, CompanionCubeEntity::new)
            .setDimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();
    public static final EntityType<RedirectionCubeEntity> REDIRECTION_CUBE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, RedirectionCubeEntity::new)
            .setDimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();
    public static final EntityType<RadioEntity> RADIO = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, RadioEntity::new)
            .setDimensions(EntityDimensions.changing(0.625F, 0.3125F))
            .build();

    public static final EntityType<OldApCubeEntity> OLD_AP_CUBE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, OldApCubeEntity::new)
            .setDimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<Portal1CompanionCubeEntity> PORTAL_1_COMPANION_CUBE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, Portal1CompanionCubeEntity::new)
            .setDimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<Portal1StorageCubeEntity> PORTAL_1_STORAGE_CUBE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, Portal1StorageCubeEntity::new)
            .setDimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<BeansEntity> BEANS = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, BeansEntity::new)
            .setDimensions(EntityDimensions.changing(0.25F, 0.375F))
            .build();

    public static final EntityType<MugEntity> MUG = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, MugEntity::new)
            .setDimensions(EntityDimensions.changing(0.1875F, 0.25F))
            .build();

    public static final EntityType<JugEntity> JUG = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, JugEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.5F))
            .build();

    public static final EntityType<ComputerEntity> COMPUTER = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, ComputerEntity::new)
            .setDimensions(EntityDimensions.changing(0.5F, 0.1875F))
            .build();

    public static final EntityType<ChairEntity> CHAIR = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, ChairEntity::new)
            .setDimensions(EntityDimensions.changing(0.4375F, 0.46875F))
            .build();

    public static final EntityType<LilPineappleEntity> LIL_PINEAPPLE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, LilPineappleEntity::new)
            .setDimensions(EntityDimensions.changing(0.5625F, 0.5F))
            .build();

    public static final EntityType<HoopyEntity> HOOPY = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, HoopyEntity::new)
            .setDimensions(EntityDimensions.changing(1.625F, 0.0625F))
            .build();

    public static final EntityType<CoreFrameEntity> CORE_FRAME = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, CoreFrameEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.375F))
            .build();

    public static final EntityType<AngerCoreEntity> ANGER_CORE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, AngerCoreEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.375F))
            .build();
    public static final EntityType<MoralityCoreEntity> MORALITY_CORE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, MoralityCoreEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.375F))
            .build();
    public static final EntityType<CakeCoreEntity> CAKE_CORE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, CakeCoreEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.375F))
            .build();
    public static final EntityType<CuriosityCoreEntity> CURIOSITY_CORE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, CuriosityCoreEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.375F))
            .build();

    public static final EntityType<SpaceCoreEntity> SPACE_CORE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, SpaceCoreEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.375F))
            .build();
    public static final EntityType<FactCoreEntity> FACT_CORE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, FactCoreEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.375F))
            .build();
    public static final EntityType<AdventureCoreEntity> ADVENTURE_CORE = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, AdventureCoreEntity::new)
            .setDimensions(EntityDimensions.changing(0.375F, 0.375F))
            .build();

    public static final EntityType<? extends GelBlobEntity> PROPULSION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.PROPULSION_GEL, id("textures/block/propulsion_gel.png")
    );
    public static final EntityType<? extends GelBlobEntity> REPULSION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.REPULSION_GEL, id("textures/block/repulsion_gel.png")
    );
    public static final EntityType<? extends GelBlobEntity> CONVERSION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.CONVERSION_GEL, id("textures/block/gel.png")
    );
    public static final EntityType<? extends GelBlobEntity> ADHESION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.ADHESION_GEL, id("textures/block/adhesion_gel.png")
    );

    public static final EntityType<RocketEntity> ROCKET = QuiltEntityTypeBuilder.create(SpawnGroup.MISC, RocketEntity::new)
        .setDimensions(EntityDimensions.changing(0.1875f, 0.1875f))
        .build();

    public static final EntityType<EnergyPelletEntity> ENERGY_PELLET = QuiltEntityTypeBuilder.create(SpawnGroup.MISC, EnergyPelletEntity::new)
        .setDimensions(EntityDimensions.changing(0.25f, 0.25f))
        .build();

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, id("experimental_portal"), EXPERIMENTAL_PORTAL);
        Registry.register(Registry.ENTITY_TYPE, id("storage_cube"), STORAGE_CUBE);
        Registry.register(Registry.ENTITY_TYPE, id("companion_cube"), COMPANION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, id("radio"), RADIO);
        Registry.register(Registry.ENTITY_TYPE, id("redirection_cube"), REDIRECTION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, id("old_ap_cube"), OLD_AP_CUBE);
        Registry.register(Registry.ENTITY_TYPE, id("portal_1_companion_cube"), PORTAL_1_COMPANION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, id("portal_1_storage_cube"), PORTAL_1_STORAGE_CUBE);
        Registry.register(Registry.ENTITY_TYPE, id("beans"), BEANS);
        Registry.register(Registry.ENTITY_TYPE, id("mug"), MUG);
        Registry.register(Registry.ENTITY_TYPE, id("jug"), JUG);
        Registry.register(Registry.ENTITY_TYPE, id("computer"), COMPUTER);
        Registry.register(Registry.ENTITY_TYPE, id("chair"), CHAIR);
        Registry.register(Registry.ENTITY_TYPE, id("lil_pineapple"), LIL_PINEAPPLE);
        Registry.register(Registry.ENTITY_TYPE, id("hoopy"), HOOPY);
        Registry.register(Registry.ENTITY_TYPE, id("core_frame"), CORE_FRAME);
        Registry.register(Registry.ENTITY_TYPE, id("anger_core"), ANGER_CORE);
        Registry.register(Registry.ENTITY_TYPE, id("intelligence_core"), CAKE_CORE);
        Registry.register(Registry.ENTITY_TYPE, id("curiosity_core"), CURIOSITY_CORE);
        Registry.register(Registry.ENTITY_TYPE, id("morality_core"), MORALITY_CORE);
        Registry.register(Registry.ENTITY_TYPE, id("space_core"), SPACE_CORE);
        Registry.register(Registry.ENTITY_TYPE, id("adventure_core"), ADVENTURE_CORE);
        Registry.register(Registry.ENTITY_TYPE, id("fact_core"), FACT_CORE);

        Registry.register(Registry.ENTITY_TYPE, id("propulsion_gel_blob"), PROPULSION_GEL_BLOB);
        Registry.register(Registry.ENTITY_TYPE, id("repulsion_gel_blob"), REPULSION_GEL_BLOB);
        Registry.register(Registry.ENTITY_TYPE, id("conversion_gel_blob"), CONVERSION_GEL_BLOB);
        Registry.register(Registry.ENTITY_TYPE, id("adhesion_gel_blob"), ADHESION_GEL_BLOB);

        Registry.register(Registry.ENTITY_TYPE, id("rocket"), ROCKET);

        Registry.register(Registry.ENTITY_TYPE, id("energy_pellet"), ENERGY_PELLET);

        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(STORAGE_CUBE, StorageCubeEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(COMPANION_CUBE, CompanionCubeEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(RADIO, RadioEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(REDIRECTION_CUBE, RedirectionCubeEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(OLD_AP_CUBE, OldApCubeEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(PORTAL_1_COMPANION_CUBE, Portal1CompanionCubeEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(PORTAL_1_STORAGE_CUBE, Portal1StorageCubeEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(BEANS, BeansEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(MUG, MugEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(JUG, JugEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(COMPUTER, ComputerEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(CHAIR, ChairEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(LIL_PINEAPPLE, LilPineappleEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(HOOPY, HoopyEntity.createMobAttributes().build());

        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(CORE_FRAME, HoopyEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(ANGER_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(CAKE_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(CURIOSITY_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(MORALITY_CORE, HoopyEntity.createMobAttributes().build());

        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(SPACE_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(ADVENTURE_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(FACT_CORE, HoopyEntity.createMobAttributes().build());
    }

    public static EntityType<? extends GelBlobEntity> createGelBlob(GelFlat gel, Identifier texture) {
        return QuiltEntityTypeBuilder.create()
            .<GelBlobEntity>entityFactory((type, world) -> new GelBlobEntity(type, world) {
                @Override
                public Identifier getTexture() {
                    return texture;
                }

                @Override
                public GelFlat getGel() {
                    return gel;
                }
            })
            .setDimensions(EntityDimensions.changing(1, 1))
            .build();
    }
}
