package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.util.PortalCubedComponent;
import com.fusionflux.portalcubed.util.PortalCubedComponents;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class CalledValues {

    // workaround for a CCA bug; maybeGet throws an NPE in internal code if the DataTracker isn't initialized
    // null check the component container to avoid it
    // TODO: Check if this is still needed
    @SuppressWarnings({"ConstantValue", "UnstableApiUsage"})
    private static <C extends Component, V> Optional<C> maybeGetSafe(ComponentKey<C> key, @Nullable V provider) {
        if (provider instanceof ComponentProvider p) {
            var cc = p.getComponentContainer();
            if (cc != null) {
                return Optional.ofNullable(key.getInternal(cc));
            }
        }
        return Optional.empty();
    }

    public static VoxelShape getPortalCutout(Entity entity) {
        return maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).map(PortalCubedComponent::getPortalCutout).orElse(VoxelShapes.empty());
    }

    public static void setPortalCutout(Entity entity, VoxelShape setValue) {
        maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).ifPresent(gc -> gc.setPortalCutout(setValue));
    }

    public static boolean getHasTeleportationHappened(Entity entity) {
        return maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).map(PortalCubedComponent::getHasTeleportationHappened).orElse(false);
    }

    public static void setHasTeleportationHappened(Entity entity, boolean setValue) {
        maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).ifPresent(gc -> gc.setHasTeleportationHappened(setValue));
    }

    public static boolean getWasInfiniteFalling(Entity entity) {
        return maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).map(PortalCubedComponent::getWasInfiniteFalling).orElse(false);
    }
    public static void setWasInfiniteFalling(Entity entity, boolean setValue) {
        maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).ifPresent(gc -> gc.setWasInfiniteFalling(setValue));
    }

    public static Vec3d getVelocityUpdateAfterTeleport(Entity entity) {
        return maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).map(PortalCubedComponent::getVelocityUpdateAfterTeleport).orElse(null);
    }

    public static void setVelocityUpdateAfterTeleport(Entity entity, Vec3d setValue) {
        maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).ifPresent(gc -> gc.setVelocityUpdateAfterTeleport(setValue));
    }

    public static boolean getCanFireGel(Entity entity) {
        return maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).map(PortalCubedComponent::getCanFireGel).orElse(false);
    }
    public static void setCanFireGel(Entity entity, boolean setValue) {
        maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).ifPresent(gc -> gc.setCanFireGel(setValue));
    }

    public static Vec3d getServerVelForGel(Entity entity) {
        return maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).map(PortalCubedComponent::getServerVelForGel).orElse(null);
    }

    public static void setServerVelForGel(Entity entity, Vec3d setValue) {
        maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).ifPresent(gc -> gc.setServerVelForGel(setValue));
    }

    public static Set<UUID> getPortals(Entity entity) {
        return maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).map(PortalCubedComponent::getPortals).orElse(Set.of());
    }

    public static void addPortals(Entity entity, UUID setValue) {
        maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).ifPresent(gc -> gc.addPortals(setValue));
    }

    public static void removePortals(Entity entity, UUID setValue) {
        maybeGetSafe(PortalCubedComponents.ENTITY_COMPONENT, entity).ifPresent(gc -> gc.removePortals(setValue));
    }

}
