package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class PortalCubedComponents implements EntityComponentInitializer {

    public static final ComponentKey<PortalCubedComponent> GRAVITY_TIMER =
            ComponentRegistry.getOrCreate(new Identifier("portalcubed", "gravitytimer"), PortalCubedComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, GRAVITY_TIMER, e -> new TimerComponent());
    }
}
