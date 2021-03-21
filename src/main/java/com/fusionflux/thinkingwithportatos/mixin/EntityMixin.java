package com.fusionflux.thinkingwithportatos.mixin;

import com.fusionflux.thinkingwithportatos.accessor.VelocityTransfer;
import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.entity.EntityAttachments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAttachments, VelocityTransfer {

    @Unique
    private static final TrackedData<Boolean> IS_ROLLING = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Direction> DIRECTION = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.FACING);
    @Unique
    private final double storeVelocity3 = 0;
    public Vec3d movementTest = new Vec3d(0, 0, 0);
    @Shadow
    public World world;
    @Shadow
    public boolean horizontalCollision;
    @Shadow
    public boolean verticalCollision;

    //private Portal portal;
    @Shadow
    public boolean noClip;
    @Shadow
    public float horizontalSpeed;
    @Shadow
    protected boolean onGround;
    @Unique
    private double maxFallSpeed = 0;
    @Unique
    private double storeVelocity1 = 0;
    @Unique
    private double storeVelocity2 = 0;
    @Unique
    private double speedTransformApply = 0;

    @Override
    public double getMaxFallSpeed() {
        return maxFallSpeed;
    }

    @Override
    public void setMaxFallSpeed(double maxFallSpeed) {
        this.maxFallSpeed = maxFallSpeed;
    }

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    public abstract void setVelocity(Vec3d velocity);

    @Shadow
    public abstract void addVelocity(double deltaX, double deltaY, double deltaZ);

    @Shadow
    public abstract boolean collidesWith(Entity other);

    @Shadow
    protected abstract boolean doesNotCollide(Box box);

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract boolean isInsideWall();

    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    public abstract boolean collides();

    @Shadow
    public abstract boolean equals(Object o);

    @Shadow
    public abstract EntityType<?> getType();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (maxFallSpeed == 10 && world.getBlockState(this.getBlockPos()).getBlock() == ThinkingWithPortatosBlocks.PROPULSION_GEL) {
            maxFallSpeed = 10;
        } else {
            if (maxFallSpeed > 0) {
                maxFallSpeed = maxFallSpeed - 1;
            }
        }

        if (world.isClient) {
            storeVelocity2 = storeVelocity1;
            storeVelocity1 = this.getVelocity().length();

            /*-----------
            if (storeVelocity3 > storeVelocity1 && storeVelocity3 > storeVelocity2) {
                speedTransformApply=storeVelocity3;
            }
            -----------*/
        }

        speedTransformApply = Math.max(storeVelocity1, storeVelocity2);

    }

    @Override
    public double getVelocityTransfer() {
        return this.speedTransformApply;
    }

    @Override
    public void setVelocityTransfer(double speedValueTransferDuck) {
        this.speedTransformApply = speedValueTransferDuck;
    }

    /*----------
    @Inject(method = "calculateDimensions", at = @At("TAIL"))
    public void calculateDimensions(CallbackInfo ci) {
        EntityPose entityPose2 = this.getPose();
        EntityDimensions entityDimensions3 = this.getDimensions(entityPose2);
        this.standingEyeHeight = this.getEyeHeight(entityPose2, entityDimensions3) - 1;
    }
    ----------*/
}
