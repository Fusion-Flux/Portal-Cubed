package com.fusionflux.thinkingwithportatos.mixin;

import com.fusionflux.thinkingwithportatos.accessor.VelocityTransfer;
import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.entity.CustomPortalEntity;
import com.fusionflux.thinkingwithportatos.entity.EntityAttachments;
import com.qouteall.immersive_portals.Global;
import com.qouteall.immersive_portals.McHelper;
import com.qouteall.immersive_portals.ducks.IEEntity;
import com.qouteall.immersive_portals.mixin.common.collision.MixinEntity;
import com.qouteall.immersive_portals.portal.Portal;
import com.qouteall.immersive_portals.teleportation.CollisionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

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

    @Shadow public abstract double offsetX(double widthScale);

    @Shadow public abstract boolean canFly();

    @Shadow public abstract Direction getMovementDirection();

   // @Shadow public abstract Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, ReusableStream<VoxelShape> collisions);

    @Shadow protected abstract Vec3d adjustMovementForCollisions(Vec3d movement);

    @Shadow protected abstract Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type);

    @Shadow @Final private EntityType<?> type;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        Vec3d expand = this.getVelocity().multiply(10);
        Box streachedBB = this.getBoundingBox().stretch(expand);

        List<Entity> globalPortals = this.world.getEntitiesByClass(CustomPortalEntity.class, streachedBB, null);
            for (Entity globalPortal : globalPortals) {
                if (streachedBB.intersects(globalPortal.getBoundingBox())) {
                    double offsetX=0;
                    double offsetZ=0;
                    double offsetY=0;
                    if(Math.abs(this.getVelocity().y) > Math.abs(this.getVelocity().x)||Math.abs(this.getVelocity().z) > Math.abs(this.getVelocity().x)) {
                        offsetX = (this.getBoundingBox().getCenter().x - globalPortal.getBoundingBox().getCenter().x)*.02;
                    }
                    if(Math.abs(this.getVelocity().y) > Math.abs(this.getVelocity().z)||Math.abs(this.getVelocity().x) > Math.abs(this.getVelocity().z)) {
                        offsetZ = (this.getBoundingBox().getCenter().z - globalPortal.getBoundingBox().getCenter().z)*.02;
                    }
                    if(Math.abs(this.getVelocity().z) > Math.abs(this.getVelocity().y)||Math.abs(this.getVelocity().x) > Math.abs(this.getVelocity().y)) {
                        offsetY = (this.getBoundingBox().getCenter().y - globalPortal.getBoundingBox().getCenter().y)*.02;
                    }
                    if(!this.getBoundingBox().intersects(globalPortal.getBoundingBox()))
                    this.setVelocity(this.getVelocity().add(-offsetX,-offsetY,-offsetZ));
                }
            }
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
