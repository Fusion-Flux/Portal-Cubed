package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalPlaceholderEntity;
import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.PortalVelocityHelper;
import com.google.common.collect.Lists;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
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
    private Vec3d serverPos = Vec3d.ZERO;

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

    @Shadow public abstract boolean canUsePortals();

    @Shadow public abstract Vec3d getEyePos();

    @Shadow public abstract void teleport(double destX, double destY, double destZ);

    @Shadow public abstract void setPosition(Vec3d pos);

    @Shadow public abstract void setPos(double x, double y, double z);

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract void setVelocity(double x, double y, double z);

    @Shadow public abstract void setYaw(float yaw);

    @Shadow public abstract float getYaw();

    @Shadow public abstract void setVelocityClient(double x, double y, double z);

    @Shadow public abstract void setPosition(double x, double y, double z);

    @Shadow public double lastRenderX;

    @Shadow public double lastRenderY;

    @Shadow public double lastRenderZ;

    @Shadow public double prevX;

    @Shadow public double prevY;

    @Shadow public double prevZ;


    @Shadow public abstract void requestTeleport(double destX, double destY, double destZ);

    private Vec3d teleportVelocity = Vec3d.ZERO;
    private boolean shouldTeleport = false;
    private boolean shouldTeleportClient = false;
    public double lastZ = 0;

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
        if(!world.isClient)
        if (shouldTeleport) {
            this.requestTeleport(serverPos.x, serverPos.y, serverPos.z);
            this.setServerVel(Vec3d.ZERO);
            this.setShouldTeleport(false);
        }
        if (shouldTeleportClient && this.getPos().equals(serverPos)) {
            this.setVelocity(teleportVelocity);
            shouldTeleportClient = false;
        }

        Entity thisentity = ((Entity) (Object) this);
        Vec3d entityVelocity = this.getVelocity();

        Box portalCheckBox = getBoundingBox();
        boolean canTeleport = false;
        if (thisentity instanceof PlayerEntity && !this.world.isClient) {
            portalCheckBox = portalCheckBox.expand(10);
        } else {
            portalCheckBox = portalCheckBox.stretch(entityVelocity);
            canTeleport = true;
        }
        List<ExperimentalPortal> list = ((Entity) (Object) this).world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);
        VoxelShape ommitedDirections = VoxelShapes.empty();
        for (ExperimentalPortal portal : list) {
            //if (portal.calculateIntersectionBox() != nullBox && portal.getIntersectionBoundingBox().intersects(this.getBoundingBox())){

                if (portal.calculateCuttoutBox() != nullBox) {
                    if (portal.getActive())
                        ommitedDirections = VoxelShapes.union(ommitedDirections, VoxelShapes.cuboid(portal.getCutoutBoundingBox()));
                }


            if (!(thisentity instanceof ExperimentalPortal) && this.canUsePortals() && portal.getActive()) {
                Direction portalFacing = portal.getFacingDirection();
                Direction portalVertFacing = Direction.fromVector(new BlockPos(CalledValues.getAxisH(portal).x, CalledValues.getAxisH(portal).y, CalledValues.getAxisH(portal).z));

                Direction otherDirec = Direction.fromVector((int) CalledValues.getOtherFacing(portal).getX(), (int) CalledValues.getOtherFacing(portal).getY(), (int) CalledValues.getOtherFacing(portal).getZ());
                Direction otherPortalVertFacing = Direction.fromVector(new BlockPos(CalledValues.getOtherAxisH(portal).x, CalledValues.getOtherAxisH(portal).y, CalledValues.getOtherAxisH(portal).z));

                if (otherDirec != null) {
                    double teleportYOffset = switch (otherDirec) {
                        case DOWN -> 2;
                        case NORTH, SOUTH, EAST, WEST -> {
                            if (portalFacing != Direction.UP && portalFacing != Direction.DOWN) {
                                yield portal.getPos().y - thisentity.getPos().y;
                            } else {
                                yield 1;
                            }
                        }
                        default -> 0;
                    };

                    double teleportXOffset = portal.getPos().x - thisentity.getPos().x;
                    double teleportZOffset = portal.getPos().z - thisentity.getPos().z;

                    if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                        if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
                            if (portalVertFacing == Direction.NORTH || portalVertFacing == Direction.SOUTH) {
                                if (otherDirec == Direction.EAST || otherDirec == Direction.WEST) {
                                    teleportZOffset = -teleportXOffset;
                                    teleportXOffset = 0;
                                }
                            }

                            if (portalVertFacing == Direction.EAST || portalVertFacing == Direction.WEST) {
                                if (otherDirec == Direction.NORTH || otherDirec == Direction.SOUTH) {
                                    teleportXOffset = -teleportZOffset;
                                    teleportZOffset = 0;
                                }
                            }
                        }
                    }

                    if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                        if (portalFacing != Direction.UP && portalFacing != Direction.DOWN) {
                            if (otherPortalVertFacing == Direction.NORTH || otherPortalVertFacing == Direction.SOUTH) {
                                if (portalFacing == Direction.EAST || portalFacing == Direction.WEST) {
                                    teleportXOffset = -teleportZOffset;
                                    teleportZOffset = 0;
                                }
                            }

                            if (otherPortalVertFacing == Direction.EAST || otherPortalVertFacing == Direction.WEST) {
                                if (portalFacing == Direction.NORTH || portalFacing == Direction.SOUTH) {
                                    teleportZOffset = -teleportXOffset;
                                    teleportXOffset = 0;
                                }
                            }
                        }
                    }

                    if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                        if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                            if (otherPortalVertFacing == Direction.NORTH || otherPortalVertFacing == Direction.SOUTH) {
                                if (portalVertFacing == Direction.EAST || portalVertFacing == Direction.WEST) {
                                    double storedx = teleportXOffset;
                                    teleportXOffset = -teleportZOffset;
                                    teleportZOffset = -storedx;
                                }
                            }

                            if (otherPortalVertFacing == Direction.EAST || otherPortalVertFacing == Direction.WEST) {
                                if (portalVertFacing == Direction.NORTH || portalVertFacing == Direction.SOUTH) {
                                    double storedz = teleportZOffset;
                                    teleportZOffset = -teleportXOffset;
                                    teleportXOffset = -storedz;
                                }
                            }
                            if (portalVertFacing != null)
                                if (otherPortalVertFacing == portalVertFacing.getOpposite()) {
                                    teleportZOffset = -teleportZOffset;
                                    teleportXOffset = -teleportXOffset;
                                }
                        }
                    }

                    if (portalFacing == Direction.NORTH || portalFacing == Direction.SOUTH) {
                        if (otherDirec == Direction.EAST || otherDirec == Direction.WEST) {
                            teleportZOffset = -teleportXOffset;
                            teleportXOffset = 0;
                        }
                    }

                    if (portalFacing == Direction.EAST || portalFacing == Direction.WEST) {
                        if (otherDirec == Direction.NORTH || otherDirec == Direction.SOUTH) {
                            teleportXOffset = -teleportZOffset;
                            teleportZOffset = 0;
                        }
                    }
                    Vec3d entityEyePos = this.getEyePos();

                    if (canTeleport) {
                        if (portalFacing.getUnitVector().getX() < 0) {
                            if (entityEyePos.getX() + entityVelocity.getX() > portal.getPos().getX()) {
                                if (thisentity instanceof PlayerEntity bob && this.world.isClient && bob.isMainPlayer()) {
                                    var bytebuf = PacketByteBufs.create();
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getX() - teleportXOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getY() - teleportYOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    bytebuf.writeBoolean(true);
                                    NetworkingSafetyWrapper.sendFromClient("portalpacket", bytebuf);
                                    serverPos = new Vec3d(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    shouldTeleportClient = true;
                                    teleportVelocity = PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec);
                                } else if(!(thisentity instanceof PlayerEntity)) {
                                    this.setPosition(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    this.setVelocity(PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec));
                                }
                                this.setLastPosition(this.getPos());
                                this.setYaw(this.getYaw() + PortalVelocityHelper.yawAddition(portal.getFacingDirection(), otherDirec));
                            }
                        }
                        if (portalFacing.getUnitVector().getY() < 0) {
                            if (entityEyePos.getY() + entityVelocity.getY() > portal.getPos().getY()) {
                                if (thisentity instanceof PlayerEntity bob && this.world.isClient && bob.isMainPlayer()) {
                                    var bytebuf = PacketByteBufs.create();
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getX() - teleportXOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getY() - teleportYOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    bytebuf.writeBoolean(true);
                                    NetworkingSafetyWrapper.sendFromClient("portalpacket", bytebuf);
                                    serverPos = new Vec3d(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    shouldTeleportClient = true;
                                    teleportVelocity = PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec);
                                } else if(!(thisentity instanceof PlayerEntity)) {
                                    this.setPosition(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    this.setVelocity(PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec));
                                }
                                this.setLastPosition(this.getPos());
                                this.setYaw(this.getYaw() + PortalVelocityHelper.yawAddition(portal.getFacingDirection(), otherDirec));
                            }
                        }
                        if (portalFacing.getUnitVector().getZ() < 0) {
                            if (entityEyePos.getZ() + entityVelocity.getZ() > portal.getPos().getZ()) {
                                if (thisentity instanceof PlayerEntity bob && this.world.isClient && bob.isMainPlayer()) {
                                    var bytebuf = PacketByteBufs.create();
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getX() - teleportXOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getY() - teleportYOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    bytebuf.writeBoolean(true);
                                    NetworkingSafetyWrapper.sendFromClient("portalpacket", bytebuf);
                                    serverPos = new Vec3d(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    shouldTeleportClient = true;
                                    teleportVelocity = PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec);
                                } else if(!(thisentity instanceof PlayerEntity)) {
                                    this.setPosition(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    this.setVelocity(PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec));
                                }
                                this.setLastPosition(this.getPos());
                                this.setYaw(this.getYaw() + PortalVelocityHelper.yawAddition(portal.getFacingDirection(), otherDirec));
                            }
                        }


                        if (portalFacing.getUnitVector().getX() > 0) {
                            if (entityEyePos.getX() + entityVelocity.getX() < portal.getPos().getX()) {
                                if (thisentity instanceof PlayerEntity bob && this.world.isClient && bob.isMainPlayer()) {
                                    var bytebuf = PacketByteBufs.create();
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getX() - teleportXOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getY() - teleportYOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    bytebuf.writeBoolean(true);
                                    NetworkingSafetyWrapper.sendFromClient("portalpacket", bytebuf);
                                    serverPos = new Vec3d(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    shouldTeleportClient = true;
                                    teleportVelocity = PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec);
                                } else if(!(thisentity instanceof PlayerEntity)) {
                                    this.setPosition(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    this.setVelocity(PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec));
                                }
                                this.setLastPosition(this.getPos());
                                this.setYaw(this.getYaw() + PortalVelocityHelper.yawAddition(portal.getFacingDirection(), otherDirec));
                            }
                        }
                        if (portalFacing.getUnitVector().getY() > 0) {
                            if (entityEyePos.getY() + entityVelocity.getY() < portal.getPos().getY()) {
                                if (thisentity instanceof PlayerEntity bob && this.world.isClient && bob.isMainPlayer()) {
                                    var bytebuf = PacketByteBufs.create();
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getX() - teleportXOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getY() - teleportYOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    bytebuf.writeBoolean(true);
                                    NetworkingSafetyWrapper.sendFromClient("portalpacket", bytebuf);
                                    serverPos = new Vec3d(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    shouldTeleportClient = true;
                                    teleportVelocity = PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec);
                                } else if(!(thisentity instanceof PlayerEntity)) {
                                    this.setPosition(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    this.setVelocity(PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec));
                                }
                                this.setLastPosition(this.getPos());
                                this.setYaw(this.getYaw() + PortalVelocityHelper.yawAddition(portal.getFacingDirection(), otherDirec));
                            }
                        }
                        if (portalFacing.getUnitVector().getZ() > 0) {
                            if (entityEyePos.getZ() + entityVelocity.getZ() < portal.getPos().getZ()) {
                                if (thisentity instanceof PlayerEntity bob && this.world.isClient && bob.isMainPlayer()) {
                                    var bytebuf = PacketByteBufs.create();
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getX() - teleportXOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getY() - teleportYOffset);
                                    bytebuf.writeDouble(CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    bytebuf.writeBoolean(true);
                                    NetworkingSafetyWrapper.sendFromClient("portalpacket", bytebuf);
                                    serverPos = new Vec3d(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    shouldTeleportClient = true;
                                    teleportVelocity = PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec);
                                } else if(!(thisentity instanceof PlayerEntity)) {
                                    this.setPosition(CalledValues.getDestination(portal).getX() - teleportXOffset, CalledValues.getDestination(portal).getY() - teleportYOffset, CalledValues.getDestination(portal).getZ() - teleportZOffset);
                                    this.setVelocity(PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec));
                                }
                                this.setLastPosition(this.getPos());
                                this.setYaw(this.getYaw() + PortalVelocityHelper.yawAddition(portal.getFacingDirection(), otherDirec));
                            }
                        }
                    }
                }
            }
        //}
    }
       // if(thisentity instanceof PlayerEntity && this.world.isClient && ommitedDirections != VoxelShapes.empty()){
       //     var bytebuf = PacketByteBufs.create();
       //     bytebuf.writeDouble(this.getVelocity().getX());
       //     bytebuf.writeDouble(this.getVelocity().getY());
       //     bytebuf.writeDouble(this.getVelocity().getZ());
       //     NetworkingSafetyWrapper.sendFromClient("portalpacket", bytebuf);
       // }
            CalledValues.setPortalCutout(((Entity) (Object) this), ommitedDirections);


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


        Vec3d rotatedPos = this.pos;
        //if((Object)this instanceof PlayerEntity){
            rotatedPos = RotationUtil.vecWorldToPlayer(this.pos, GravityChangerAPI.getGravityDirection((Entity)(Object)this));
        //}
        if(!this.isOnGround()) {
            if (rotatedPos.y > this.maxFallHeight) {
                this.maxFallHeight = rotatedPos.y;
            }
        }else{
            this.maxFallHeight = rotatedPos.y;
        }

        this.lastVel = this.getVelocity();

        if (world.getBlockState(this.getBlockPos()).getBlock() != PortalCubedBlocks.REPULSION_GEL && this.isBounced()){
            this.setBounced(false);
        }


    }
    @Inject(method = "pushAwayFrom", at = @At("HEAD") , cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo ci) {
        if(entity instanceof StorageCubeEntity){
            ci.cancel();
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
    public void setServerVel(Vec3d fall) {
        this.serverPos = fall;
    }

    @Override
    public Vec3d getServerVel() {
        return this.serverPos;
    }

    @Override
    public void setShouldTeleport(boolean fall) {
        this.shouldTeleport = fall;
    }

    @Override
    public boolean getShouldTeleport() {
        return this.shouldTeleport;
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
            method = "adjustSingleAxisMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(target = "Lcom/google/common/collect/ImmutableList$Builder;addAll(Ljava/lang/Iterable;)Lcom/google/common/collect/ImmutableList$Builder;", value = "INVOKE",ordinal = 1)
    )
    private static void addAllModifyArg(Args args, @Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        VoxelShape portalBox = CalledValues.getPortalCutout(entity);
            if (portalBox != VoxelShapes.empty())
                args.set(0, ((CustomCollisionView) world).getPortalBlockCollisions(entity, entityBoundingBox.stretch(movement), portalBox));
    }

    @Inject(method = "doesNotCollide(Lnet/minecraft/util/math/Box;)Z", at = @At("RETURN"), cancellable = true)
    private void doesNotCollide(Box box, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity) (Object) this));
        if (portalBox != VoxelShapes.empty())
            cir.setReturnValue(true);
    }

    @Inject(method = "wouldPoseNotCollide", at = @At("RETURN"), cancellable = true)
    public void wouldPoseNotCollide(EntityPose pose, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity)(Object)this));
        if(portalBox != VoxelShapes.empty())
            cir.setReturnValue(true);
    }

        @Inject(method = "isInsideWall", at = @At("HEAD"), cancellable = true)
    public void isInsideWall(CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity)(Object)this));
        if(portalBox != VoxelShapes.empty())
        cir.setReturnValue(false);
    }
    @Inject(method = "collidesWithStateAtPos", at = @At("HEAD"), cancellable = true)
    public void collidesWithStateAtPos(BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity)(Object)this));
        if(portalBox != VoxelShapes.empty())
            cir.setReturnValue(false);
    }

    public final void setLastPosition(Vec3d pos) {
        this.lastRenderX = pos.getX();
        this.lastRenderY = pos.getY();
        this.lastRenderZ = pos.getZ();
        this.prevX = pos.getX();
        this.prevY = pos.getY();
        this.prevZ = pos.getZ();
    }

}
