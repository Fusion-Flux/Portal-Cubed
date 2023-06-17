package com.fusionflux.portalcubed.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.TeleportResult;
import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.ClientTeleportCheck;
import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.listeners.WentThroughPortalListener;
import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Shadow public abstract DamageSources damageSources();

    @Shadow public abstract Vec3 getPosition(float partialTicks);

    @Shadow public abstract Vec3 position();

    @Shadow public abstract Vec3 getEyePosition();

    private static final AABB NULL_BOX = new AABB(0, 0, 0, 0, 0, 0);

    @Unique
    private final Map<BlockState, BlockPos> collidingBlocks = new HashMap<>();
    @Unique
    private final Map<BlockState, BlockPos> leftBlocks = new HashMap<>();

    @Unique
    private Portal viewTranslatingPortal;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        Entity thisentity = ((Entity) (Object) this);

        Vec3 entityVelocity = this.getDeltaMovement();


        if (!(thisentity instanceof Player)) {
            AABB portalCheckBox = getBoundingBox();

            portalCheckBox = portalCheckBox.expandTowards(entityVelocity.add(0, .08, 0));

            List<Portal> list = ((Entity) (Object) this).level.getEntitiesOfClass(Portal.class, portalCheckBox);
            VoxelShape omittedDirections = Shapes.empty();

            for (Portal portal : list) {
                if (portal.calculateCutoutBox() != NULL_BOX && portal.calculateBoundsCheckBox() != NULL_BOX) {
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

        if (!thisEntity.level.isClientSide() && !(thisEntity instanceof Player) && !(thisEntity instanceof Portal)) {
            Vec3 entityVelocity = this.getDeltaMovement();


            AABB portalCheckBox = getBoundingBox();

            portalCheckBox = portalCheckBox.expandTowards(entityVelocity.add(0, .08, 0));


            List<Portal> list = ((Entity) (Object) this).level.getEntitiesOfClass(Portal.class, portalCheckBox);
            Portal portal;
            for (Portal portalCheck : list) {
                portal = portalCheck;
                if (this.canChangeDimensions() && portal.getActive() && !CalledValues.getHasTeleportationHappened(thisEntity) && !CalledValues.getIsTeleporting(thisEntity)) {
                    assert portal.getOtherNormal().isPresent();
                    Direction portalFacing = portal.getFacingDirection();
                    final Vec3 otherNormal = portal.getOtherNormal().get();
                    Direction otherDirec = Direction.fromNormal((int) otherNormal.x(), (int) otherNormal.y(), (int) otherNormal.z());

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
            Portal portal,
            Vec3 entityVelocity
    ) {
        final TeleportResult result = PortalCubed.commonTeleport(
            portal,
            entityVelocity,
            new Vec3(
                (thisEntity.getEyePosition().x()) - portal.position().x(),
                (thisEntity.getEyePosition().y()) - portal.position().y(),
                (thisEntity.getEyePosition().z()) - portal.position().z()
            ),
            thisEntity,
            Optional.empty(),
            thisEntity.getXRot(),
            thisEntity.getYRot()
        );

        final Vec3 dest = result.dest();
        thisEntity.moveTo(dest.x, dest.y, dest.z, result.yaw(), result.pitch());
        thisEntity.setDeltaMovement(result.velocity());
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
                serverWorld.sendParticles(player, true, dest.x, dest.y, dest.z, packet);
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

    @ModifyArg(
        method = "method_30022",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/shapes/Shapes;joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z"
        ),
        index = 0
    )
    private VoxelShape cutoutForIsInWall(VoxelShape shape) {
        return Shapes.joinUnoptimized(shape, CalledValues.getPortalCutout(((Entity)(Object)this)), BooleanOp.ONLY_FIRST);
    }

    @ModifyArg(
        method = "isColliding",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/shapes/Shapes;joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z"
        ),
        index = 0
    )
    private VoxelShape cutoutForIsColliding(VoxelShape shape) {
        return Shapes.joinUnoptimized(shape, CalledValues.getPortalCutout(((Entity)(Object)this)), BooleanOp.ONLY_FIRST);
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

    @ModifyReturnValue(method = "getEyePosition()Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
    private Vec3 transformViaPortalNoInterp(Vec3 original) {
        final var newPos = PortalDirectionUtils.simpleTransformPassingVector(
            (Entity)(Object)this,
            position().add(0, 0.02, 0),
            original, p -> p.getNormal().y < 0
        );
        viewTranslatingPortal = newPos != null ? newPos.second() : null;
        return newPos != null ? newPos.first() : original;
    }

    @ModifyReturnValue(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
    private Vec3 transformViaPortalInterp(Vec3 original, float tickDelta) {
        final var newPos = PortalDirectionUtils.simpleTransformPassingVector(
            (Entity)(Object)this,
            getPosition(tickDelta).add(0, 0.02, 0),
            original, p -> p.getNormal().y < 0
        );
        viewTranslatingPortal = newPos != null ? newPos.second() : null;
        return newPos != null ? newPos.first() : original;
    }

    @ModifyReturnValue(method = "calculateViewVector", at = @At("RETURN"))
    private Vec3 transformViewVector(Vec3 original, float xRot, float yRot) {
        getEyePosition(); // Force a recalculation of viewTranslatingPortal
        if (viewTranslatingPortal == null) {
            return original;
        }
        return viewTranslatingPortal.getTransformQuat().rotate(original, false);
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void portalCubed$letYouFallLonger(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        //noinspection ConstantValue
        if (!((Object)this instanceof LivingEntity living)) return;
        ItemStack stack = living.getItemBySlot(EquipmentSlot.FEET);
        if (damageSource == damageSources().fall() && (stack.is(PortalCubedItems.LONG_FALL_BOOTS))) {
            cir.setReturnValue(true);
        }
    }
}
