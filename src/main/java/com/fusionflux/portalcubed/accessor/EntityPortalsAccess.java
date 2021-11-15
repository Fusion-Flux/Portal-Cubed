package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.entity.CustomPortalEntity;

import java.util.List;

public interface EntityPortalsAccess {
    List<CustomPortalEntity> getPortalList();

    void addPortalToList(CustomPortalEntity portal);
}
