package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;
import java.util.UUID;

public interface PortalCubedComponent extends Component {
    boolean getSwapGravity();

    void setSwapGravity(boolean gravityState);

    UUID getCubeUUID();

    void setCubeUUID(UUID cubeUUID);

    List<UUID> getPortals();

    void setPortals(List<UUID> portalUUIDs);

    void addPortals(UUID portalUUID);

    void removePortals(UUID portalUUID);

    VoxelShape getPortalCutout();

    void setPortalCutout(VoxelShape portalCutout);

    boolean getHasTeleportationHappened();

    void setHasTeleportationHappened(boolean hasHappened);

    void teleport(Vec3d teleportTo, Direction dira, Direction dirb);

}
