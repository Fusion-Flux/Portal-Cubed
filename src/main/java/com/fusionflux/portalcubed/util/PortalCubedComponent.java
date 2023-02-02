package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.Set;
import java.util.UUID;

public interface PortalCubedComponent extends Component {

    UUID getCubeUUID();

    void setCubeUUID(UUID cubeUUID);

    Set<UUID> getPortals();

    void addPortals(UUID portalUUID);

    void removePortals(UUID portalUUID);

    VoxelShape getPortalCutout();

    void setPortalCutout(VoxelShape portalCutout);

    boolean getHasTeleportationHappened();

    void setHasTeleportationHappened(boolean hasHappened);

    boolean getWasInfiniteFalling();

    void setWasInfiniteFalling(boolean infFall);

    Vec3d getVelocityUpdateAfterTeleport();

    void setVelocityUpdateAfterTeleport(Vec3d velocity);

}
