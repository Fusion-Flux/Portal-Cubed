package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.blocks.BaseGel;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.beams.ExcursionFunnelEntity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedEntities {

    public static final EntityType<Portal> PORTAL = QuiltEntityTypeBuilder.create(MobCategory.MISC, Portal::new)
            .setDimensions(EntityDimensions.scalable(1F, 1F))
            .build();

    public static final EntityType<StorageCubeEntity> STORAGE_CUBE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, StorageCubeEntity::new)
            .setDimensions(EntityDimensions.scalable(0.625F, 0.625F))
            .build();

    public static final EntityType<CompanionCubeEntity> COMPANION_CUBE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, CompanionCubeEntity::new)
            .setDimensions(EntityDimensions.scalable(0.625F, 0.625F))
            .build();
    public static final EntityType<RedirectionCubeEntity> REDIRECTION_CUBE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, RedirectionCubeEntity::new)
            .setDimensions(EntityDimensions.scalable(0.625F, 0.625F))
            .build();
    public static final EntityType<SchrodingerCubeEntity> SCHRODINGER_CUBE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, SchrodingerCubeEntity::new)
        .setDimensions(EntityDimensions.scalable(0.625F, 0.625F))
        .build();
    public static final EntityType<RadioEntity> RADIO = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, RadioEntity::new)
            .setDimensions(EntityDimensions.scalable(0.625F, 0.3125F))
            .build();

    public static final EntityType<OldApCubeEntity> OLD_AP_CUBE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, OldApCubeEntity::new)
            .setDimensions(EntityDimensions.scalable(0.625F, 0.625F))
            .build();

    public static final EntityType<Portal1CompanionCubeEntity> PORTAL_1_COMPANION_CUBE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, Portal1CompanionCubeEntity::new)
            .setDimensions(EntityDimensions.scalable(0.625F, 0.625F))
            .build();

    public static final EntityType<Portal1StorageCubeEntity> PORTAL_1_STORAGE_CUBE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, Portal1StorageCubeEntity::new)
            .setDimensions(EntityDimensions.scalable(0.625F, 0.625F))
            .build();

    public static final EntityType<BeansEntity> BEANS = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, BeansEntity::new)
            .setDimensions(EntityDimensions.scalable(0.25F, 0.375F))
            .build();

    public static final EntityType<MugEntity> MUG = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, MugEntity::new)
            .setDimensions(EntityDimensions.scalable(0.1875F, 0.25F))
            .build();

    public static final EntityType<JugEntity> JUG = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, JugEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.5F))
            .build();

    public static final EntityType<ComputerEntity> COMPUTER = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, ComputerEntity::new)
            .setDimensions(EntityDimensions.scalable(0.5F, 0.1875F))
            .build();

    public static final EntityType<ChairEntity> CHAIR = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, ChairEntity::new)
            .setDimensions(EntityDimensions.scalable(0.4375F, 0.46875F))
            .build();

    public static final EntityType<LilPineappleEntity> LIL_PINEAPPLE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, LilPineappleEntity::new)
            .setDimensions(EntityDimensions.scalable(0.5625F, 0.5F))
            .build();

    public static final EntityType<HoopyEntity> HOOPY = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, HoopyEntity::new)
            .setDimensions(EntityDimensions.scalable(1.625F, 0.0625F))
            .build();

    public static final EntityType<CoreFrameEntity> CORE_FRAME = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, CoreFrameEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.375F))
            .build();

    public static final EntityType<AngerCoreEntity> ANGER_CORE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, AngerCoreEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.375F))
            .build();
    public static final EntityType<MoralityCoreEntity> MORALITY_CORE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, MoralityCoreEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.375F))
            .build();
    public static final EntityType<CakeCoreEntity> CAKE_CORE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, CakeCoreEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.375F))
            .build();
    public static final EntityType<CuriosityCoreEntity> CURIOSITY_CORE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, CuriosityCoreEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.375F))
            .build();

    public static final EntityType<SpaceCoreEntity> SPACE_CORE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, SpaceCoreEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.375F))
            .build();
    public static final EntityType<FactCoreEntity> FACT_CORE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, FactCoreEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.375F))
            .build();
    public static final EntityType<AdventureCoreEntity> ADVENTURE_CORE = QuiltEntityTypeBuilder.create(MobCategory.CREATURE, AdventureCoreEntity::new)
            .setDimensions(EntityDimensions.scalable(0.375F, 0.375F))
            .build();

    public static final EntityType<? extends GelBlobEntity> PROPULSION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.PROPULSION_GEL, id("textures/block/propulsion_gel.png")
    );
    public static final EntityType<? extends GelBlobEntity> REPULSION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.REPULSION_GEL, id("textures/block/repulsion_gel.png")
    );
    public static final EntityType<? extends GelBlobEntity> ADHESION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.ADHESION_GEL, id("textures/block/adhesion_gel.png")
    );
    public static final EntityType<? extends GelBlobEntity> CONVERSION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.CONVERSION_GEL, id("textures/block/gel.png")
    );
    public static final EntityType<? extends GelBlobEntity> REFLECTION_GEL_BLOB = createGelBlob(
        PortalCubedBlocks.REFLECTION_GEL, id("textures/block/reflection_gel.png")
    );

    public static final EntityType<RocketEntity> ROCKET = QuiltEntityTypeBuilder.create(MobCategory.MISC, RocketEntity::new)
        .setDimensions(EntityDimensions.scalable(0.1875f, 0.1875f))
        .build();

    public static final EntityType<EnergyPelletEntity> ENERGY_PELLET = QuiltEntityTypeBuilder.create(MobCategory.MISC, EnergyPelletEntity::new)
        .setDimensions(EntityDimensions.scalable(0.25f, 0.25f))
        .build();

    public static final EntityType<TurretEntity> TURRET = QuiltEntityTypeBuilder.create(MobCategory.MISC, TurretEntity::new)
        .setDimensions(EntityDimensions.scalable(0.75f * TurretEntity.MODEL_SCALE, 1.5f * TurretEntity.MODEL_SCALE))
        .build();

    public static final EntityType<ExcursionFunnelEntity> EXCURSION_FUNNEL = QuiltEntityTypeBuilder.create(MobCategory.MISC, ExcursionFunnelEntity::new)
            .setDimensions(EntityDimensions.scalable(1, 1))
            .maxChunkTrackingRange(64)
            .disableSummon()
            .build();

    public static final TagKey<EntityType<?>> P1_ENTITY = TagKey.create(Registries.ENTITY_TYPE, id("p1_entity"));
    public static final TagKey<EntityType<?>> PORTAL_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, id("portal_blacklist"));

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("portal"), PORTAL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("storage_cube"), STORAGE_CUBE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("companion_cube"), COMPANION_CUBE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("radio"), RADIO);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("redirection_cube"), REDIRECTION_CUBE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("schrodinger_cube"), SCHRODINGER_CUBE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("old_ap_cube"), OLD_AP_CUBE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("portal_1_companion_cube"), PORTAL_1_COMPANION_CUBE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("portal_1_storage_cube"), PORTAL_1_STORAGE_CUBE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("beans"), BEANS);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("mug"), MUG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("jug"), JUG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("computer"), COMPUTER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("chair"), CHAIR);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("lil_pineapple"), LIL_PINEAPPLE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("hoopy"), HOOPY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("core_frame"), CORE_FRAME);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("anger_core"), ANGER_CORE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("intelligence_core"), CAKE_CORE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("curiosity_core"), CURIOSITY_CORE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("morality_core"), MORALITY_CORE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("space_core"), SPACE_CORE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("adventure_core"), ADVENTURE_CORE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("fact_core"), FACT_CORE);

        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("propulsion_gel_blob"), PROPULSION_GEL_BLOB);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("repulsion_gel_blob"), REPULSION_GEL_BLOB);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("adhesion_gel_blob"), ADHESION_GEL_BLOB);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("conversion_gel_blob"), CONVERSION_GEL_BLOB);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("reflection_gel_blob"), REFLECTION_GEL_BLOB);

        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("rocket"), ROCKET);

        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("energy_pellet"), ENERGY_PELLET);

        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("turret"), TURRET);

        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("excursion_funnel"), EXCURSION_FUNNEL);

        DefaultAttributes.SUPPLIERS.put(STORAGE_CUBE, StorageCubeEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(COMPANION_CUBE, CompanionCubeEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(RADIO, RadioEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(REDIRECTION_CUBE, RedirectionCubeEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(SCHRODINGER_CUBE, SchrodingerCubeEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(OLD_AP_CUBE, OldApCubeEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(PORTAL_1_COMPANION_CUBE, Portal1CompanionCubeEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(PORTAL_1_STORAGE_CUBE, Portal1StorageCubeEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(BEANS, BeansEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(MUG, MugEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(JUG, JugEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(COMPUTER, ComputerEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(CHAIR, ChairEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(LIL_PINEAPPLE, LilPineappleEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(HOOPY, HoopyEntity.createMobAttributes().build());

        DefaultAttributes.SUPPLIERS.put(CORE_FRAME, HoopyEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(ANGER_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(CAKE_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(CURIOSITY_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(MORALITY_CORE, HoopyEntity.createMobAttributes().build());

        DefaultAttributes.SUPPLIERS.put(SPACE_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(ADVENTURE_CORE, HoopyEntity.createMobAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FACT_CORE, HoopyEntity.createMobAttributes().build());

        DefaultAttributes.SUPPLIERS.put(TURRET, TurretEntity.createMobAttributes().build());
    }

    public static EntityType<? extends GelBlobEntity> createGelBlob(BaseGel gel, ResourceLocation texture) {
        return QuiltEntityTypeBuilder.create()
            .<GelBlobEntity>entityFactory((type, world) -> new GelBlobEntity(type, world) {
                @Override
                public ResourceLocation getTexture() {
                    return texture;
                }

                @Override
                public BaseGel getGel() {
                    return gel;
                }
            })
            .setDimensions(EntityDimensions.scalable(1, 1))
            .build();
    }
}
