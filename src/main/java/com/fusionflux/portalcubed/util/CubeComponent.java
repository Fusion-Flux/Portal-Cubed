package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

public class CubeComponent implements ButtonComponent, AutoSyncedComponent {
    boolean buttonState = false;
    private final CorePhysicsEntity entity;


    public CubeComponent(CorePhysicsEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean getOnButton() {
        return buttonState;
    }

    @Override
    public void setOnButton(boolean buttonON) {
        this.buttonState = buttonON;
        PortalCubedComponents.CUBE_COMPONENT.sync(entity);
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        buttonState = tag.getBoolean("buttonState");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("buttonState",buttonState);
    }
}
