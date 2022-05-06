package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class PortalCubedComponents implements EntityComponentInitializer {

    public static final ComponentKey<PortalCubedComponent> ENTITY_COMPONENT =
            ComponentRegistry.getOrCreate(new Identifier("portalcubed", "entity_component"), PortalCubedComponent.class);

    public static final ComponentKey<CustomPortalDataComponent> PORTAL_DATA =
            ComponentRegistry.getOrCreate(new Identifier("portalcubed", "portal_data"), CustomPortalDataComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, ENTITY_COMPONENT, EntityComponent::new);
        registry.registerFor(ExperimentalPortal.class, PORTAL_DATA, PortalDataComponent::new);
    }
}
