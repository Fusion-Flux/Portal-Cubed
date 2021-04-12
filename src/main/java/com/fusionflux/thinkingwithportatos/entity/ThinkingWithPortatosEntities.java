package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
    public static final EntityType<GelOrbEntity> GEL_ORB = FabricEntityTypeBuilder.<GelOrbEntity>create(SpawnGroup.MISC, GelOrbEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
            .trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
            .build(); // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
    public static final EntityType<RepulsionGelOrbEntity> REPULSION_GEL_ORB = FabricEntityTypeBuilder.<RepulsionGelOrbEntity>create(SpawnGroup.MISC, RepulsionGelOrbEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
            .trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
            .build(); // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS

    private static <T extends Entity> void registerEntity(Consumer<EntityType<T>> setEntityType, Supplier<EntityType<T>> getEntityType, String id, EntityType.EntityFactory<T> constructor, Registry<EntityType<?>> registry) {
        EntityType<T> entityType = FabricEntityTypeBuilder.create(SpawnGroup.MISC, constructor).dimensions(new EntityDimensions(1.0F, 1.0F, true)).fireImmune().trackable(96, 20).build();
        setEntityType.accept(entityType);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(id), entityType);
    }
    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "cube"), CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "companion_cube"), COMPANION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "portal_placeholder"), PORTAL_PLACEHOLDER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "gel_orb"), GEL_ORB);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "repulsion_gel_orb"), REPULSION_GEL_ORB);
        //Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID,"customportal"),CustomPortalEntity.entityType);

        EntityRendererRegistry.INSTANCE.register(GEL_ORB, (dispatcher, context) -> new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer()));
        EntityRendererRegistry.INSTANCE.register(REPULSION_GEL_ORB, (dispatcher, context) -> new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer()));

        DefaultedRegistry<EntityType<?>> registry = Registry.ENTITY_TYPE;
        registerEntity((o) -> {
            CustomPortalEntity.entityType = o;
        }, () -> {
            return CustomPortalEntity.entityType;
        }, "thinkingwithportatos:custom_portal", CustomPortalEntity::new, registry);
    }
}
