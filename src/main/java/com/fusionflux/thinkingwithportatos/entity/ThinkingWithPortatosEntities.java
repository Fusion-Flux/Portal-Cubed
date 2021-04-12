package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import dev.lazurite.rayon.core.api.event.ElementCollisionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ThinkingWithPortatosEntities {
    public static final EntityType<CubeEntity> CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CubeEntity::new)
            .dimensions(EntityDimensions.fixed(1.0F, 1.0F))
            .build();

    public static final EntityType<CompanionCubeEntity> COMPANION_CUBE = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CompanionCubeEntity::new)
            .dimensions(EntityDimensions.fixed(1.0F, 1.0F))
            .build();

    public static final EntityType<PortalPlaceholderEntity> PORTAL_PLACEHOLDER = FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, PortalPlaceholderEntity::new)
            .dimensions(EntityDimensions.changing(0F, 0F))
            .build();

    public static final EntityType<CustomPortalEntity> CUSTOM_PORTAL = FabricEntityTypeBuilder.create(SpawnGroup.MISC, CustomPortalEntity::new)
            .dimensions(EntityDimensions.changing(0F, 0F))
            .build();

    public static final EntityType<GelOrbEntity> GEL_ORB = FabricEntityTypeBuilder.<GelOrbEntity>create(SpawnGroup.MISC, GelOrbEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
            .trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
            .build(); // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "cube"), CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "companion_cube"), COMPANION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "portal_placeholder"), PORTAL_PLACEHOLDER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "custom_portal"), CUSTOM_PORTAL);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "gel_orb"), GEL_ORB);

        ElementCollisionEvents.BLOCK_COLLISION.register((executor, element, block, impulse) -> {
            if (element instanceof CubeEntity) {
                executor.execute(() -> ((CubeEntity) element).onCollision(impulse));
            }
        });
    }
}
