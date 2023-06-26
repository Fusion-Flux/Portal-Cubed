package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.blocks.blockentities.CatapultBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import net.minecraft.world.phys.Vec3;

public interface EntityExt {

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

    Vec3 getLastVel();

    void setMaxFallHeight(double maxFallHeight);

    boolean cfg();

    void setCFG();

    void collidedWithVelocityHelper(VelocityHelperBlockEntity block);

    void collidedWithCatapult(CatapultBlockEntity block);
}
