package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;

import java.util.List;
import java.util.UUID;

public interface EntityPortalsAccess {
    List<ExperimentalPortal> getPortalList();

    void addPortalToList(ExperimentalPortal portal);

}
