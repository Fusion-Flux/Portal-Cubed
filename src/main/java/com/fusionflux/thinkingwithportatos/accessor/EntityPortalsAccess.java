package com.fusionflux.thinkingwithportatos.accessor;

import com.qouteall.immersive_portals.portal.Portal;

import java.util.List;

public interface EntityPortalsAccess {
    List<Portal> getPortalList();

    void addPortalToList(Portal portal);
}
