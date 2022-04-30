package com.fusionflux.portalcubed.util;

import net.minecraft.nbt.NbtCompound;

public class TimerComponent implements PortalCubedComponent {
    boolean gravityState = false;

    @Override
    public boolean getSwapGravity() {
        return gravityState;
    }

    @Override
    public void setSwapGravity(boolean gravityState) {
        this.gravityState = gravityState;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }
}
