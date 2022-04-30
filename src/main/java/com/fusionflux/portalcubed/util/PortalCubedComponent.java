package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface PortalCubedComponent extends Component {
    boolean getSwapGravity();

    void setSwapGravity(boolean gravityState);
}
