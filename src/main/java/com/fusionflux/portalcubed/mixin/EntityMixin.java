package com.fusionflux.portalcubed.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.ClientTeleportCheck;
import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.fusionflux.portalcubed.listeners.CustomCollisionView;
import com.fusionflux.portalcubed.listeners.WentThroughPortalListener;
import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import com.fusionflux.portalcubed.util.IPQuaternion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAttachments, EntityPortalsAccess, ClientTeleportCheck {
    @Shadow
    public Level level;

    @Unique
    private double maxFallSpeed = 0;

    @Unique
    private boolean inFunnel = false;

    @Unique
    private double maxFallHeight = -99999999;

    private Direction prevGravDirec = Direction.DOWN;

    @Unique
    private Vec3 lastVel = Vec3.ZERO;

    @Unique
    private int gelTransferTimer = 0;

    @Unique
    private int gelTransferChangeTimer = 0;

    @Unique
    private boolean isBounced = false;

    @Unique
    private int funnelTimer = 0;

    @Override
    public double getMaxFallSpeed() {
        return maxFallSpeed;
    }

    @Override
    public void setMaxFallSpeed(double maxFallSpeed) {
        this.maxFallSpeed = maxFallSpeed;
    }

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract boolean equals(Object o);

    @Shadow
    public abstract boolean isNoGravity();

    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    private Vec3 position;

    @Shadow
    public abstract boolean canChangeDimensions();

    @Shadow
    public abstract void setDeltaMovement(Vec3 velocity);

    @Shadow
    public abstract float getYRot();

    @Shadow public abstract double getY();

    @Shadow public abstract int getId();

    @Shadow public abstract double getX();

    @Shadow public abstract double getZ();

    @Shadow public abstract float getXRot();

    private static final AABB NULL_BOX = new AABB(0, 0, 0, 0, 0, 0);

    @Unique
    private final Map<BlockState, BlockPos> collidingBlocks = new HashMap<>();
    @Unique
    private final Map<BlockState, BlockPos> leftBlocks = new HashMap<>();


    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        Entity thisentity = ((Entity) (Object) this);

        Vec3 entityVelocity = this.getDeltaMovement();


        if (!(thisentity instanceof Player)) {
            AABB portalCheckBox = getBoundingBox();

            portalCheckBox = portalCheckBox.expandTowards(entityVelocity.add(0, .08, 0));

            List<ExperimentalPortal> list = ((Entity) (Object) this).level.getEntitiesOfClass(ExperimentalPortal.class, portalCheckBox);
            VoxelShape omittedDirections = Shapes.empty();

            for (ExperimentalPortal portal : list) {
                if (portal.calculateCuttoutBox() != NULL_BOX && portal.calculateBoundsCheckBox() != NULL_BOX) {
                    if (portal.getActive())
                        omittedDirections = Shapes.or(omittedDirections, Shapes.create(portal.getCutoutBoundingBox()));
                }
            }
            CalledValues.setPortalCutout(((Entity) (Object) this), omittedDirections);
        }

        if (this.isInFunnel() && this.getFunnelTimer() != 0) {
            this.setFunnelTimer(this.getFunnelTimer() - 1);
        }
        if (this.isInFunnel() && this.getFunnelTimer() == 0 && this.isNoGravity()) {
            RayonIntegration.INSTANCE.setNoGravity((Entity)(Object)this, false);
            setInFunnel(false);
        }


        if (this.gelTransferTimer != 0) {
            this.gelTransferTimer -= 1;
        }
        if (this.gelTransferChangeTimer != 0) {
            this.gelTransferChangeTimer -= 1;
        }

        if (maxFallSpeed == 10 && level.getBlockState(this.blockPosition()).getBlock() == PortalCubedBlocks.PROPULSION_GEL) {
            maxFallSpeed = 10;
        } else {
            if (maxFallSpeed > 0) {
                maxFallSpeed = maxFallSpeed - 1;
            }
        }


        Vec3 rotatedPos;
        rotatedPos = RotationUtil.vecWorldToPlayer(this.position, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        if (prevGravDirec != GravityChangerAPI.getGravityDirection(((Entity) (Object) this))) {
            this.maxFallHeight = rotatedPos.y;
        }

        if (!this.isOnGround()) {
            if (rotatedPos.y > this.maxFallHeight) {
                this.maxFallHeight = rotatedPos.y;
            }
        } else {
            this.maxFallHeight = rotatedPos.y;
        }

        this.lastVel = this.getDeltaMovement();

        if (level.getBlockState(this.blockPosition()).getBlock() != PortalCubedBlocks.REPULSION_GEL && this.isBounced()) {
            this.setBounced(false);
        }

        prevGravDirec = GravityChangerAPI.getGravityDirection(((Entity) (Object) this));
    }


    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTail(CallbackInfo ci) {
        Entity thisEntity = ((Entity) (Object) this);

        if (!thisEntity.level.isClientSide() && !(thisEntity instanceof Player) && !(thisEntity instanceof ExperimentalPortal)) {
            Vec3 entityVelocity = this.getDeltaMovement();


            AABB portalCheckBox = getBoundingBox();

            portalCheckBox = portalCheckBox.expandTowards(entityVelocity.add(0, .08, 0));


            List<ExperimentalPortal> list = ((Entity) (Object) this).level.getEntitiesOfClass(ExperimentalPortal.class, portalCheckBox);
            ExperimentalPortal portal;
            for (ExperimentalPortal portalCheck : list) {
                portal = portalCheck;
                if (this.canChangeDimensions() && portal.getActive() && !CalledValues.getHasTeleportationHappened(thisEntity) && !CalledValues.getIsTeleporting(thisEntity)) {
                    Direction portalFacing = portal.getFacingDirection();
                    Direction otherDirec = Direction.fromNormal((int) portal.getOtherFacing().x(), (int) portal.getOtherFacing().y(), (int) portal.getOtherFacing().z());

                    if (otherDirec != null) {

                        //noinspection ConstantValue
                        if ((Object)this instanceof LivingEntity) {
                            entityVelocity = entityVelocity.add(0, .08, 0);
                        }

                        Vec3 entityEyePos = thisEntity.getEyePosition();

                        if (portalFacing.step().x() < 0) {
                            if (entityEyePos.x() + entityVelocity.x >= portal.position().x() && entityVelocity.x() > 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                performTeleport(thisEntity, portal, entityVelocity);
                                break;
                            }
                        }
                        if (portalFacing.step().y() < 0) {
                            if (entityEyePos.y() + entityVelocity.y >= portal.position().y() && entityVelocity.y() > 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                performTeleport(thisEntity, portal, entityVelocity);
                                break;
                            }
                        }
                        if (portalFacing.step().z() < 0) {
                            if (entityEyePos.z() + entityVelocity.z >= portal.position().z() && entityVelocity.z() > 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                performTeleport(thisEntity, portal, entityVelocity);
                                break;
                            }
                        }
                        if (portalFacing.step().x() > 0) {
                            if (entityEyePos.x() + entityVelocity.x <= portal.position().x() && entityVelocity.x() < 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                performTeleport(thisEntity, portal, entityVelocity);
                                break;
                            }
                        }
                        if (portalFacing.step().y() > 0) {
                            if (entityEyePos.y() + entityVelocity.y <= portal.position().y() && entityVelocity.y() < 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                performTeleport(thisEntity, portal, entityVelocity);
                                break;
                            }
                        }
                        if (portalFacing.step().z() > 0) {
                            if (entityEyePos.z() + entityVelocity.z <= portal.position().z() && entityVelocity.z() < 0 && portal.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                performTeleport(thisEntity, portal, entityVelocity);
                                break;
                            }
                        }

                    }
                }
            }
        }
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo ci) {
        if (entity instanceof CorePhysicsEntity || entity instanceof GelBlobEntity) {
            ci.cancel();
        }
    }

    private void performTeleport(
            Entity thisEntity,
            ExperimentalPortal portal,
            Vec3 entityVelocity
    ) {
        double teleportXOffset = (thisEntity.getEyePosition().x()) - portal.position().x();
        double teleportYOffset = (thisEntity.getEyePosition().y()) - portal.position().y();
        double teleportZOffset = (thisEntity.getEyePosition().z()) - portal.position().z();
        Direction portalFacing = portal.getFacingDirection();
        Direction otherDirec = Direction.fromNormal((int) portal.getOtherFacing().x(), (int) portal.getOtherFacing().y(), (int) portal.getOtherFacing().z());

        IPQuaternion rotationW = IPQuaternion.getRotationBetween(portal.getAxisW().orElseThrow().scale(-1), portal.getOtherAxisW(), portal.getAxisH().orElseThrow());
        IPQuaternion rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getAxisW().orElseThrow());

        if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
            if (otherDirec == portalFacing) {
                rotationW = IPQuaternion.getRotationBetween(portal.getNormal().scale(-1), portal.getOtherNormal(), (portal.getAxisH().orElseThrow()));
                rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getNormal().scale(-1));
            }
        }

        float modPitch = thisEntity.getXRot();
        if (modPitch == 90) {
            modPitch = 0;
        }

        Vec3 rotatedYaw = Vec3.directionFromRotation(modPitch, thisEntity.getYRot());
        Vec3 rotatedPitch = Vec3.directionFromRotation(thisEntity.getXRot(), thisEntity.getYRot());
        Vec3 rotatedVel = entityVelocity;
        Vec3 rotatedOffsets = new Vec3(teleportXOffset, teleportYOffset, teleportZOffset);

        rotatedYaw = (rotationH.rotate(rotationW.rotate(rotatedYaw)));
        rotatedPitch = (rotationH.rotate(rotationW.rotate(rotatedPitch)));
        rotatedVel = (rotationH.rotate(rotationW.rotate(rotatedVel)));
        rotatedOffsets = (rotationH.rotate(rotationW.rotate(rotatedOffsets)));

        rotatedOffsets = rotatedOffsets.subtract(0, thisEntity.getEyeY() - thisEntity.getY(), 0);
        if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
            if (rotatedOffsets.y < -0.95) {
                rotatedOffsets = new Vec3(rotatedOffsets.x, -0.95, rotatedOffsets.z);
            } else if (rotatedOffsets.y > (-0.95 + (1.9 - thisEntity.getBbHeight()))) {
                rotatedOffsets = new Vec3(rotatedOffsets.x, (-0.95 + (1.9 - thisEntity.getBbHeight())), rotatedOffsets.z);
            }
        }

        Vec2 lookAnglePitch = new Vec2(
                (float)Math.toDegrees(-Mth.atan2(rotatedPitch.y, Math.sqrt(rotatedPitch.x * rotatedPitch.x + rotatedPitch.z * rotatedPitch.z))),
                (float)Math.toDegrees(Mth.atan2(rotatedPitch.z, rotatedPitch.x))
        );

        Vec2 lookAngleYaw = new Vec2(
                (float)Math.toDegrees(-Mth.atan2(rotatedYaw.y, Math.sqrt(rotatedYaw.x * rotatedYaw.x + rotatedYaw.z * rotatedYaw.z))),
                (float)Math.toDegrees(Mth.atan2(rotatedYaw.z, rotatedYaw.x))
        );
        final Vec3 destPos = portal.getDestination().orElseThrow(ExperimentalPortal.NOT_INIT).add(rotatedOffsets);
        thisEntity.moveTo(destPos.x, destPos.y, destPos.z, lookAngleYaw.y - 90, lookAnglePitch.x);
        thisEntity.setDeltaMovement(rotatedVel);
        GravityChangerAPI.clearGravity(thisEntity);
        if (level instanceof ServerLevel serverWorld) {
            final FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(getId());
            buf.writeDouble(getX());
            buf.writeDouble(getY());
            buf.writeDouble(getZ());
            buf.writeFloat(getYRot());
            buf.writeFloat(getXRot());
            final Packet<?> packet = ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.REFRESH_POS, buf);
            for (final ServerPlayer player : serverWorld.players()) {
                serverWorld.sendParticles(player, true, destPos.x, destPos.y, destPos.z, packet);
            }
        }
        if (this instanceof WentThroughPortalListener listener) {
            listener.wentThroughPortal(portal);
        }
    }

    @Override
    public boolean isInFunnel() {
        return this.inFunnel;
    }

    @Override
    public void setInFunnel(boolean inFunnel) {
        this.inFunnel = inFunnel;
    }

    @Override
    public boolean isBounced() {
        return this.isBounced;
    }

    @Override
    public void setBounced(boolean bounced) {
        this.isBounced = bounced;
    }

    @Override
    public int getFunnelTimer() {

        return this.funnelTimer;
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
    public Vec3 getLastVel() {
        return this.lastVel;
    }


    @Override
    public void setFunnelTimer(int funnelTimer) {
        this.funnelTimer = funnelTimer;
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
            method = "collideBoundingBox",
            at = @At(
                target = "Lcom/google/common/collect/ImmutableList$Builder;addAll(Ljava/lang/Iterable;)Lcom/google/common/collect/ImmutableList$Builder;",
                value = "INVOKE",
                ordinal = 1,
                remap = false
            )
    )
    private static void addAllModifyArg(Args args, @Nullable Entity entity, Vec3 movement, AABB entityBoundingBox, Level world, List<VoxelShape> collisions) {
        VoxelShape portalBox = CalledValues.getPortalCutout(entity);
        if (portalBox != Shapes.empty())
            args.set(0, ((CustomCollisionView) world).getPortalBlockCollisions(entity, entityBoundingBox.expandTowards(movement), portalBox));
    }

    @Inject(method = "isFree(Lnet/minecraft/world/phys/AABB;)Z", at = @At("RETURN"), cancellable = true)
    private void doesNotCollide(AABB box, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity) (Object) this));
        if (portalBox != Shapes.empty())
            cir.setReturnValue(true);
    }

    @Inject(method = "canEnterPose", at = @At("RETURN"), cancellable = true)
    public void wouldPoseNotCollide(Pose pose, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity) (Object) this));
        if (portalBox != Shapes.empty())
            cir.setReturnValue(true);
    }

    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    public void isInsideWall(CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity) (Object) this));
        if (portalBox != Shapes.empty()) cir.setReturnValue(false);
    }

    @Inject(method = "isColliding", at = @At("HEAD"), cancellable = true)
    public void collidesWithStateAtPos(BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((Entity) (Object) this));
        if (portalBox != Shapes.empty())
            cir.setReturnValue(false);
    }

    @Inject(method = "checkInsideBlocks", at = @At("HEAD"))
    private void beginBlockCheck(CallbackInfo ci) {
        leftBlocks.putAll(collidingBlocks);
    }

    @Redirect(
            method = "checkInsideBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V"
            )
    )
    private void midBlockCheck(BlockState instance, Level world, BlockPos pos, Entity entity) {
        instance.entityInside(world, pos, entity);
        if (
                instance.getBlock() instanceof BlockCollisionTrigger trigger &&
                        intersects(
                                entity.getBoundingBox().move(pos.multiply(-1)),
                                trigger.getTriggerShape(instance, world, pos, CollisionContext.of(entity))
                        )
        ) {
            final BlockPos immutable = pos.immutable();
            if (collidingBlocks.put(instance, immutable) == null) {
                trigger.onEntityEnter(instance, world, immutable, entity);
            }
            leftBlocks.remove(instance);
        }
    }

    @Inject(method = "checkInsideBlocks", at = @At("TAIL"))
    private void endBlockCheck(CallbackInfo ci) {
        for (final var entry : leftBlocks.entrySet()) {
            if (entry.getKey().getBlock() instanceof BlockCollisionTrigger trigger) {
                trigger.onEntityLeave(entry.getKey(), level, entry.getValue(), (Entity) (Object) this);
            }
            collidingBlocks.remove(entry.getKey());
        }
        leftBlocks.clear();
    }

    @Redirect(
        method = "pick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"
        )
    )
    private BlockHitResult portalCubed$portalCompatibleRaycast(Level world, ClipContext context) {
        return CrossPortalInteraction.blockInteractionRaycast(world, context);
    }

    private boolean intersects(AABB box, VoxelShape shape) {
        return shape.toAabbs().stream().anyMatch(box::intersects);
    }

    @Override
    public boolean cfg() {
        return false;
    }

    @Override
    public void setCFG() {
    }

//    @ModifyReturnValue(method = "getEyePosition", at = @At("RETURN"))
//    private Vec3 transformViaPortal(Vec3 original, float tickDelta) {
//        final Vec3 newPos = PortalDirectionUtils.simpleTransformPassingVector((Entity)(Object)this, getPosition(tickDelta), original);
//        return newPos != null ? newPos : original;
//    }
}
