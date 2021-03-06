package com.fusionflux.fluxtech.entity;

import com.fusionflux.fluxtech.FluxTech;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluxTechEntities {
    public static final EntityType<CubeEntity> CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CubeEntity::new)
                                                                             .dimensions(EntityDimensions.fixed(1.0F, 1.0F))
                                                                             .build();
    public static final EntityType<CompanionCubeEntity> COMPANION_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CompanionCubeEntity::new)
                                                                             .dimensions(EntityDimensions.fixed(1.0F, 1.0F))
                                                                             .build();

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(FluxTech.MOD_ID, "cube"), CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(FluxTech.MOD_ID, "companion_cube"), COMPANION_CUBE);
    }
}
