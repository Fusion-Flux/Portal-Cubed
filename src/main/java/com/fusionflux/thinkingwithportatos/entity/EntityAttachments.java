package com.fusionflux.thinkingwithportatos.entity;

import net.minecraft.util.math.Direction;

public interface EntityAttachments {

    boolean isRolling();

    void setRolling(boolean rolling);

    Direction getDirection();

    void setDirection(Direction direction);

    double getMaxFallSpeed();

    void setMaxFallSpeed(double maxFallSpeed);



}
