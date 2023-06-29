package com.fusionflux.portalcubed.entity;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources.pcSources;

// TODO: Extend LivingEntity
public class CorePhysicsEntity extends PathfinderMob implements Fizzleable {

    private float fizzleProgress = 0f;
    private boolean fizzling = false;

    public CorePhysicsEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    private boolean canUsePortals = true;
    private boolean hasCollided;
    private int timeSinceLastSound;

    private List<UUID> intermediaryPortals = List.of();

    public Vec3 lastPos = this.position();

    private final Vec3 offsetHeight = new Vec3(0, this.getBbHeight() / 2, 0);

    @Override
    public boolean canBeCollidedWith() {
        return canUsePortals;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return other != this && canBeCollidedWith() && other instanceof LivingEntity && other.isAlive();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.isCreativePlayer() || source == damageSources().fellOutOfWorld())
            return false;
        if (!(source.getEntity() instanceof Player player))
            return true;
        return !player.getItemInHand(InteractionHand.MAIN_HAND).is(PortalCubedItems.WRENCHES);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && !isInvulnerableTo(source) && !isRemoved()) {
            dropAllDeathLoot(source);
            discard();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return super.getDisplayName().plainCopy();
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        ItemStack stack = super.getPickResult();
        if (stack != null)
            stack.setHoverName(getDisplayName());
        return stack;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDiscardFriction(true);
    }

    private static final EntityDataAccessor<Optional<UUID>> HOLDER_UUID = SynchedEntityData.defineId(CorePhysicsEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> ON_BUTTON = SynchedEntityData.defineId(CorePhysicsEntity.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(HOLDER_UUID, Optional.empty());
        this.getEntityData().define(ON_BUTTON, false);
    }

    public Optional<UUID> getHolderUUID() {
        return getEntityData().get(HOLDER_UUID);
    }

    public void setHolderUUID(Optional<UUID> uuid) {
        this.getEntityData().set(HOLDER_UUID, uuid);
    }

    public boolean isOnButton() {
        return getEntityData().get(ON_BUTTON);
    }

    public void setOnButton(boolean on) {
        getEntityData().set(ON_BUTTON, on);
    }

    public void setRotYaw(float yaw) {
        this.yBodyRot = yaw;
    }

    @Override
    public boolean canChangeDimensions() {
        return canUsePortals;
    }

    @Override
    public void tick() {
        super.tick();
        final boolean isBeingHeld = getHolderUUID().isPresent() && !fizzling;
        timeSinceLastSound++;
        this.hasImpulse = true;
        canUsePortals = getHolderUUID().isEmpty();
        Vec3 rotatedOffset = RotationUtil.vecPlayerToWorld(offsetHeight, GravityChangerAPI.getGravityDirection(this));
        this.lastPos = this.position();
        this.setDiscardFriction(!this.onGround() && !this.level().getBlockState(this.blockPosition()).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL));
        if (isBeingHeld) {
            Player player = (Player) ((LevelExt) level()).getEntityByUuid(getHolderUUID().get());
            if (player != null && player.isAlive()) {
                Vec3 vec3d = player.getEyePosition(0);
                double d = 1.5;
                canUsePortals = false;
                Vec3 vec3d2 = this.getPlayerRotationVector(player.getXRot(), player.getYRot());
                Vec3 vec3d3 = vec3d.add((vec3d2.x * d) - rotatedOffset.x, (vec3d2.y * d) - rotatedOffset.y, (vec3d2.z * d) - rotatedOffset.z);
                final AdvancedEntityRaycast.Result raycastResult = PortalDirectionUtils.raycast(level(), new ClipContext(
                    vec3d, vec3d3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this
                ));
                final Vec3 holdPos = raycastResult.finalHit().getLocation();
                if (!level().isClientSide) {
                    GravityChangerAPI.addGravity(this, new Gravity(GravityChangerAPI.getGravityDirection(player), 10, 1, "player_interaction"));
                }
                this.fallDistance = 0;
                if (RayonIntegration.INSTANCE.isPresent()) {
                    final float destYaw = Mth.wrapDegrees(player.yHeadRot + 180);
                    final float multiplier = (destYaw > 120 || destYaw < 0) ? -1 : 1;
                    final float yawDelta = Mth.wrapDegrees(
                        Mth.wrapDegrees(RayonIntegration.INSTANCE.getYaw(this) * multiplier) - destYaw
                    );
                    RayonIntegration.INSTANCE.rotateYaw(this, yawDelta);
                    RayonIntegration.INSTANCE.setAngularVelocityYaw(this, new Vector3f(0, yawDelta, 0));
                } else {
                    setYRot(player.yHeadRot);
                    setYHeadRot(player.yHeadRot);
                    setYBodyRot(player.yHeadRot);
                }
                final List<UUID> portals = raycastResult.rays()
                    .stream()
                    .map(AdvancedEntityRaycast.Result.Ray::hit)
                    .filter(h -> h instanceof EntityHitResult)
                    .map(h -> ((EntityHitResult)h).getEntity().getUUID())
                    .toList();
                if (!portals.equals(intermediaryPortals)) {
                    intermediaryPortals = portals;
                    moveTo(holdPos);
                } else {
                    final Vec3 movement = RotationUtil.vecWorldToPlayer(holdPos.subtract(position()), GravityChangerAPI.getGravityDirection(player));
                    if (RayonIntegration.INSTANCE.isPresent()) {
                        RayonIntegration.INSTANCE.setVelocity(this, movement);
                    }
                    RayonIntegration.INSTANCE.simpleMove(this, MoverType.PLAYER, movement);
                }
            } else {
                if (player != null) {
                    setHolderUUID(Optional.empty());
                }
                canUsePortals = true;
            }
            RayonIntegration.INSTANCE.setNoGravity(this, true);
        } else if (this.isNoGravity() && !fizzling && !((EntityExt)this).isInFunnel()) {
            RayonIntegration.INSTANCE.setNoGravity(this, false);
        }
        if (this.getDeltaMovement().y < -3.92) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, .81d, 0));
        }
        if (fizzling) {
            if (level().isClientSide) {
                fizzleProgress += Minecraft.getInstance().getFrameTime();
            } else {
                fizzleProgress += 0.05f;
                if (fizzleProgress >= 1f) {
                    remove(RemovalReason.KILLED);
                }
            }
        }
        if (getHolderUUID().isEmpty() && !level().isClientSide) {
            //noinspection DataFlowIssue
            level().getServer().getPlayerList().broadcast(
                null, getX(), getY(), getZ(), 64,
                level().dimension(),
                new ClientboundRotateHeadPacket(this, (byte)Mth.floor(getYHeadRot() * 256f / 360f))
            );
        }
        if (!RayonIntegration.INSTANCE.isPresent()) {
            setXRot(0f);
        }
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return !level().isClientSide || getHolderUUID().isPresent();
    }

    @NotNull
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
    }

    @NotNull
    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void startFizzlingProgress() {
        fizzling = true;
    }

    @Override
    public void fizzle() {
        if (fizzling) return;
        fizzling = true;
        level().playSound(null, getX(), getY(), getZ(), PortalCubedSounds.MATERIAL_EMANCIPATION_EVENT, SoundSource.NEUTRAL, 0.1f, 1f);
        RayonIntegration.INSTANCE.setNoGravity(this, true);
        final FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(getId());
        final Packet<?> packet = ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.FIZZLE_PACKET, buf);
        PlayerLookup.tracking(this).forEach(player -> player.connection.send(packet));
    }

    @Override
    public float getFizzleProgress() {
        return fizzleProgress;
    }

    @Override
    public boolean fizzling() {
        return fizzling;
    }

    @Override
    public boolean fizzlesInGoo() {
        return true;
    }

    @Override
    public FizzleType getFizzleType() {
        return FizzleType.OBJECT;
    }

    protected final Vec3 getPlayerRotationVector(float pitch, float yaw) {
        float f = pitch * (float) (Math.PI / 180.0);
        float g = -yaw * (float) (Math.PI / 180.0);
        float h = Mth.cos(g);
        float i = Mth.sin(g);
        float j = Mth.cos(f);
        float k = Mth.sin(f);
        return RotationUtil.vecPlayerToWorld(new Vec3(i * j, -k, h * j), GravityChangerAPI.getGravityDirection(this));
    }

    @Override
    public void move(MoverType movementType, Vec3 movement) {
        super.move(movementType, movement);
        if (horizontalCollision) {
            if (!hasCollided) {
                hasCollided = true;
                if (!level().isClientSide && timeSinceLastSound >= 20) {
                    level().playSound(null, this, getCollisionSound(), SoundSource.NEUTRAL, 1f, 1f);
                    timeSinceLastSound = 0;
                }
            }
        } else {
            hasCollided = false;
        }
    }

    protected SoundEvent getCollisionSound() {
        return PortalCubedSounds.CUBE_LOW_HIT_EVENT; // TODO: implement for other physics objects (this requires a lot of assets)
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide) //noinspection DataFlowIssue
            getHolderUUID().ifPresent(value -> PortalCubedComponents.HOLDER_COMPONENT.get(((ServerLevel) level()).getEntity(value)).stopHolding());
        super.remove(reason);
    }

    @Override
    public void checkDespawn() {
    }

    protected static AABB createFootBox(double x, double y, double z) {
        return new AABB(-x / 2, 0, -z / 2, x / 2, y, z / 2);
    }

    @NotNull
    @Override
    public Fallsounds getFallSounds() {
        return new Fallsounds(PortalCubedSounds.GENERIC_PHYSICS_FALL_EVENT, PortalCubedSounds.GENERIC_PHYSICS_FALL_EVENT);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (onGround) {
            if (state.isAir() && fallDistance > 0 && getType().is(PortalCubedEntities.P1_ENTITY)) {
                final List<Entity> collisions = level().getEntitiesOfClass(Entity.class, getBoundingBox().expandTowards(0, -0.1, 0), this::canCollideWith);
                for (final Entity collision : collisions) {
                    collision.hurt(pcSources(level()).cube(), fallDistance * 1.5f);
                }
            }
            fallDistance = 0;
        } else {
            fallDistance -= y;
        }
    }
}
