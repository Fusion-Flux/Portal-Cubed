package com.fusionflux.portalcubed.accessor;

import java.util.Optional;

import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;

public interface AdvancedRaycastResultHolder {
    Optional<AdvancedEntityRaycast.Result> getResult();
    void setResult(Optional<AdvancedEntityRaycast.Result> result);
}
