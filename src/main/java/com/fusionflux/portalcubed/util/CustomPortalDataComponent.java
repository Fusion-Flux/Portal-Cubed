package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface CustomPortalDataComponent extends Component {
    Vec3d getAxisW();

    Vec3d getAxisH();

    Vec3d getOtherAxisH();

    void setOtherAxisH(Vec3d Destination);

    Vec3d getDestination();

    void setDestination(Vec3d Destination);

    Vec3d getOtherFacing();

    void setOtherFacing(Vec3d Facing);

    UUID getPlayer();

    void setPlayer(UUID player);

    void setOrientation(Vec3d AxisW,Vec3d AxisH);


}
