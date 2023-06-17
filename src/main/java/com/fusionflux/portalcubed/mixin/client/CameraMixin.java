package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CameraExt;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExt {
    @Shadow
    private BlockGetter level;
    @Shadow
    @Final
    private BlockPos.MutableBlockPos blockPosition;

    @Shadow private Entity entity;

    @Shadow private boolean initialized;

    @Shadow protected abstract void move(double distanceOffset, double verticalOffset, double horizontalOffset);

    @Shadow protected abstract double getMaxZoom(double startingDistance);

    @Shadow protected abstract void setPosition(Vec3 pos);

    @Shadow @Final private Vector3f forwards;

    @Shadow @Final private Vector3f up;

    @Shadow @Final private Vector3f left;

    @Shadow @Final private Quaternionf rotation;

    @Override
    public FluidState portalcubed$getSubmergedFluidState() {
        return this.level.getFluidState(blockPosition);
    }

    @Override
    public void updateSimple(BlockGetter area, Entity focusedEntity) {
        this.level = area;
        this.entity = focusedEntity;
        initialized = true;
    }

    @Override
    public void backCameraUp(Vec3 from) {
        setPosition(from);
        move(-getMaxZoom(4), 0, 0);
    }

}
