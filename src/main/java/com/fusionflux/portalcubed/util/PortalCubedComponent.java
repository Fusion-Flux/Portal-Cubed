package com.fusionflux.portalcubed.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface PortalCubedComponent extends Component {

    Set<UUID> getPortals();

    void addPortals(UUID portalUUID);

    void removePortals(UUID portalUUID);

    VoxelShape getPortalCutout();

    void setPortalCutout(VoxelShape cutout);

    VoxelShape getCrossPortalCollision();

    void setCrossPortalCollision(VoxelShape collision);

    boolean getHasTeleportationHappened();

    void setHasTeleportationHappened(boolean hasHappened);

    boolean getWasInfiniteFalling();

    void setWasInfiniteFalling(boolean infFall);

    Vec3 getVelocityUpdateAfterTeleport();

    void setVelocityUpdateAfterTeleport(Vec3 velocity);

    boolean getCanFireGel();

    void setCanFireGel(boolean canGel);

    Vec3 getServerVelForGel();

    void setServerVelForGel(Vec3 velocity);

    @Nullable
    BlockPos getLauncher();

    void setLauncher(@Nullable BlockPos launcher);

}
