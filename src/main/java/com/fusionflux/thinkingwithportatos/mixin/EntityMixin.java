package com.fusionflux.thinkingwithportatos.mixin;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.accessor.EntityPortalsAccess;
import com.fusionflux.thinkingwithportatos.accessor.VelocityTransfer;
import com.fusionflux.thinkingwithportatos.blocks.RepulsionGel;
import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.entity.CustomPortalEntity;
import com.fusionflux.thinkingwithportatos.entity.EntityAttachments;
import com.fusionflux.thinkingwithportatos.entity.PortalPlaceholderEntity;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.google.common.collect.Lists;
import com.qouteall.immersive_portals.teleportation.CollisionHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAttachments, VelocityTransfer, EntityPortalsAccess {
    @Shadow
    public World world;
    @Shadow
    public int age;
    @Shadow
    public boolean horizontalCollision;
    @Shadow
    public boolean verticalCollision;
    @Unique
    private int timeinblock = 1;
    @Unique
    private double maxFallSpeed = 0;
    @Unique
    private final double repulsionGelSoundLimiter = 0;
    @Unique
    private double storeVelocity1 = 0;
    @Unique
    private double storeVelocity2 = 0;
    @Unique
    private double speedTransformApply = 0;
    @Unique
    private boolean recentlyTouchedPortal;
    @Unique
    private final List<CustomPortalEntity> portalList = Lists.newArrayList();

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
    public abstract Box getBoundingBox();

    @Shadow
    public abstract boolean equals(Object o);

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract Vec3d getPos();

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Shadow
    public abstract float getEyeHeight(EntityPose pose);

    @Shadow
    public abstract EntityPose getPose();

    @Shadow
    public abstract boolean isAlive();

    @Shadow
    public abstract boolean isSneaking();

    @Override
    public List<CustomPortalEntity> getPortalList() {
        return portalList;
    }

    @Override
    public void addPortalToList(CustomPortalEntity portal) {
        portalList.add(portal);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        Vec3d expand = this.getVelocity().multiply(10);
        Box streachedBB = this.getBoundingBox().stretch(expand);

        List<CustomPortalEntity> globalPortals = this.world.getEntitiesByClass(CustomPortalEntity.class, streachedBB, null);

        for (CustomPortalEntity globalPortal : globalPortals) {
            if (streachedBB.intersects(globalPortal.getBoundingBox())) {
                Vec3d portalFacing = new Vec3d((int) globalPortal.getNormal().getX(), (int) globalPortal.getNormal().getY(), (int) globalPortal.getNormal().getZ());
                double offsetX = 0;
                double offsetZ = 0;
                double offsetY = 0;

                Box streachedPortalBB = globalPortal.getBoundingBox().stretch(portalFacing.getX() * Math.abs(this.getVelocity().getX())*10, portalFacing.getY() * Math.abs(this.getVelocity().getY())*10, portalFacing.getZ() * Math.abs(this.getVelocity().getZ())*10);
                if (streachedPortalBB.intersects(this.getBoundingBox())){
                    if (Math.abs(this.getVelocity().y) > Math.abs(this.getVelocity().x) || Math.abs(this.getVelocity().z) > Math.abs(this.getVelocity().x)) {
                        offsetX = (this.getBoundingBox().getCenter().x - globalPortal.getBoundingBox().getCenter().x) * .05;
                    }
                if (Math.abs(this.getVelocity().y) > Math.abs(this.getVelocity().z) || Math.abs(this.getVelocity().x) > Math.abs(this.getVelocity().z)) {
                    offsetZ = (this.getBoundingBox().getCenter().z - globalPortal.getBoundingBox().getCenter().z) * .05;
                }
                if (Math.abs(this.getVelocity().z) > Math.abs(this.getVelocity().y) || Math.abs(this.getVelocity().x) > Math.abs(this.getVelocity().y)) {
                    offsetY = (this.getBoundingBox().getCenter().y - globalPortal.getBoundingBox().getCenter().y) * .05;
                }
                if (!this.getBoundingBox().intersects(globalPortal.getBoundingBox()) && !this.isSneaking())
                    this.setVelocity(this.getVelocity().add(-offsetX, -offsetY, -offsetZ));
            }
            }
        }

        if (!world.isClient) {
            if (world.getBlockState(new BlockPos(this.getX(), this.getY() + this.getEyeHeight(this.getPose()), this.getZ())) == ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK.getDefaultState()) {
                if (Math.abs(timeinblock - this.age) > 20) {
                    this.damage(DamageSource.DROWN, 2);
                }
            } else {
                timeinblock = this.age;
            }

            List<Entity> portalSound = this.world.getEntitiesByClass(CustomPortalEntity.class, this.getBoundingBox().expand(2), null);

            for (Entity globalportal : portalSound) {
                CustomPortalEntity collidingportal = (CustomPortalEntity) globalportal;
                collidingportal.getActive();

                if (CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this)) && collidingportal.getActive() && !recentlyTouchedPortal) {
                    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), ThinkingWithPortatosSounds.ENTITY_ENTER_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                    recentlyTouchedPortal = true;
                }

                if (!CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this)) && collidingportal.getActive() && recentlyTouchedPortal) {
                    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), ThinkingWithPortatosSounds.ENTITY_EXIT_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                    recentlyTouchedPortal = false;
                }
            }
        }

        if (maxFallSpeed == 10 && world.getBlockState(this.getBlockPos()).getBlock() == ThinkingWithPortatosBlocks.PROPULSION_GEL) {
            maxFallSpeed = 10;
        } else {
            if (maxFallSpeed > 0) {
                maxFallSpeed = maxFallSpeed - 1;
            }
        }


        if (world.getBlockState(this.getBlockPos()).getBlock() == ThinkingWithPortatosBlocks.REPULSION_GEL) {
            BlockState state = world.getBlockState(this.getBlockPos());
            Vec3d direction = new Vec3d(0, 0, 0);
            if (this.verticalCollision) {
                if (state.get(RepulsionGel.UP)) {
                    direction = direction.add(0, -2, 0);
                }

                if (state.get(RepulsionGel.DOWN)) {
                    direction = direction.add(0, 2, 0);
                }

            }
            if (this.horizontalCollision) {
                if (state.get(RepulsionGel.NORTH)) {
                    direction = direction.add(0, 0, 1);
                }

                if (state.get(RepulsionGel.SOUTH)) {
                    direction = direction.add(0, 0, -1);
                }

                if (state.get(RepulsionGel.EAST)) {
                    direction = direction.add(-1, 0, 0);
                }

                if (state.get(RepulsionGel.WEST)) {
                    direction = direction.add(1, 0, 0);
                }
                direction = direction.add(0, 0.5, 0);
            }
            if (!this.isSneaking() && !direction.equals(new Vec3d(0, 0, 0))) {
                if (world.isClient && (Object) this instanceof PlayerEntity) {
                    world.playSound((PlayerEntity) (Object) this, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                } else {
                    world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                }
                this.setVelocity(this.getVelocity().multiply(1.4));
                this.setVelocity(this.getVelocity().add(direction.x, direction.y, direction.z));
            }
        } else if (world.getBlockState(new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() + 1, this.getBlockPos().getZ())).getBlock() == ThinkingWithPortatosBlocks.REPULSION_GEL) {
            BlockState state = world.getBlockState(new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() + 1, this.getBlockPos().getZ()));
            Vec3d direction = new Vec3d(0, 0, 0);
            if (this.verticalCollision) {
                if (state.get(RepulsionGel.UP)) {
                    direction = direction.add(0, -1, 0);
                }

                if (state.get(RepulsionGel.DOWN)) {
                    direction = direction.add(0, 1, 0);
                }

            }
            if (this.horizontalCollision) {
                if (state.get(RepulsionGel.NORTH)) {
                    direction = direction.add(0, 0, 1);
                }

                if (state.get(RepulsionGel.SOUTH)) {
                    direction = direction.add(0, 0, -1);
                }

                if (state.get(RepulsionGel.EAST)) {
                    direction = direction.add(-1, 0, 0);
                }

                if (state.get(RepulsionGel.WEST)) {
                    direction = direction.add(1, 0, 0);
                }
                direction = direction.add(0, 0.45, 0);
            }
            if (!this.isSneaking() && !direction.equals(new Vec3d(0, 0, 0))) {
                if (world.isClient && (Object) this instanceof PlayerEntity) {
                    world.playSound((PlayerEntity) (Object) this, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                } else {
                    world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                }
                this.setVelocity(this.getVelocity().add(direction.x, direction.y, direction.z));
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

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public void remove(CallbackInfo ci) {
        if (!world.isClient) {
            for (CustomPortalEntity checkedportal : portalList) {
                if (checkedportal != null) {
                    if (!checkedportal.getOutline().equals("null")) {
                        PortalPlaceholderEntity portalOutline;
                        portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(UUID.fromString(checkedportal.getOutline()));
                        assert portalOutline != null;
                        if (portalOutline != null) {
                            portalOutline.kill();
                        }
                    }
                    world.playSound(null, checkedportal.getPos().getX(), checkedportal.getPos().getY(), checkedportal.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                    checkedportal.kill();
                }
            }
        }
    }


    /*----------
    @Inject(method = "calculateDimensions", at = @At("TAIL"))
    public void calculateDimensions(CallbackInfo ci) {
        EntityPose entityPose2 = this.getPose();
        EntityDimensions entityDimensions3 = this.getDimensions(entityPose2);
        this.standingEyeHeight = this.getEyeHeight(entityPose2, entityDimensions3) - 1;
    }
    ----------*/

    @Inject(method = "fall", at = @At("HEAD"), cancellable = true)
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
        if (ThinkingWithPortatos.getBodyGrabbingManager(world.isClient).isGrabbed((Entity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo ci) {
        if (ThinkingWithPortatos.getBodyGrabbingManager(world.isClient).isGrabbed((Entity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "getVelocity", at = @At("HEAD"), cancellable = true)
    public void getVelocity(CallbackInfoReturnable<Vec3d> ci) {
        if (ThinkingWithPortatos.getBodyGrabbingManager(world.isClient).isGrabbed((Entity) (Object) this)) {
            ci.setReturnValue(Vec3d.ZERO);
        }
    }
}
