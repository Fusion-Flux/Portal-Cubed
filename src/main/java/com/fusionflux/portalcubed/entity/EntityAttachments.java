package com.fusionflux.portalcubed.entity;

import net.minecraft.util.math.Vec3d;

public interface EntityAttachments {

    boolean isBounced();

    void setBounced(boolean bounced);

    boolean isInFunnel();

    void setInFunnel(boolean inFunnel);

    int getFunnelTimer();

    void setFunnelTimer(int funnelTimer);

    double getMaxFallSpeed();

    void setMaxFallSpeed(double maxFallSpeed);

    double getMaxFallHeight();

    int getGelTimer();

    void setGelTimer(int funnelTimer);

    Vec3d getLastVel();

    void setMaxFallHeight(double maxFallHeight);

    boolean cfg();

    void setCFG();

}
