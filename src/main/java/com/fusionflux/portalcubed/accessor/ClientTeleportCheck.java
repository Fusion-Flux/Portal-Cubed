package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;

public interface ClientTeleportCheck {

    boolean clientEntityTeleporting();

    void setClientEntityTeleporting(boolean teleport);

}
