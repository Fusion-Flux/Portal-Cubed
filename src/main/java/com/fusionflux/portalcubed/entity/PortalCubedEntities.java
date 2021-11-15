package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.PortalCubed;
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

    public static final EntityType<GelOrbEntity> GEL_ORB = FabricEntityTypeBuilder.<GelOrbEntity>create(SpawnGroup.MISC, GelOrbEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
            .trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
            .build(); // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
    public static final EntityType<RepulsionGelOrbEntity> REPULSION_GEL_ORB = FabricEntityTypeBuilder.<RepulsionGelOrbEntity>create(SpawnGroup.MISC, RepulsionGelOrbEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
            .trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
            .build(); // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "portal_placeholder"), PORTAL_PLACEHOLDER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "custom_portal"), CUSTOM_PORTAL);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "gel_orb"), GEL_ORB);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(PortalCubed.MODID, "repulsion_gel_orb"), REPULSION_GEL_ORB);
    }


}
