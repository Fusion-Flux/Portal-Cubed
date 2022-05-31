package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;

import java.util.List;

public interface EntityPortalsAccess {
    List<ExperimentalPortal> getPortalList();

    void addPortalToList(ExperimentalPortal portal);

}
