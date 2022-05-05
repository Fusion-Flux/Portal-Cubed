package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public interface PortalCubedComponent extends Component {
    boolean getSwapGravity();

    void setSwapGravity(boolean gravityState);

    Box getPoralAdjustBoundingBox();

    void setPoralAdjustBoundingBox(Box entityBB);

    Vec3d getOmmitDirection();

    void setOmmitDirection(Vec3d directions);

}
