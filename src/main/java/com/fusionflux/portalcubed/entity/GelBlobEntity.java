package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.HashSet;
import java.util.Set;

public abstract class GelBlobEntity extends ProjectileEntity {
    private static final Vec3d[] ANGLES;
    private static final TrackedData<Integer> SIZE = DataTracker.registerData(GelBlobEntity.class, TrackedDataHandlerRegistry.INTEGER);

    static {
        final Set<Vec3d> angles = new HashSet<>();
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                angles.add(Vec3d.fromPolar(360 / 128f * x, 360 / 128f * y - 128));
            }
        }
        ANGLES = angles.toArray(new Vec3d[0]);
    }

    public GelBlobEntity(EntityType<? extends GelBlobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(SIZE, 1);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Size", NbtElement.INT_TYPE)) {
            setSize(nbt.getInt("Size"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Size", getSize());
    }

    public int getSize() {
        return dataTracker.get(SIZE);
    }

    public void setSize(int size) {
        dataTracker.set(SIZE, size);
    }

    public float getScale() {
        final int size = getSize();
        return (6 * (size + 1) - size) / 16f;
    }

    public int getExplosionRadius() {
        return 3 * getSize();
    }

    @Override
    public void tick() {
        super.tick();
        if (fluidHeight.values().doubleStream().anyMatch(d -> d != 0)) {
            kill();
        }
        boolean bl = this.noClip;
        Vec3d vec3d = this.getVelocity();

        Vec3d vec3d3 = this.getPos();
        Vec3d vec3d2 = vec3d3.add(vec3d);
        HitResult hitResult = this.world.raycast(new RaycastContext(vec3d3, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d2 = hitResult.getPos();
        }

        EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d2);
        if (entityHitResult != null) {
            hitResult = entityHitResult;
        }

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            //noinspection DataFlowIssue
            Entity entity = ((EntityHitResult)hitResult).getEntity();
            Entity entity2 = this.getOwner();
            if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
                hitResult = null;
            }
        }

        if (hitResult != null && !bl) {
            this.onCollision(hitResult);
            this.velocityDirty = true;
        }

        vec3d = this.getVelocity();
        double e = vec3d.x;
        double f = vec3d.y;
        double g = vec3d.z;

        double h = this.getX() + e;
        double j = this.getY() + f;
        double k = this.getZ() + g;

        float m = 0.99F;
        if (this.isTouchingWater()) {
            kill();
        }

        this.setVelocity(vec3d.multiply(m));
        if (!this.hasNoGravity() && !bl) {
            Vec3d vec3d4 = this.getVelocity();
            this.setVelocity(vec3d4.x, vec3d4.y - 0.05F, vec3d4.z);
        }

        this.setPosition(h, j, k);
        this.checkBlockCollision();
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(
            this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit
        );
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) return;
        kill();
        if (!world.isClient) {
            explode();
        }
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        super.fall(heightDifference, onGround, landedState, landedPosition);
        kill();
        if (!world.isClient) {
            explode();
        }
    }

    public void explode() {
        world.playSound(null, getX(), getY(), getZ(), PortalCubedSounds.GEL_SPLAT_EVENT, SoundCategory.NEUTRAL, 0.5f, 1f);
        world.getEntitiesByType(
            EntityType.PLAYER,
            Box.of(getPos(), 1, 1, 1),
            p -> p instanceof ServerPlayerEntity
        ).forEach(p -> ServerPlayNetworking.send(
            (ServerPlayerEntity)p,
            PortalCubedClientPackets.GEL_OVERLAY_PACKET,
            PacketByteBufs.create()
        ));
        final int radius = getExplosionRadius();
        final Vec3d origin = getBoundingBox().getCenter();
        if (radius == 0) {
            final BlockPos originPos = new BlockPos(origin);
            for (final Direction dir : Direction.values()) {
                maybeExplodeAt(new BlockHitResult(
                    origin.add(Vec3d.of(dir.getVector())),
                    dir.getOpposite(),
                    originPos.offset(dir),
                    false
                ));
            }
            return;
        }
        for (final Vec3d angle : ANGLES) {
            final BlockHitResult hit = world.raycast(new RaycastContext(
                origin, origin.add(angle.multiply(radius)),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
            ));
            if (hit.getType() != HitResult.Type.BLOCK) continue;
            maybeExplodeAt(hit);
        }
    }

    private void maybeExplodeAt(BlockHitResult hit) {
        final BlockState hitState = world.getBlockState(hit.getBlockPos());
        if (!hitState.isSideSolidFullSquare(world, hit.getBlockPos(), hit.getSide())) return;
        final BlockPos sidePos = hit.getBlockPos().offset(hit.getSide());
        final BlockState sideState = world.getBlockState(sidePos);
        final BooleanProperty property = GelFlat.getFacingProperty(hit.getSide().getOpposite());
        if (sideState.isOf(getGel())) {
            world.setBlockState(sidePos, sideState.with(property, true));
        } else if (sideState.getMaterial().isReplaceable() || sideState.getBlock() instanceof GelFlat) {
            world.setBlockState(sidePos, getGel().getDefaultState().with(property, true));
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (amount != 0 &&
            (!(source instanceof EntityDamageSource eds) ||
                (eds.getAttacker() instanceof PlayerEntity player &&
                    player.isCreative()))
        ) {
            remove(RemovalReason.KILLED);
        }
        return false;
    }

    public abstract Identifier getTexture();

    public abstract GelFlat getGel();
}
