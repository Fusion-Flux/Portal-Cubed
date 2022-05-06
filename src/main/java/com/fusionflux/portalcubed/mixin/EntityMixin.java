package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalPlaceholderEntity;
import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.google.common.collect.Lists;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAttachments, EntityPortalsAccess {
    @Shadow
    public World world;

    @Unique
    private double maxFallSpeed = 0;

    @Unique
    private final List<ExperimentalPortal> portalList = Lists.newArrayList();

    @Unique
    private boolean IN_FUNNEL = false;

    @Unique
    private double maxFallHeight = -99999999;

    @Unique
    private Vec3d lastVel = Vec3d.ZERO;

    @Unique
    private int gelTransferTimer = 0;

    @Unique
    private boolean IS_BOUNCED = false;

    @Unique
    private int FUNNEL_TIMER = 0;

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
    public abstract void setNoGravity(boolean noGravity);

    @Shadow
    public abstract boolean hasNoGravity();

    @Shadow public abstract boolean isOnGround();

    @Shadow private Vec3d pos;

    @Shadow protected abstract Box calculateBoundsForPose(EntityPose pos);

    @Shadow public boolean horizontalCollision;

    @Override
    public List<ExperimentalPortal> getPortalList() {
        return portalList;
    }

    @Override
    public void addPortalToList(ExperimentalPortal portal) {
        portalList.add(portal);
    }

    private static final Box nullBox = new Box(0, 0, 0, 0, 0, 0);

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
            //if(!(((Entity) (Object) this) instanceof ClientPlayerEntity) && !(((Entity) (Object) this) instanceof ServerPlayerEntity)){
        if(!this.world.isClient) {
            List<ExperimentalPortal> list = ((Entity) (Object) this).world.getNonSpectatingEntities(ExperimentalPortal.class, getBoundingBox());
            VoxelShape ommitedDirections = VoxelShapes.empty();
            for (ExperimentalPortal entity1 : list) {
                if(entity1.calculateCuttoutBox() != nullBox)
                ommitedDirections = VoxelShapes.union(ommitedDirections,VoxelShapes.cuboid(entity1.calculateCuttoutBox()));
            }
            CalledValues.setPortalCutout(((Entity) (Object) this), ommitedDirections);
        }

        if(this.world.getBlockState(this.getBlockPos()).getBlock() != PortalCubedBlocks.ADHESION_GEL && CalledValues.getSwapTimer((Entity)(Object)this)){
            CalledValues.setSwapTimer((Entity)(Object)this,false);
            GravityChangerAPI.setGravityDirection(((Entity) (Object)this), GravityChangerAPI.getDefaultGravityDirection((Entity) (Object)this));
        }

            if (this.isInFunnel() && this.getFunnelTimer() != 0) {
                this.setFunnelTimer(this.getFunnelTimer() - 1);
            }
            if (this.isInFunnel() && this.getFunnelTimer() == 0 && this.hasNoGravity()) {
                this.setNoGravity(false);
                setInFunnel(false);
            }




        if(this.gelTransferTimer != 0){
            this.gelTransferTimer -= 1;
        }

        //if (!world.isClient) {
//
        //    List<ExperimentalPortal> portalSound = this.world.getEntitiesByClass(ExperimentalPortal.class, this.getBoundingBox(), e -> true);
//
        //    for (Entity globalportal : portalSound) {
        //        ExperimentalPortal collidingportal = (ExperimentalPortal) globalportal;
        //        collidingportal.getActive();
//
        //        //if (CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this)) && collidingportal.getActive() && !recentlyTouchedPortal) {
        //        //    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), PortalCubedSounds.ENTITY_ENTER_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
        //        //    recentlyTouchedPortal = true;
        //        //}
////
        //        //if (!CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this)) && collidingportal.getActive() && recentlyTouchedPortal) {
        //        //    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), PortalCubedSounds.ENTITY_EXIT_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
        //        //    recentlyTouchedPortal = false;
        //        //}
        //    }
        //}

        if (maxFallSpeed == 10 && world.getBlockState(this.getBlockPos()).getBlock() == PortalCubedBlocks.PROPULSION_GEL) {
            maxFallSpeed = 10;
        } else {
            if (maxFallSpeed > 0) {
                maxFallSpeed = maxFallSpeed - 1;
            }
        }

        this.lastVel = this.getVelocity();

        Vec3d rotatedPos = this.pos;
        if((Object)this instanceof PlayerEntity){
            rotatedPos = RotationUtil.vecWorldToPlayer(this.pos, GravityChangerAPI.getGravityDirection((PlayerEntity)(Object)this));
        }
        if(!this.isOnGround()) {
            if (rotatedPos.y > this.maxFallHeight) {
                this.maxFallHeight = rotatedPos.y;
            }
        }else{
            this.maxFallHeight = rotatedPos.y;
        }

        if (world.getBlockState(this.getBlockPos()).getBlock() != PortalCubedBlocks.REPULSION_GEL && this.isBounced()){
            this.setBounced(false);
        }


    }


    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(CallbackInfo ci) {
        if (!world.isClient) {
            for (ExperimentalPortal checkedportal : portalList) {
                if (checkedportal != null) {
                    if (!checkedportal.getOutline().equals("null")) {
                        PortalPlaceholderEntity portalOutline;
                        portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(UUID.fromString(checkedportal.getOutline()));
                        assert portalOutline != null;
                        if (portalOutline != null) {
                            portalOutline.kill();
                        }
                    }
                    world.playSound(null, checkedportal.getPos().getX(), checkedportal.getPos().getY(), checkedportal.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                    checkedportal.kill();
                }
            }
        }
    }


    @Override
    public boolean isInFunnel() {
        return this.IN_FUNNEL;
    }

    @Override
    public void setInFunnel(boolean inFunnel) {
        this.IN_FUNNEL = inFunnel;
    }

    @Override
    public boolean isBounced() {
        return this.IS_BOUNCED;
    }

    @Override
    public void setBounced(boolean bounced) {
        this.IS_BOUNCED = bounced;
    }

    @Override
    public int getFunnelTimer() {

        return this.FUNNEL_TIMER;
    }

    @Override
    public double getMaxFallHeight() {
        return this.maxFallHeight;
    }


    @Override
    public void setMaxFallHeight(double fall) {
        this.maxFallHeight = fall;
    }

    @Override
    public Vec3d getLastVel() {
        return this.lastVel;
    }


    @Override
    public void setFunnelTimer(int funnelTimer) {
        this.FUNNEL_TIMER = funnelTimer;
    }

    @Override
    public void setGelTimer(int funnelTimer) {
        this.gelTransferTimer = funnelTimer;
    }

    @Override
    public int getGelTimer() {

        return this.gelTransferTimer;
    }


    @ModifyArgs(
            method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(target = "Lcom/google/common/collect/ImmutableList$Builder;addAll(Ljava/lang/Iterable;)Lcom/google/common/collect/ImmutableList$Builder;", value = "INVOKE",ordinal = 1)
    )
    private static void addAllModifyArg(Args args, @Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        //if(!world.isClient) {
            VoxelShape portalBox = CalledValues.getPortalCutout(entity);
            if (portalBox != VoxelShapes.empty())
                args.set(0, ((CustomCollisionView) world).getPortalBlockCollisions(entity, entityBoundingBox.stretch(movement), portalBox));
        //}
    }

    @Inject(method = "doesNotCollide(Lnet/minecraft/util/math/Box;)Z", at = @At("RETURN"), cancellable = true)
    private void doesNotCollide(Box box, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity) (Object) this));

        if (portalBox != VoxelShapes.empty())
            cir.setReturnValue(((CustomCollisionView) ((Entity) (Object) this).world).isPortalSpaceEmpty(((Entity) (Object) this), box, portalBox) && !this.world.containsFluid(box));
        //System.out.println("doesnotcollide");
    }

    @Inject(method = "wouldPoseNotCollide", at = @At("RETURN"), cancellable = true)
    public void wouldPoseNotCollide(EntityPose pose, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity)(Object)this));
        if(portalBox != VoxelShapes.empty())
            cir.setReturnValue(((CustomCollisionView) ((Entity)(Object)this).world).isPortalSpaceEmpty(((Entity)(Object)this), this.calculateBoundsForPose(pose).contract(1.0E-7), portalBox));
        //System.out.println("wouldposenotcollide");
    }
}