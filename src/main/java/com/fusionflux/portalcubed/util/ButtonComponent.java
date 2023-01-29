package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface ButtonComponent extends Component {
    boolean getOnButton();

    void setOnButton(boolean gravityState);

}
