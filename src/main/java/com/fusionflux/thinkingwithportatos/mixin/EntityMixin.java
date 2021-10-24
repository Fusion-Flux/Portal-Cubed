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
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.core.teleportation.CollisionHelper;

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
    private double storeVelocity1 = 0;
    @Unique
    private double storeVelocity2 = 0;
    @Unique
    private double speedTransformApply = 0;
    @Unique
    private boolean recentlyTouchedPortal;
    @Unique
    private final List<CustomPortalEntity> portalList = Lists.newArrayList();

    @Unique
    private boolean IN_FUNNEL = false;

    @Unique
    private boolean IS_BOUNCED = false;

    @Unique
    private int FUNNEL_TIMER = 0;

    @Unique
    private static boolean IsInitalized = false;

    @Unique
    private static boolean IsInitalized2 = false;

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
    public abstract EntityPose getPose();

    @Shadow
    public abstract boolean isAlive();

    @Shadow
    public abstract boolean isSneaking();


    @Shadow public abstract void setNoGravity(boolean noGravity);

    @Shadow public abstract boolean hasNoGravity();

    @Shadow @Final protected DataTracker dataTracker;

    @Shadow protected abstract void onBlockCollision(BlockState state);

    @Shadow public abstract int getId();

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
if(world.isClient()) {

        if (this.isInFunnel() && this.getFunnelTimer() != 0) {
            this.setFunnelTimer(this.getFunnelTimer() - 1);
        }
        if (this.isInFunnel() && this.getFunnelTimer() == 0 && this.hasNoGravity()) {
            this.setNoGravity(false);
            setInFunnel(false);
        }

}
        Vec3d expand = this.getVelocity().multiply(10);
        Box streachedBB = this.getBoundingBox().stretch(expand);

        List<CustomPortalEntity> globalPortals = this.world.getEntitiesByClass(CustomPortalEntity.class, streachedBB, e -> true);

        /*for (CustomPortalEntity globalPortal : globalPortals) {
            if (streachedBB.intersects(globalPortal.getBoundingBox())) {
                Vec3d portalFacing = new Vec3d((int) globalPortal.getNormal().getX(), (int) globalPortal.getNormal().getY(), (int) globalPortal.getNormal().getZ());
                double offsetX = 0;
                double offsetZ = 0;
                double offsetY = 0;

                Box streachedPortalBB = globalPortal.getBoundingBox().stretch(portalFacing.getX() * Math.abs(this.getVelocity().getX())*10, portalFacing.getY() * Math.abs(this.getVelocity().getY())*10, portalFacing.getZ() * Math.abs(this.getVelocity().getZ())*10);
                if (streachedPortalBB.intersects(this.getBoundingBox())){
                    if (Math.abs(this.getVelocity().y) > Math.abs(this.getVelocity().x) || Math.abs(this.getVelocity().z) > Math.abs(this.getVelocity().x)) {
                        offsetX = (this.getBoundingBox().getCenter().x - globalPortal.getBoundingBox().getCenter().x) * .03;
                    }
                if (Math.abs(this.getVelocity().y) > Math.abs(this.getVelocity().z) || Math.abs(this.getVelocity().x) > Math.abs(this.getVelocity().z)) {
                    offsetZ = (this.getBoundingBox().getCenter().z - globalPortal.getBoundingBox().getCenter().z) * .03;
                }
                if (Math.abs(this.getVelocity().z) > Math.abs(this.getVelocity().y) || Math.abs(this.getVelocity().x) > Math.abs(this.getVelocity().y)) {
                    offsetY = (this.getBoundingBox().getCenter().y - globalPortal.getBoundingBox().getCenter().y) * .03;
                }
                if (!this.getBoundingBox().intersects(globalPortal.getBoundingBox()) && !this.isSneaking())
                    this.setVelocity(this.getVelocity().add(-offsetX, -offsetY, -offsetZ));
            }
            }
        }*/

        if (!world.isClient) {

            List<CustomPortalEntity> portalSound = this.world.getEntitiesByClass(CustomPortalEntity.class, this.getBoundingBox().expand(2), e -> true);

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
            ((EntityAttachments) this).setBounced(true);
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
            ((EntityAttachments) this).setBounced(true);
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


    /*@Inject(method = "initDataTracker", at = @At("RETURN"), cancellable = true)
    public void initTracker(CallbackInfo info) {
        dataTracker.startTracking(IN_FUNNEL, false);
    }*/

    /*@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"), cancellable = true)
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        NbtCompound rootTag = tag.getCompound(ThinkingWithPortatos.MODID);

        dataTracker.set(IN_FUNNEL, rootTag.getBoolean("IsInFunnel"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"), cancellable = true)
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        NbtCompound rootTag = new NbtCompound();

        tag.put(ThinkingWithPortatos.MODID, rootTag);
        rootTag.putBoolean("IsInFunnel", dataTracker.get(IN_FUNNEL));
    }*/


    @Override
    public boolean isInFunnel() {
        return this.IN_FUNNEL;
    }

    @Override
    public void setInFunnel(boolean inFunnel) {
        this.IN_FUNNEL=inFunnel;
    }

    @Override
    public boolean isBounced() {
        return this.IS_BOUNCED;
    }

    @Override
    public void setBounced(boolean bounced) {
        this.IS_BOUNCED=bounced;
    }

    @Override
    public int getFunnelTimer() {

        return this.FUNNEL_TIMER;
    }

    @Override
    public void setFunnelTimer(int funnelTimer) {
        this.FUNNEL_TIMER=funnelTimer;
    }

    /*----------
    @Inject(method = "calculateDimensions", at = @At("TAIL"))
    public void calculateDimensions(CallbackInfo ci) {
        EntityPose entityPose2 = this.getPose();
        EntityDimensions entityDimensions3 = this.getDimensions(entityPose2);
        this.standingEyeHeight = this.getEyeHeight(entityPose2, entityDimensions3) - 1;
    }
    ----------*/

    /*protected void checkBlockCollision() {
        Box box = this.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
        if (this.world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                    for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                        mutable.set(i, j, k);
                        BlockState blockState = this.world.getBlockState(mutable);


                    }
                }
            }
        }

    }*/

}
