package com.fusionflux.portalcubed.entity;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface EntityAttachments {

    boolean isBounced();

    void setBounced(boolean bounced);

    boolean isInFunnel();

    void setInFunnel(boolean inFunnel);

    int getFunnelTimer();

    void setFunnelTimer(int funnelTimer);

    Direction getDirection();

    void setDirection(Direction direction);

    double getMaxFallSpeed();

    void setMaxFallSpeed(double maxFallSpeed);

    double getMaxFallHeight();

    int getGelTimer();

    void setGelTimer(int funnelTimer);

    int getGelChangeTimer();

    void setGelChangeTimer(int funnelTimer);

    Vec3d getLastVel();

    Vec3d getLastPos();

    void setMaxFallHeight(double maxFallHeight);

    void setServerVel(Vec3d serverVel);

    Vec3d getServerVel();

    void setTeleportUUID(UUID serverVel);

    UUID getTeleportUUID();

    void setShouldTeleport(boolean shouldTeleport);

    boolean getShouldTeleport();



}
