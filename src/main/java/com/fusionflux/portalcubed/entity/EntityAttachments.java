package com.fusionflux.portalcubed.entity;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

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

    double getLastMaxFallHeight();

    Vec3d getLastVel();

    void setMaxFallHeight(double maxFallHeight);


}
