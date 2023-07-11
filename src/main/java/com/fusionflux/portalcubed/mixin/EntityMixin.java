package com.fusionflux.portalcubed.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.TeleportResult;
import com.fusionflux.portalcubed.accessor.*;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.CatapultBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.blocks.fizzler.AbstractFizzlerBlock;
import com.fusionflux.portalcubed.client.gui.ExpressionFieldWidget;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiApi;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.listeners.WentThroughPortalListener;
import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import com.fusionflux.portalcubed.util.GeneralUtil;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.objecthunter.exp4j.Expression;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExt, EntityPortalsAccess, ClientTeleportCheck {
    @Unique
    private double maxFallSpeed = 0;

    @Unique
    private boolean inFunnel = false;

    @Unique
    private double maxFallHeight = -99999999;

    @Unique
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
    public abstract boolean onGround();

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

    @Shadow public abstract Vec3 getPosition(float partialTicks);

    @Shadow public abstract Vec3 position();

    @Shadow public abstract Vec3 getEyePosition();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow public abstract boolean isEffectiveAi();

    @Shadow public boolean hasImpulse;

    @Shadow public abstract Level level();

    @Unique
    private final Map<BlockState, BlockPos> collidingBlocks = new HashMap<>();
    @Unique
    private final Map<BlockState, BlockPos> leftBlocks = new HashMap<>();

    @Unique
    private Pair<Vec3, Portal> portalEyeInfo;
    @Unique
    private Vec3 portalEyeInfoKey;
    @Unique
    private Pair<Vec3, Portal> portalEyeInfo2;
    @Unique
    private Vec3 portalEyeInfo2Key;

    @Unique
    private VelocityHelperBlockEntity velocityHelper;
    @Unique
    private long velocityHelperStartTime;
    @Unique
    private Vec3 velocityHelperOffset;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        Entity thiz = (Entity) (Object) this;

        if (!(thiz instanceof Player) && !(thiz instanceof Portal) && !thiz.getType().is(PortalCubedEntities.PORTAL_BLACKLIST)) {
            GeneralUtil.setupPortalShapes(thiz);
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

        if (maxFallSpeed == 10 && level().getBlockState(this.blockPosition()).getBlock() == PortalCubedBlocks.PROPULSION_GEL) {
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

        if (!this.onGround()) {
            if (rotatedPos.y > this.maxFallHeight) {
                this.maxFallHeight = rotatedPos.y;
            }
        } else {
            this.maxFallHeight = rotatedPos.y;
        }

        this.lastVel = this.getDeltaMovement();

        if (level().getBlockState(this.blockPosition()).getBlock() != PortalCubedBlocks.REPULSION_GEL && this.isBounced()) {
            this.setBounced(false);
        }

        prevGravDirec = GravityChangerAPI.getGravityDirection(((Entity) (Object) this));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTail(CallbackInfo ci) {
        Entity thisEntity = ((Entity) (Object) this);

        if (!thisEntity.level().isClientSide() && !(thisEntity instanceof Player) && !(thisEntity instanceof Portal) && !thisEntity.getType().is(PortalCubedEntities.PORTAL_BLACKLIST)) {
            Vec3 entityVelocity = this.getDeltaMovement();


            AABB portalCheckBox = getBoundingBox();

            portalCheckBox = portalCheckBox.expandTowards(entityVelocity.add(0, .08, 0));


            List<Portal> list = ((Entity) (Object) this).level().getEntitiesOfClass(Portal.class, portalCheckBox);
            Portal portal;
            for (Portal portalCheck : list) {
                portal = portalCheck;
                if (this.canChangeDimensions() && portal.getActive() && !CalledValues.getHasTeleportationHappened(thisEntity) && !CalledValues.getIsTeleporting(thisEntity)) {
                    assert portal.getOtherNormal().isPresent();
                    Direction portalFacing = portal.getFacingDirection();

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

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo ci) {
        if (entity instanceof CorePhysicsEntity || entity instanceof GelBlobEntity) {
            ci.cancel();
        }
    }

    @Unique
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
            Vec3.ZERO,
            thisEntity,
            Optional.empty(),
            thisEntity.getXRot(),
            thisEntity.getYRot()
        );

        final Vec3 dest = result.dest();
        thisEntity.moveTo(dest.x, dest.y, dest.z, result.yaw(), result.pitch());
        thisEntity.setDeltaMovement(result.velocity());
        GravityChangerAPI.clearGravity(thisEntity);
        if (level() instanceof ServerLevel serverWorld) {
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

    @ModifyArgs(
        method = "isInWall",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;ofSize(Lnet/minecraft/world/phys/Vec3;DDD)Lnet/minecraft/world/phys/AABB;"
        )
    )
    private void rotateInWallCheckBB(Args args) {
        getEyePosition();
        if (portalEyeInfo == null) return;
        final Vec3 newBB = portalEyeInfo.second().getTransformQuat().rotate(
            new Vec3(args.get(1), args.get(2), args.get(3)), false
        );
        args.set(1, newBB.x);
        args.set(2, newBB.y);
        args.set(3, newBB.z);
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
        return Shapes.joinUnoptimized(
            Shapes.joinUnoptimized(shape, CalledValues.getPortalCutout(((Entity)(Object)this)), BooleanOp.ONLY_FIRST),
            CalledValues.getCrossPortalCollision((Entity)(Object)this),
            BooleanOp.OR
        );
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
        return Shapes.joinUnoptimized(
            Shapes.joinUnoptimized(shape, CalledValues.getPortalCutout(((Entity)(Object)this)), BooleanOp.ONLY_FIRST),
            CalledValues.getCrossPortalCollision((Entity)(Object)this),
            BooleanOp.OR
        );
    }

    @Inject(method = "checkInsideBlocks", at = @At("HEAD"))
    private void beginBlockCheck(CallbackInfo ci) {
        leftBlocks.putAll(collidingBlocks);
    }

    @WrapOperation(
        method = "checkInsideBlocks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V"
        )
    )
    private void midBlockCheck(BlockState instance, Level level, BlockPos pos, Entity entity, Operation<Void> original) {
        original.call(instance, level, pos, entity);
        if (
            instance.getBlock() instanceof BlockCollisionTrigger trigger &&
                intersects(
                    entity.getBoundingBox().move(pos.multiply(-1)),
                    trigger.getTriggerShape(instance, level, pos, CollisionContext.of(entity))
                )
        ) {
            final BlockPos immutable = pos.immutable();
            if (collidingBlocks.put(instance, immutable) == null) {
                trigger.onEntityEnter(instance, level, immutable, entity);
            }
            leftBlocks.remove(instance);
        }
    }

    @Inject(method = "checkInsideBlocks", at = @At("TAIL"))
    private void endBlockCheck(CallbackInfo ci) {
        for (final var entry : leftBlocks.entrySet()) {
            if (entry.getKey().getBlock() instanceof BlockCollisionTrigger trigger) {
                trigger.onEntityLeave(entry.getKey(), level(), entry.getValue(), (Entity) (Object) this);
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

    @Unique
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
        return transformVecThroughPortal(position(), original);
    }

    @ModifyReturnValue(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
    private Vec3 transformViaPortalInterp(Vec3 original, float tickDelta) {
        return transformVecThroughPortal(getPosition(tickDelta), original);
    }

    @Unique
    private Vec3 transformVecThroughPortal(Vec3 base, Vec3 vec) {
        final Pair<Vec3, Portal> eyeInfo;
        if (base == portalEyeInfoKey) {
            eyeInfo = portalEyeInfo;
        } else if (base == portalEyeInfo2Key) {
            eyeInfo = portalEyeInfo2;
        } else {
            portalEyeInfo2 = portalEyeInfo;
            portalEyeInfo2Key = portalEyeInfoKey;
            eyeInfo = portalEyeInfo = PortalDirectionUtils.simpleTransformPassingVector(
                (Entity)(Object)this,
                base.add(0, 0.02, 0),
                vec, p -> p.getNormal().y < 0
            );
            portalEyeInfoKey = base;
        }
        return eyeInfo != null ? eyeInfo.first() : vec;
    }

    @ModifyReturnValue(method = "calculateViewVector", at = @At("RETURN"))
    private Vec3 transformViewVector(Vec3 original, float xRot, float yRot) {
        getEyePosition();
        if (portalEyeInfo == null) {
            return original;
        }
        return portalEyeInfo.second().getTransformQuat().rotate(original, false);
    }

    @WrapOperation(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void collideWithFizzlersOnMove(Entity self, double x, double y, double z, Operation<Void> original) {
        // this is done so collision works even when moving very fast.
        Vec3 pos = position();
        original.call(self, x, y, z);
        Vec3 newPos = position();
        // based on ProjectileUtil
        ClipContext ctx = new ClipContext(pos, newPos, Block.OUTLINE, Fluid.NONE, (Entity) (Object) this);
        BlockHitResult hit = level().clip(ctx);
        if (hit.getType() == Type.BLOCK) {
            BlockState state = level().getBlockState(hit.getBlockPos());
            if (state.getBlock() instanceof AbstractFizzlerBlock fizzler)
                fizzler.applyEffectsTo((Entity) (Object) this);
        }
    }

    @Override
    public void collidedWithVelocityHelper(VelocityHelperBlockEntity block) {
        if (!isEffectiveAi() || block.getDestination() == null) return;
        if (velocityHelper != null && block.getBlockPos().equals(velocityHelper.getBlockPos())) return;
        final Expression condition = block.getCondition();
        condition.setVariable("x", getDeltaMovement().x);
        condition.setVariable("y", getDeltaMovement().y);
        condition.setVariable("z", getDeltaMovement().z);
        try {
            if (condition.evaluate() == 0) return;
        } catch (RuntimeException e) {
            logVHWarning("condition", e);
            return;
        }
        velocityHelper = block;
        velocityHelperStartTime = level().getGameTime();
        velocityHelperOffset = Vec3.atCenterOf(block.getBlockPos()).subtract(position());
    }

    @Override
    public void collidedWithCatapult(CatapultBlockEntity block) {
        if (!isEffectiveAi()) return;
        final double relH = block.getRelH(position().x, position().z);
        final double relY = block.getRelY(position().y);
        final double angle = block.getAngle();
        final double speed = GeneralUtil.calculateVelocity(relH, relY, angle, -0.08 * PehkuiApi.INSTANCE.getFallingScale((Entity)(Object)this));
        if (!Double.isFinite(speed)) return;
        //noinspection ConstantValue
        if ((Object)this instanceof Player player) {
            player.setDiscardFriction(player.getItemBySlot(EquipmentSlot.FEET).is(PortalCubedItems.LONG_FALL_BOOTS));
        }
        RayonIntegration.INSTANCE.setVelocity((Entity)(Object)this, block.getLaunchDir(position().x, position().z).scale(Math.min(speed, 10)));
        hasImpulse = true;
    }

    @Unique
    private void logVHWarning(String type, RuntimeException e) {
        //noinspection ConstantValue
        if ((Object)this instanceof Player && level().isClientSide) {
            logVHWarningToChat(type, e);
        }
        PortalCubed.LOGGER.info("{} at {}", getVHWarning(type).getString(), velocityHelper.getBlockPos(), e);
    }

    @Unique
    @ClientOnly
    private void logVHWarningToChat(String type, RuntimeException e) {
        Minecraft.getInstance().gui.getChat().addMessage(getVHWarning(type));
        Minecraft.getInstance().gui.getChat().addMessage(
            Component.literal(ExpressionFieldWidget.cleanError(e)).withStyle(ChatFormatting.RED)
        );
    }

    @Unique
    private Component getVHWarning(String type) {
        return Component.translatable(
            "portalcubed.velocity_helper.failed_expression",
            Component.translatable("portalcubed.velocity_helper." + type + "_expression")
        ).withStyle(ChatFormatting.RED);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickVelocityHelper(CallbackInfo ci) {
        if (velocityHelper == null || velocityHelper.getDestination() == null) {
            velocityHelper = null;
            return;
        }
        double progress = (level().getGameTime() - velocityHelperStartTime) / (double)velocityHelper.getFlightDuration();
        if (progress >= 1.0) {
            velocityHelper = null;
            return;
        }
        if (progress < 0) {
            progress = 0;
        }
        final Expression curve = velocityHelper.getInterpolationCurve();
        curve.setVariable("x", progress);
        final double useProgress;
        try {
            useProgress = Mth.clamp(curve.evaluate(), 0, 1);
        } catch (RuntimeException e) {
            logVHWarning("curve", e);
            return;
        }
        assert velocityHelper.getDestination() != null;
        setDeltaMovement(new Vec3(
            Mth.lerp(useProgress, velocityHelper.getBlockPos().getX() + 0.5, velocityHelper.getDestination().getX() + 0.5),
            Mth.lerp(useProgress, velocityHelper.getBlockPos().getY() + 0.5, velocityHelper.getDestination().getY() + 0.5),
            Mth.lerp(useProgress, velocityHelper.getBlockPos().getZ() + 0.5, velocityHelper.getDestination().getZ() + 0.5)
        ).subtract(position()).subtract(velocityHelperOffset));
    }
}
