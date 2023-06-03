package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedComponents implements EntityComponentInitializer {
    public static final ComponentKey<PortalCubedComponent> ENTITY_COMPONENT = ComponentRegistry.getOrCreate(id("entity_component"), PortalCubedComponent.class);
    public static final ComponentKey<HolderComponent> HOLDER_COMPONENT = ComponentRegistry.getOrCreate(id("holder_component"), HolderComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, ENTITY_COMPONENT, EntityComponent::new);
        registry.registerFor(Player.class, HOLDER_COMPONENT, HolderComponent::new);
    }
}
