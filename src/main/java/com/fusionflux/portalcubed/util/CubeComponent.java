package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
