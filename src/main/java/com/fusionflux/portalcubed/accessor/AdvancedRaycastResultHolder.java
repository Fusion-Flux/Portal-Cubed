package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;

import java.util.Optional;

public interface AdvancedRaycastResultHolder {
    Optional<AdvancedEntityRaycast.Result> getResult();
    void setResult(Optional<AdvancedEntityRaycast.Result> result);
}
