package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.PortalCubed;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PortalCubedEntities {
    public static final EntityType<PortalPlaceholderEntity> PORTAL_PLACEHOLDER = FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, PortalPlaceholderEntity::new)
            .dimensions(EntityDimensions.changing(0F, 0F))
            .build();

    public static final EntityType<ExperimentalPortal> EXPERIMENTAL_PORTAL = FabricEntityTypeBuilder.create(SpawnGroup.MISC, ExperimentalPortal::new)
            .dimensions(EntityDimensions.changing(1F, 1F))
            .build();

    public static final EntityType<StorageCubeEntity> STORAGE_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, StorageCubeEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<CompanionCubeEntity> COMPANION_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CompanionCubeEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();
    public static final EntityType<RedirectionCubeEntity> REDIRECTION_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RedirectionCubeEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();
    public static final EntityType<RadioEntity> RADIO = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RadioEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.3125F))
            .build();

    public static final EntityType<OldApCubeEntity> OLDAPCUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OldApCubeEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<Portal1CompanionCubeEntity> PORTAL_1_COMPANION_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Portal1CompanionCubeEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<Portal1StorageCubeEntity> PORTAL_1_STORAGE_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Portal1StorageCubeEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<BeansEntity> BEANS = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BeansEntity::new)
            .dimensions(EntityDimensions.changing(0.25F, 0.375F))
            .build();

    public static final EntityType<MugEntity> MUG = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MugEntity::new)
            .dimensions(EntityDimensions.changing(0.1875F, 0.25F))
            .build();

    public static final EntityType<JugEntity> JUG = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, JugEntity::new)
            .dimensions(EntityDimensions.changing(0.375F, 0.5F))
            .build();

    public static final EntityType<ComputerEntity> COMPUTER = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ComputerEntity::new)
            .dimensions(EntityDimensions.changing(0.5F, 0.1875F))
            .build();

    public static final EntityType<ChairEntity> CHAIR = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ChairEntity::new)
            .dimensions(EntityDimensions.changing(0.4375F, 0.46875F))
            .build();


    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "portal_placeholder"), PORTAL_PLACEHOLDER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "experimental_portal"), EXPERIMENTAL_PORTAL);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "storage_cube"), STORAGE_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "companion_cube"), COMPANION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "radio"), RADIO);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "redirection_cube"), REDIRECTION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "old_ap_cube"), OLDAPCUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "portal_1_companion_cube"), PORTAL_1_COMPANION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "portal_1_storage_cube"), PORTAL_1_STORAGE_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "beans"), BEANS);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "mug"), MUG);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "jug"), JUG);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "computer"), COMPUTER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "chair"), CHAIR);
        FabricDefaultAttributeRegistry.register(STORAGE_CUBE, StorageCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(COMPANION_CUBE, CompanionCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(RADIO, RadioEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(REDIRECTION_CUBE, RedirectionCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(OLDAPCUBE, OldApCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(PORTAL_1_COMPANION_CUBE, Portal1CompanionCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(PORTAL_1_STORAGE_CUBE, Portal1StorageCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(BEANS, BeansEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(MUG, MugEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(JUG, JugEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(COMPUTER, ComputerEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(CHAIR, ChairEntity.createMobAttributes());
    }


}
