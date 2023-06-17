package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.TeleportResult;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.MixinPCClientAccessor;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import com.fusionflux.portalcubed.util.IPQuaternion;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements EntityAttachments {
    @Unique
    private boolean cfg;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @NotNull
    @Shadow
    @Override
    public abstract ItemStack getItemBySlot(@NotNull EquipmentSlot slot);

    @Shadow
    @Override
    public abstract void playSound(@NotNull SoundEvent sound, float volume, float pitch);

    @Shadow
    @Override
    public abstract boolean isSwimming();

    @Shadow @Final private Abilities abilities;

    @Override
    @Shadow public abstract float getSpeed();

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3 portalCubed$what(Vec3 travelVectorOriginal) {
        if (!this.isNoGravity() && !this.isOnGround() && PortalCubedConfig.enableAccurateMovement && !this.isSwimming() && !this.abilities.flying && !this.isFallFlying() && this.getItemBySlot(EquipmentSlot.FEET).getItem().equals(PortalCubedItems.LONG_FALL_BOOTS) && !this.level.getBlockState(this.blockPosition()).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL) && !this.level.getBlockState(new BlockPos(this.blockPosition().getX(), this.blockPosition().getY() + 1, this.blockPosition().getZ())).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) {
            double mathVal = 1;
            double horizontalVelocity = Math.abs(this.getDeltaMovement().x) + Math.abs(this.getDeltaMovement().z);
            if (horizontalVelocity / 0.01783440120041885 > 1) {
                mathVal = horizontalVelocity / 0.01783440120041885;
            }
            travelVectorOriginal = new Vec3(travelVectorOriginal.x / mathVal, travelVectorOriginal.y, travelVectorOriginal.z / mathVal);
        }
        if (CalledValues.getIsTeleporting(this)) {
            travelVectorOriginal = Vec3.ZERO;
        }

        return travelVectorOriginal;
    }

    @Inject(method = "getFlyingSpeed", at = @At("HEAD"), cancellable = true)
    private void replaceFlyingSpeed(CallbackInfoReturnable<Float> cir) {
        if (!this.isNoGravity() && !this.isOnGround() && PortalCubedConfig.enableAccurateMovement && !this.isSwimming() && !this.abilities.flying && !this.isFallFlying() && this.getItemBySlot(EquipmentSlot.FEET).getItem().equals(PortalCubedItems.LONG_FALL_BOOTS) && !this.level.getBlockState(this.blockPosition()).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL) && !this.level.getBlockState(new BlockPos(this.blockPosition().getX(), this.blockPosition().getY() + 1, this.blockPosition().getZ())).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) {
            cir.setReturnValue(0.04f);
        }
    }

    private boolean enableNoDrag2;

    private static final AABB NULL_BOX = new AABB(0, 0, 0, 0, 0, 0);
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHead(CallbackInfo ci) {
        Player thisEntity = ((Player) (Object) this);

        if (level.isClientSide && CalledValues.getHasTeleportationHappened(thisEntity)) {
            var byteBuf = PacketByteBufs.create();
            NetworkingSafetyWrapper.sendFromClient("client_teleport_update", byteBuf);
            CalledValues.setHasTeleportationHappened(thisEntity, false);
            ((EntityAttachments) thisEntity).setMaxFallHeight(-99999999);
            CalledValues.setIsTeleporting(thisEntity, false);
            this.setDeltaMovement(CalledValues.getVelocityUpdateAfterTeleport(thisEntity));
        }

        Vec3 entityVelocity = this.getDeltaMovement();

        AABB portalCheckBox = getBoundingBox();
        if (!this.level.isClientSide) {
            portalCheckBox = portalCheckBox.inflate(10);
        } else {
            portalCheckBox = portalCheckBox.expandTowards(entityVelocity);
        }
        List<Portal> list = level.getEntitiesOfClass(Portal.class, portalCheckBox);
        VoxelShape omittedDirections = Shapes.empty();

        for (Portal portal : list) {
            if (portal.calculateCutoutBox() != NULL_BOX) {
                if (portal.getActive())
                    omittedDirections = Shapes.or(omittedDirections, Shapes.create(portal.getCutoutBoundingBox()));
            }
        }
        CalledValues.setPortalCutout(((Player) (Object) this), omittedDirections);


        ItemStack itemFeet = this.getItemBySlot(EquipmentSlot.FEET);
        if ((!this.isOnGround() && PortalCubedConfig.enableAccurateMovement && !this.isSwimming() && !this.abilities.flying && !this.isFallFlying() && itemFeet.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS) && !this.level.getBlockState(this.blockPosition()).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL) && !this.level.getBlockState(new BlockPos(this.blockPosition().getX(), this.blockPosition().getY() + 1, this.blockPosition().getZ())).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL))) {
            if (!enableNoDrag2) {
                enableNoDrag2 = true;
            }
            this.setDiscardFriction(true);
        } else if (enableNoDrag2) {
            enableNoDrag2 = false;
            this.setDiscardFriction(false);
        }

        if (itemFeet.getItem().equals(PortalCubedItems.LONG_FALL_BOOTS)) {
            if (this.getDeltaMovement().y < -3.92) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, .081d, 0));
            }
        }

        if (!level.isClientSide || !MixinPCClientAccessor.allowCfg() || !isShiftKeyDown()) {
            cfg = false;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTail(CallbackInfo ci) {
        if (this.level.isClientSide) {
            Player thisEntity = ((Player) (Object) this);

            Vec3 entityVelocity = thisEntity.getDeltaMovement();

            AABB portalCheckBox = getBoundingBox();

            portalCheckBox = portalCheckBox.expandTowards(entityVelocity).expandTowards(entityVelocity.scale(-1));


            List<Portal> list = level.getEntitiesOfClass(Portal.class, portalCheckBox);

            Set<Pair<Portal, Vec3>> possiblePortals = new HashSet<>();


            for (Portal portalCheck : list) {
                if (this.canChangeDimensions() && portalCheck.getActive() && !CalledValues.getHasTeleportationHappened(thisEntity) && !CalledValues.getIsTeleporting(thisEntity)) {
                    assert portalCheck.getOtherNormal().isPresent();
                    Direction portalFacing = portalCheck.getFacingDirection();
                    final Vec3 otherNormal = portalCheck.getOtherNormal().get();
                    Direction otherDirec = Direction.fromNormal((int) otherNormal.x(), (int) otherNormal.y(), (int) otherNormal.z());
                    if (otherDirec != null) {
                        entityVelocity = thisEntity.getDeltaMovement();

                        if (thisEntity.shouldDiscardFriction()) {
                            entityVelocity = entityVelocity.add(0, .08, 0);
                        } else {
                            entityVelocity = entityVelocity.add(0, .08 * .98, 0);
                        }
//                        Vec3 entityPos = portalCheck.getNormal().y > 0 ? thisEntity.position() : thisEntity.getEyePosition();
                        Vec3 entityPos = thisEntity.position().add(0, portalCheck.getOtherNormal().get().y < 0 ? 0.02 - entityVelocity.y : thisEntity.getEyeHeight(), 0);
                        if (portalFacing.step().x() < 0) {
                            if (entityPos.x() + entityVelocity.x >= portalCheck.position().x() && entityVelocity.x() > 0 && portalCheck.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                possiblePortals.add(Pair.of(portalCheck, entityPos));
                            }
                        }
                        if (portalFacing.step().y() < 0) {
                            if (entityPos.y() + entityVelocity.y >= portalCheck.position().y() && entityVelocity.y() > 0 && portalCheck.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                possiblePortals.add(Pair.of(portalCheck, entityPos));
                            }
                        }
                        if (portalFacing.step().z() < 0) {
                            if (entityPos.z() + entityVelocity.z >= portalCheck.position().z() && entityVelocity.z() > 0 && portalCheck.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                possiblePortals.add(Pair.of(portalCheck, entityPos));
                            }
                        }
                        if (portalFacing.step().x() > 0) {
                            if (entityPos.x() + entityVelocity.x <= portalCheck.position().x() && entityVelocity.x() < 0 && portalCheck.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                possiblePortals.add(Pair.of(portalCheck, entityPos));
                            }
                        }
                        if (portalFacing.step().y() > 0) {
                            if (entityPos.y() + entityVelocity.y <= portalCheck.position().y() && entityVelocity.y() < 0 && portalCheck.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                possiblePortals.add(Pair.of(portalCheck, entityPos));
                            }
                        }
                        if (portalFacing.step().z() > 0) {
                            if (entityPos.z() + entityVelocity.z <= portalCheck.position().z() && entityVelocity.z() < 0 && portalCheck.calculateBoundsCheckBox().intersects(thisEntity.getBoundingBox())) {
                                possiblePortals.add(Pair.of(portalCheck, entityPos));
                            }
                        }

                    }
                }
            }
            Pair<Portal, Vec3> portal = null;
            double distance = 100;
            for (var portalTry : possiblePortals) {
                double checkDistance = portalTry.first().position().distanceTo(thisEntity.getBoundingBox().getCenter());
                if (checkDistance < distance) {
                    distance = checkDistance;
                    portal = portalTry;
                }
            }
            if (portal != null) {
                performTeleport(thisEntity, portal.first(), entityVelocity, portal.second());
            }

        }
    }

    private void performTeleport(
            Player thisEntity,
            Portal portal,
            Vec3 entityVelocity,
            Vec3 basePosition
    ) {
        if (this.level.isClientSide && thisEntity.isLocalPlayer()) {
            Vec3 invert = (portal.getNormal().multiply(portal.getNormal())).scale(-1);
            if (invert.x != 0) {
                invert = invert.add(0, 1, 1);
            } else if (invert.y != 0) {
                invert = invert.add(1, 0, 1);
            } else if (invert.z != 0) {
                invert = invert.add(1, 1, 0);
            }
            final Optional<IPQuaternion> cameraInterp = interpCamera();
            var byteBuf = PacketByteBufs.create();
            byteBuf.writeVarInt(portal.getId());
            byteBuf.writeFloat(thisEntity.getYRot());
            byteBuf.writeFloat(thisEntity.getXRot());
            byteBuf.writeOptional(cameraInterp, (b, q) -> {
                b.writeDouble(q.x);
                b.writeDouble(q.y);
                b.writeDouble(q.z);
                b.writeDouble(q.w);
            });
            byteBuf.writeDouble(entityVelocity.x);
            byteBuf.writeDouble(entityVelocity.y);
            byteBuf.writeDouble(entityVelocity.z);
            final Vec3 teleportOffset = new Vec3(
                ((basePosition.x()) - portal.position().x()) * invert.x,
                ((basePosition.y()) - portal.position().y()) * invert.y,
                ((basePosition.z()) - portal.position().z()) * invert.z
            );
            byteBuf.writeDouble(teleportOffset.x);
            byteBuf.writeDouble(teleportOffset.y);
            byteBuf.writeDouble(teleportOffset.z);
            NetworkingSafetyWrapper.sendFromClient("use_portal", byteBuf);
//            CalledValues.setIsTeleporting(thisEntity, true);

            final TeleportResult result = PortalCubed.commonTeleport(
                portal,
                entityVelocity,
                teleportOffset,
                thisEntity,
                cameraInterp,
                thisEntity.getXRot(),
                thisEntity.getYRot()
            );
            final Vec3 dest = result.dest();
            moveTo(dest.x, dest.y, dest.z, result.yaw(), result.pitch());
            setDeltaMovement(result.velocity());
            interpCamera(
                IPQuaternion.getCameraRotation(result.pitch(), result.yaw())
                    .getConjugated()
                    .hamiltonProduct(result.immediateFinalRot())
            );
        }
    }

    @ClientOnly
    private static Optional<IPQuaternion> interpCamera() {
        return PortalCubedClient.interpCamera();
    }

    @ClientOnly
    private static void interpCamera(IPQuaternion interp) {
        PortalCubedClient.cameraInterpStart = interp;
        PortalCubedClient.cameraInterpStartTime = System.currentTimeMillis();
    }

    @Inject(
        method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getEyeY()D"
        )
    )
    public void portalCubed$dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<@Nullable ItemEntity> cir) {
        if (!this.level.isClientSide && stack.getItem().equals(PortalCubedItems.PORTAL_GUN)) {
            CompoundTag tag = stack.getOrCreateTag();
            CompoundTag portalsTag = tag.getCompound(level.dimension().location().toString());
            Portal portalHolder;
            if (portalsTag.contains(("Left") + "Portal")) {
                portalHolder = (Portal) ((ServerLevel) level).getEntity(portalsTag.getUUID(("Left") + "Portal"));
                if (portalHolder != null) {
                    portalHolder.kill();
                }
            }
            if (portalsTag.contains(("Right") + "Portal")) {
                portalHolder = (Portal) ((ServerLevel) level).getEntity(portalsTag.getUUID(("Right") + "Portal"));
                if (portalHolder != null) {
                    portalHolder.kill();
                }
            }
        }
    }

    @Override
    public boolean cfg() {
        return cfg;
    }

    @Override
    public void setCFG() {
        cfg = true;
    }

    @WrapOperation(
        method = "updatePlayerPose",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/Pose;CROUCHING:Lnet/minecraft/world/entity/Pose;",
            opcode = Opcodes.GETSTATIC
        )
    )
    private Pose playerCrouching(Operation<Pose> original) {
        return PortalCubed.portalHudModeServerOrClient(level) ? Pose.SWIMMING : original.call();
    }
}
