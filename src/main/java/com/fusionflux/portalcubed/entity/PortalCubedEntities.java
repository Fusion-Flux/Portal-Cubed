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

    public static final EntityType<CustomPortalEntity> CUSTOM_PORTAL = FabricEntityTypeBuilder.create(SpawnGroup.MISC, CustomPortalEntity::new)
            .dimensions(EntityDimensions.changing(0F, 0F))
            .build();

    public static final EntityType<StorageCubeEntity> STORAGE_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, StorageCubeEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static final EntityType<CompanionCubeEntity> COMPANION_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CompanionCubeEntity::new)
            .dimensions(EntityDimensions.changing(0.625F, 0.625F))
            .build();

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "portal_placeholder"), PORTAL_PLACEHOLDER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "custom_portal"), CUSTOM_PORTAL);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "storage_cube"), STORAGE_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "companion_cube"), COMPANION_CUBE);
        FabricDefaultAttributeRegistry.register(STORAGE_CUBE, StorageCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(COMPANION_CUBE, CompanionCubeEntity.createMobAttributes());
    }


}
