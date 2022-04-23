package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.RepulsionGel;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.CustomPortalEntity;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.PortalPlaceholderEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.google.common.collect.Lists;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import qouteall.imm_ptl.core.teleportation.CollisionHelper;

import java.util.List;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAttachments, EntityPortalsAccess {
    @Shadow
    public World world;
    @Shadow
    public boolean horizontalCollision;
    @Shadow
    public boolean verticalCollision;
    @Unique
    private double maxFallSpeed = 0;

    @Unique
    private boolean recentlyTouchedPortal;
    @Unique
    private final List<CustomPortalEntity> portalList = Lists.newArrayList();

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
    public abstract boolean isSneaking();

    @Shadow
    public abstract void setNoGravity(boolean noGravity);

    @Shadow
    public abstract boolean hasNoGravity();

    @Shadow public abstract boolean isOnGround();

    @Shadow public float fallDistance;

    @Shadow private Vec3d pos;

    @Override
    public List<CustomPortalEntity> getPortalList() {
        return portalList;
    }

    @Override
    public void addPortalToList(CustomPortalEntity portal) {
        portalList.add(portal);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        //if (!world.isClient()) {


            if (this.isInFunnel() && this.getFunnelTimer() != 0) {
                this.setFunnelTimer(this.getFunnelTimer() - 1);
            }
            if (this.isInFunnel() && this.getFunnelTimer() == 0 && this.hasNoGravity()) {
                this.setNoGravity(false);
                setInFunnel(false);
            }

       // }

        if(this.gelTransferTimer != 0){
            this.gelTransferTimer -= 1;
        }

        if (!world.isClient) {

            List<CustomPortalEntity> portalSound = this.world.getEntitiesByClass(CustomPortalEntity.class, this.getBoundingBox().expand(2), e -> true);

            for (Entity globalportal : portalSound) {
                CustomPortalEntity collidingportal = (CustomPortalEntity) globalportal;
                collidingportal.getActive();

                if (CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this)) && collidingportal.getActive() && !recentlyTouchedPortal) {
                    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), PortalCubedSounds.ENTITY_ENTER_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                    recentlyTouchedPortal = true;
                }

                if (!CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this)) && collidingportal.getActive() && recentlyTouchedPortal) {
                    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), PortalCubedSounds.ENTITY_EXIT_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                    recentlyTouchedPortal = false;
                }
            }
        }

        if (maxFallSpeed == 10 && world.getBlockState(this.getBlockPos()).getBlock() == PortalCubedBlocks.PROPULSION_GEL) {
            maxFallSpeed = 10;
        } else {
            if (maxFallSpeed > 0) {
                maxFallSpeed = maxFallSpeed - 1;
            }
        }

        this.lastVel = this.getVelocity();

        //System.out.println(this.fallDistance);
        //this.maxFallHeight = fallDistance;
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

/*
        if (world.getBlockState(this.getBlockPos()).getBlock() == PortalCubedBlocks.REPULSION_GEL) {
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
                    world.playSound((PlayerEntity) (Object) this, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                } else {
                    world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                }
                this.setVelocity(this.getVelocity().multiply(1.4));
                this.setVelocity(this.getVelocity().add(direction.x, direction.y, direction.z));
            }
            ((EntityAttachments) this).setBounced(true);
        } else if (world.getBlockState(new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() + 1, this.getBlockPos().getZ())).getBlock() == PortalCubedBlocks.REPULSION_GEL) {
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
                    world.playSound((PlayerEntity) (Object) this, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                } else {
                    world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                }
                this.setVelocity(this.getVelocity().add(direction.x, direction.y, direction.z));
            }
            ((EntityAttachments) this).setBounced(true);
        }
*/
    }

    @Inject(method = "remove", at = @At("HEAD"))
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


}
