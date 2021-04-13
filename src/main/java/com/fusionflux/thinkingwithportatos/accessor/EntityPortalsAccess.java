package com.fusionflux.thinkingwithportatos.accessor;

import com.fusionflux.thinkingwithportatos.entity.CustomPortalEntity;
import com.qouteall.immersive_portals.portal.Portal;

import java.util.List;

public interface EntityPortalsAccess {
    List<CustomPortalEntity> getPortalList();

    void addPortalToList(CustomPortalEntity portal);
}
