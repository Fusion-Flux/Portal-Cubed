package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.qouteall.immersive_portals.portal.Portal;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
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

    private static <T extends Entity> void registerEntity(Consumer<EntityType<T>> setEntityType, Supplier<EntityType<T>> getEntityType, String id, EntityType.EntityFactory<T> constructor, Registry<EntityType<?>> registry) {
        EntityType<T> entityType = FabricEntityTypeBuilder.create(SpawnGroup.MISC, constructor).dimensions(new EntityDimensions(1.0F, 1.0F, true)).fireImmune().trackable(96, 20).build();
        setEntityType.accept(entityType);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(id), entityType);
    }
    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "cube"), CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "companion_cube"), COMPANION_CUBE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID, "portal_placeholder"), PORTAL_PLACEHOLDER);
        //Registry.register(Registry.ENTITY_TYPE, new Identifier(ThinkingWithPortatos.MODID,"customportal"),CustomPortalEntity.entityType);
        DefaultedRegistry<EntityType<?>> registry = Registry.ENTITY_TYPE;
        registerEntity((o) -> {
            CustomPortalEntity.entityType = o;
        }, () -> {
            return CustomPortalEntity.entityType;
        }, "thinkingwithportatos:custom_portal", CustomPortalEntity::new, registry);
    }
}
