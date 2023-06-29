package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.blocks.BaseGel;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.HashSet;
import java.util.Set;

public abstract class GelBlobEntity extends Projectile {
    private static final Vec3[] ANGLES;
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(GelBlobEntity.class, EntityDataSerializers.INT);

    static {
        final Set<Vec3> angles = new HashSet<>();
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                angles.add(Vec3.directionFromRotation(360 / 128f * x, 360 / 128f * y - 128));
            }
        }
        ANGLES = angles.toArray(new Vec3[0]);
    }

    public GelBlobEntity(EntityType<? extends GelBlobEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SIZE, 1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Size", Tag.TAG_INT)) {
            setSize(nbt.getInt("Size"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Size", getSize());
    }

    public int getSize() {
        return entityData.get(SIZE);
    }

    public void setSize(int size) {
        entityData.set(SIZE, size);
    }

    public float getScale() {
        final int size = getSize();
        return (6 * (size + 1) - size) / 16f;
    }

    public int getExplosionRadius() {
        return 2 * getSize();
    }

    @Override
    public void tick() {
        super.tick();
        if (fluidHeight.values().doubleStream().anyMatch(d -> d != 0)) {
            kill();
        }
        boolean bl = this.noPhysics;
        Vec3 vec3d = this.getDeltaMovement();

        Vec3 vec3d3 = this.position();
        Vec3 vec3d2 = vec3d3.add(vec3d);
        HitResult hitResult = this.level().clip(new ClipContext(vec3d3, vec3d2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d2 = hitResult.getLocation();
        }

        EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d2);
        if (entityHitResult != null) {
            hitResult = entityHitResult;
        }

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            //noinspection DataFlowIssue
            Entity entity = ((EntityHitResult)hitResult).getEntity();
            Entity entity2 = this.getOwner();
            if (entity instanceof Player && entity2 instanceof Player && !((Player)entity2).canHarmPlayer((Player)entity)) {
                hitResult = null;
            }
        }

        if (hitResult != null && !bl) {
            this.onHit(hitResult);
            this.hasImpulse = true;
        }

        vec3d = this.getDeltaMovement();
        double e = vec3d.x;
        double f = vec3d.y;
        double g = vec3d.z;

        double h = this.getX() + e;
        double j = this.getY() + f;
        double k = this.getZ() + g;

        float m = 0.99F;
        if (this.isInWater()) {
            kill();
        }

        this.setDeltaMovement(vec3d.scale(m));
        if (!this.isNoGravity() && !bl) {
            Vec3 vec3d4 = this.getDeltaMovement();
            this.setDeltaMovement(vec3d4.x, vec3d4.y - 0.05F, vec3d4.z);
        }

        this.setPos(h, j, k);
        this.checkInsideBlocks();
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3 currentPosition, Vec3 nextPosition) {
        return ProjectileUtil.getEntityHitResult(
            this.level(), this, currentPosition, nextPosition, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity
        );
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) return;
        kill();
        if (!level().isClientSide) {
            if (hitResult instanceof EntityHitResult ehr && ehr.getEntity() instanceof ServerPlayer serverPlayer) {
                final FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(getGel()));
                ServerPlayNetworking.send(
                    serverPlayer,
                    PortalCubedClientPackets.GEL_OVERLAY_PACKET,
                    buf
                );
            }
            explode();
        }
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        super.checkFallDamage(heightDifference, onGround, landedState, landedPosition);
        kill();
        if (!level().isClientSide) {
            explode();
        }
    }

    public void explode() {
        level().playSound(null, getX(), getY(), getZ(), PortalCubedSounds.GEL_SPLAT_EVENT, SoundSource.NEUTRAL, 0.5f, 1f);
        final FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(getGel()));
        final int overlayDiameter = getSize() + 1;
        level().getEntities(
            EntityType.PLAYER,
            AABB.ofSize(position(), overlayDiameter, overlayDiameter, overlayDiameter),
            p -> p instanceof ServerPlayer
        ).forEach(p -> ServerPlayNetworking.send(
            (ServerPlayer)p,
            PortalCubedClientPackets.GEL_OVERLAY_PACKET,
            buf
        ));
        final int radius = getExplosionRadius();
        final Vec3 origin = getBoundingBox().getCenter();
        if (radius == 0) {
            final BlockPos originPos = BlockPos.containing(origin);
            for (final Direction dir : Direction.values()) {
                maybeExplodeAt(new BlockHitResult(
                    origin.add(Vec3.atLowerCornerOf(dir.getNormal())),
                    dir.getOpposite(),
                    originPos.relative(dir),
                    false
                ));
            }
            return;
        }
        for (final Vec3 angle : ANGLES) {
            final BlockHitResult hit = level().clip(new ClipContext(
                origin, origin.add(angle.scale(radius)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
            ));
            if (hit.getType() != HitResult.Type.BLOCK) continue;
            maybeExplodeAt(hit);
        }
    }

    private void maybeExplodeAt(BlockHitResult hit) {
        final BlockState hitState = level().getBlockState(hit.getBlockPos());
        if (!hitState.isFaceSturdy(level(), hit.getBlockPos(), hit.getDirection())) return;
        final BlockPos sidePos = hit.getBlockPos().relative(hit.getDirection());
        final BlockState sideState = level().getBlockState(sidePos);
        final BooleanProperty property = MultifaceBlock.getFaceProperty(hit.getDirection().getOpposite());
        if (sideState.is(getGel())) {
            level().setBlockAndUpdate(sidePos, sideState.setValue(property, true));
        } else if (sideState.canBeReplaced() || sideState.getBlock() instanceof BaseGel) {
            level().setBlockAndUpdate(sidePos, getGel().defaultBlockState().setValue(property, true));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
//        if (amount != 0 &&
//            (!(source instanceof EntityDamageSource eds) ||
//                (eds.getEntity() instanceof Player player &&
//                    player.isCreative()))
//        ) {
        if (
            amount != 0 &&
                (source.getDirectEntity() == null ||
                    (source.getDirectEntity() instanceof Player player &&
                        player.isCreative()))
        ) {
            remove(RemovalReason.KILLED);
        }
        return false;
    }

    public abstract ResourceLocation getTexture();

    public abstract BaseGel getGel();
}
