package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.UUID;

public interface PortalCubedComponent extends Component {
    boolean getSwapGravity();

    void setSwapGravity(boolean gravityState);

    UUID getCubeUUID();

    void setCubeUUID(UUID cubeUUID);

    VoxelShape getPortalCutout();

    void setPortalCutout(VoxelShape portalCutout);

    Vec3d getPosition();

    void setPosition(Vec3d velocity);

    void teleport(Vec3d teleportTo, Direction dira, Direction dirb);

}
