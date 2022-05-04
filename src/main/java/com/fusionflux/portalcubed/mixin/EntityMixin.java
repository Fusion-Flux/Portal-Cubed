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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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


    @Shadow public abstract boolean doesNotCollide(double offsetX, double offsetY, double offsetZ);

    @Override
    public List<ExperimentalPortal> getPortalList() {
        return portalList;
    }

    @Override
    public void addPortalToList(ExperimentalPortal portal) {
        portalList.add(portal);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        //if(!this.world.isClient){
        //    List<Entity> list = world.getEntitiesByClass(Entity.class, this.getBoundingBox(), e -> (e instanceof ExperimentalPortal));
        //    for (Entity entity : list) {
        //        if (entity instanceof ExperimentalPortal portal) {
        //            //portal.getFacingDirection().getVector();
        //           // System.out.println("AAAAAAAA");
        //            this.doesNotCollide(portal.getFacingDirection().getOpposite().getVector().getX(),portal.getFacingDirection().getOpposite().getVector().getY(),portal.getFacingDirection().getOpposite().getVector().getZ());
        //        }
        //    }
        //}
        //if (!world.isClient()) {
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


       // }


        if(this.gelTransferTimer != 0){
            this.gelTransferTimer -= 1;
        }

        if (!world.isClient) {

            List<ExperimentalPortal> portalSound = this.world.getEntitiesByClass(ExperimentalPortal.class, this.getBoundingBox().expand(2), e -> true);

            for (Entity globalportal : portalSound) {
                ExperimentalPortal collidingportal = (ExperimentalPortal) globalportal;
                collidingportal.getActive();

                //if (CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this)) && collidingportal.getActive() && !recentlyTouchedPortal) {
                //    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), PortalCubedSounds.ENTITY_ENTER_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                //    recentlyTouchedPortal = true;
                //}
//
                //if (!CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this)) && collidingportal.getActive() && recentlyTouchedPortal) {
                //    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), PortalCubedSounds.ENTITY_EXIT_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                //    recentlyTouchedPortal = false;
                //}
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

    ///**
    // * @author
    // */
    //@Overwrite
    //public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
    //    ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(collisions.size() + 1);
    //    if (!collisions.isEmpty()) {
    //        builder.addAll(collisions);
    //    }
//
    //    WorldBorder worldBorder = world.getWorldBorder();
    //    boolean bl = entity != null && worldBorder.canCollide(entity, entityBoundingBox.stretch(movement));
    //    if (bl) {
    //        builder.add(worldBorder.asVoxelShape());
    //    }
//
    //    List<Entity> list = world.getEntitiesByClass(Entity.class, entityBoundingBox, e -> !(e instanceof PlayerEntity && ((PlayerEntity) e).getAbilities().flying));
    //    boolean test=false;
    //    for (Entity entityList : list) {
    //        if(entityList instanceof ExperimentalPortal portal){
    //            test = true;
    //            Direction portaldirec = portal.getFacingDirection();
    //            movement.multiply(Math.abs(portaldirec.getUnitVector().getX())*-1,Math.abs(portaldirec.getUnitVector().getY())*-1,Math.abs(portaldirec.getUnitVector().getZ())*-1);
    //            builder.addAll(world.getBlockCollisions(entity, entityBoundingBox.offset(movement)));
    //        }
    //    }
    //    if(!test)
    //        builder.addAll(world.getBlockCollisions(entity, entityBoundingBox.offset(movement)));
//
    //    return adjustMovementForCollisions(movement, entityBoundingBox, builder.build());
    //}
//
    //private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
    //    if (collisions.isEmpty()) {
    //        return movement;
    //    } else {
    //        double d = movement.x;
    //        double e = movement.y;
    //        double f = movement.z;
    //        if (e != 0.0) {
    //            e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions, e);
    //            if (e != 0.0) {
    //                entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
    //            }
    //        }
//
    //        boolean bl = Math.abs(d) < Math.abs(f);
    //        if (bl && f != 0.0) {
    //            f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
    //            if (f != 0.0) {
    //                entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
    //            }
    //        }
//
    //        if (d != 0.0) {
    //            d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions, d);
    //            if (!bl && d != 0.0) {
    //                entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
    //            }
    //        }
//
    //        if (!bl && f != 0.0) {
    //            f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
    //        }
//
    //        return new Vec3d(d, e, f);
    //    }
    //}


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
    //@Overwrite
    //public void checkBlockCollision() {
    //}
    //@Inject(method = "isInsideWall", at = @At("HEAD"), cancellable = true)
    //public void isInsideWall(CallbackInfoReturnable<Boolean> cir) {
    //cir.setReturnValue(false);
    //}
    //@Inject(method = "collidesWithStateAtPos", at = @At("HEAD"), cancellable = true)
    //public void collidesWithStateAtPos(BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
    //    cir.setReturnValue(false);
    //    //VoxelShape voxelShape = state.getCollisionShape(this.world, pos, ShapeContext.of(this));
    //    //VoxelShape voxelShape2 = voxelShape.offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
    //    //return VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(this.getBoundingBox()), BooleanBiFunction.AND);
    //}

    @ModifyArgs(
            method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(target = "Lcom/google/common/collect/ImmutableList$Builder;addAll(Ljava/lang/Iterable;)Lcom/google/common/collect/ImmutableList$Builder;", value = "INVOKE",ordinal = 1)
    )
    private static void addAllModifyArg(Args args, @Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        List<Entity> list = world.getEntitiesByClass(Entity.class, entityBoundingBox, e -> e instanceof ExperimentalPortal);
        //if (!list.isEmpty()) {
            for (Entity entity1 : list) {
                args.set(0,((CustomCollisionView) world).getPortalBlockCollisions(entity, entityBoundingBox.stretch(movement), ((ExperimentalPortal) entity1).getFacingDirection()));
                break;
            }
       // }
    }

    //private Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
    //    ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(collisions.size() + 1);
    //    if (!collisions.isEmpty()) {
    //        builder.addAll(collisions);
    //    }
//
    //    WorldBorder worldBorder = world.getWorldBorder();
    //    boolean bl = entity != null && worldBorder.canCollide(entity, entityBoundingBox.stretch(movement));
    //    if (bl) {
    //        builder.add(worldBorder.asVoxelShape());
    //    }
    //    List<Entity> list = world.getEntitiesByClass(Entity.class, ((Entity) (Object) this).getBoundingBox(), e -> e instanceof ExperimentalPortal);
    //    if (!list.isEmpty()) {
    //        for (Entity entity1 : list) {
    //            builder.addAll(((CustomCollisionView) world).getPortalBlockCollisions(entity, entityBoundingBox.stretch(movement), ((ExperimentalPortal) entity1).getFacingDirection()));
    //        }
    //    } else {
    //        builder.addAll(world.getBlockCollisions(entity, entityBoundingBox.stretch(movement)));
    //    }
    //    return adjustMovementForCollisions(movement, entityBoundingBox, builder.build());
    //}
}
