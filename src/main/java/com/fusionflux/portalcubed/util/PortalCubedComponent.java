package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface PortalCubedComponent extends Component {
    boolean getSwapGravity();

    void setSwapGravity(boolean gravityState);

    UUID getCubeUUID();

    void setCubeUUID(UUID cubeUUID);

    Vec3d getOmmitDirection();

    void setOmmitDirection(Vec3d directions);

}
