package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.entity.CustomPortalEntity;

import java.util.List;
import java.util.UUID;

public interface EntityPortalsAccess {
    List<CustomPortalEntity> getPortalList();

    void addPortalToList(CustomPortalEntity portal);

    UUID getCubeUUID();

    boolean getUUIDPresent();

    void setCubeUUID(UUID uuid);
}
