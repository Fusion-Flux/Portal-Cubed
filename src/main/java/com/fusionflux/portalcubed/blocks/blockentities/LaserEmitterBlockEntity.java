package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.blocks.LaserCatcherBlock;
import com.fusionflux.portalcubed.blocks.LaserEmitterBlock;
import com.fusionflux.portalcubed.blocks.LaserRelayBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import com.fusionflux.portalcubed.particle.PortalCubedParticleTypes;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.GeneralUtil;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources.pcSources;

public class LaserEmitterBlockEntity extends BlockEntity {
    private record Target(@NotNull BlockPos pos, @Nullable Direction side) {
    }

    private final List<AdvancedEntityRaycast.Result> multiSegments = new ArrayList<>();
    private final Set<Target> targets = new HashSet<>();

    @ClientOnly
    private SoundInstance musicInstance;

    public LaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_EMITTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        final ListTag list = new ListTag();
        for (final Target target : targets) {
            final CompoundTag targetNbt = new CompoundTag();
            targetNbt.putIntArray("Target", new int[] {target.pos.getX(), target.pos.getY(), target.pos.getZ()});
            if (target.side != null) {
                targetNbt.putString("TargetSide", target.side.getName());
            }
            list.add(targetNbt);
        }
        tag.put("Targets", list);
    }

    @Override
    public void load(CompoundTag tag) {
        targets.clear();
        for (final Tag elem : tag.getList("Targets", Tag.TAG_COMPOUND)) {
            final CompoundTag targetNbt = (CompoundTag)elem;
            final int[] targetA = targetNbt.getIntArray("Target");
            if (targetA.length >= 3) {
                targets.add(new Target(
                    new BlockPos(targetA[0], targetA[1], targetA[2]),
                    Direction.byName(tag.getString("TargetSide"))
                ));
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        multiSegments.clear();
        if (!state.getValue(LaserEmitterBlock.POWERED)) {
            if (!level.isClientSide) {
                for (final Target target : targets) {
                    level.getBlockEntity(target.pos, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).ifPresent(LaserNodeBlockEntity::removeLaser);
                }
                targets.clear();
            }
            return;
        }
        Vec3 direction = Vec3.atLowerCornerOf(state.getValue(LaserEmitterBlock.FACING).getNormal());
        Vec3 start = Vec3.atCenterOf(pos).add(direction.scale(0.5));
        double lengthRemaining = PortalCubedConfig.maxBridgeLength;
        final Set<Entity> alreadyHit = new HashSet<>();
        BlockState hitState;
        do {
            //noinspection DataFlowIssue
            final AdvancedEntityRaycast.Result segments = AdvancedEntityRaycast.raycast(
                level,
                new ClipContext(
                    start, start.add(direction.scale(lengthRemaining)),
                    ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null
                ),
                PortalDirectionUtils.PORTAL_RAYCAST_TRANSFORM,
                new AdvancedEntityRaycast.TransformInfo(
                    e -> e instanceof RedirectionCubeEntity && !alreadyHit.contains(e),
                    (context, blockHit, entityHit) -> {
                        final RedirectionCubeEntity entity = (RedirectionCubeEntity)entityHit.getEntity();
                        alreadyHit.add(entity);
                        final double distance = context.getFrom().distanceTo(context.getTo());
                        final Vec3 offset = entityHit.getLocation().subtract(context.getFrom());
                        final RedirectionCubeEntity destination = entity.getConnection();
                        alreadyHit.add(destination);
                        destination.markActive();
                        final Vec3 newOffset = Vec3.directionFromRotation(destination.getXRot(), destination.getYRot())
                            .scale(distance - offset.length());
                        final Vec3 origin = destination.position().add(new Vec3(0, destination.getBbHeight() / 2, 0));
                        return new AdvancedEntityRaycast.TransformResult(
                            entityHit.getLocation().add(offset.scale(0.25 / offset.length())),
                            AdvancedEntityRaycast.withStartEnd(context, origin, origin.add(newOffset))
                        );
                    }
                ),
                new AdvancedEntityRaycast.TransformInfo(
                    e -> e instanceof CorePhysicsEntity && !alreadyHit.contains(e),
                    (context, blockHit, entityHit) -> new AdvancedEntityRaycast.TransformResult(entityHit.getLocation(), null)
                )
            );
            direction = segments.finalRay().relative().normalize();
            start = segments.finalRay().end();
            lengthRemaining -= segments.length();
            multiSegments.add(segments);
            if (segments.finalHit().getType() == HitResult.Type.BLOCK) {
                final BlockHitResult finalHit = (BlockHitResult)segments.finalHit();
                hitState = level.getBlockState(finalHit.getBlockPos());
                if (hitState.is(PortalCubedBlocks.REFLECTION_GEL)) {
                    final Direction.Axis axis = finalHit.getDirection().getAxis();
                    direction = direction.with(axis, -direction.get(axis));
                }
            } else {
                hitState = null;
            }
        } while (hitState != null && (hitState.is(PortalCubedBlocks.LASER_RELAY) || hitState.is(PortalCubedBlocks.REFLECTION_GEL)));

        if (level.isClientSide) {
            clientTick();
            return;
        }

        if (hitState != null && !(hitState.getBlock() instanceof LaserRelayBlock)) {
            final Vec3 finalPos = multiSegments.get(multiSegments.size() - 1).finalRay().end();
            ((ServerLevel)level).sendParticles(
                PortalCubedParticleTypes.ENERGY_SPARK,
                finalPos.x, finalPos.y, finalPos.z, 5, 0, 0, 0, 0.01
            );
        }

        Entity owner = EntityType.MARKER.create(level);
        assert owner != null;
        alreadyHit.clear();
        for (final AdvancedEntityRaycast.Result result : multiSegments) {
            for (final AdvancedEntityRaycast.Result.Ray ray : result.rays()) {
                Vec3 rayStart = ray.start();
                EntityHitResult hit;
                do {
                    hit = new AdvancedEntityRaycast.Result.Ray(rayStart, ray.end(), null).entityRaycast(owner, e -> e instanceof LivingEntity && !alreadyHit.contains(e));
                    if (hit != null) {
                        rayStart = hit.getLocation();
                        final Entity hitEntity = hit.getEntity();
                        alreadyHit.add(hitEntity);
                        owner = hitEntity;
                        if (hitEntity instanceof CorePhysicsEntity) {
                            continue; // TODO: Turrets and chairs burn
                        }
                        if (!hitEntity.isOnGround()) continue;
                        hitEntity.hurt(pcSources(level).laser(), PortalCubedConfig.laserDamage);
                        hitEntity.setRemainingFireTicks(Math.max(10, hitEntity.getRemainingFireTicks()));
                        final Vec3 velocity = GeneralUtil.calculatePerpendicularVector(ray.start(), ray.end(), hitEntity.position())
                            .normalize()
                            .scale(1.25);
                        hitEntity.push(velocity.x, velocity.y, velocity.z);
                    }
                } while (hit != null && !GeneralUtil.targetsEqual(hit, ray.hit()));
                if (ray.hit() instanceof EntityHitResult entityHitResult) {
                    owner = entityHitResult.getEntity();
                }
            }
        }

        final Set<Target> newTargets = new HashSet<>();
        final Object2IntMap<BlockPos> changes = new Object2IntOpenHashMap<>(targets.size() + multiSegments.size());
        for (final AdvancedEntityRaycast.Result result : multiSegments) {
            if (result.finalHit().getType() != HitResult.Type.BLOCK) continue;
            final BlockHitResult finalHit = (BlockHitResult)result.finalHit();
            final BlockState hitState1 = level.getBlockState(finalHit.getBlockPos());
            if (hitState1.is(PortalCubedBlocks.LASER_CATCHER) && finalHit.getDirection() != hitState1.getValue(LaserCatcherBlock.FACING)) continue;
            final Target target = new Target(
                finalHit.getBlockPos(),
                hitState1.is(PortalCubedBlocks.LASER_RELAY) ? null : finalHit.getDirection()
            );
            newTargets.add(target);
            if (targets.add(target)) {
                changes.put(target.pos, changes.getOrDefault(target.pos, 0) + 1);
            }
        }

        for (final Target target : targets) {
            if (!newTargets.contains(target)) {
                changes.put(target.pos, changes.getOrDefault(target.pos, 0) - 1);
            }
        }
        targets.retainAll(newTargets);

        for (final var entry : changes.object2IntEntrySet()) {
            if (entry.getIntValue() == 0) continue;
            final LaserNodeBlockEntity entity = level.getBlockEntity(entry.getKey(), PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).orElse(null);
            if (entity == null) continue;
            if (entry.getIntValue() > 0) {
                for (int i = 0; i < entry.getIntValue(); i++) {
                    entity.addLaser();
                }
            } else {
                for (int i = 0; i < -entry.getIntValue(); i++) {
                    entity.removeLaser();
                }
            }
        }
        if (!changes.isEmpty()) {
            setChanged();
        }
    }

    public List<AdvancedEntityRaycast.Result> getMultiSegments() {
        return multiSegments;
    }

    @ClientOnly
    protected void clientTick() {
        if (musicInstance == null && level != null && level.getBlockState(worldPosition).getValue(LaserEmitterBlock.POWERED)) {
            musicInstance = new AbstractTickableSoundInstance(PortalCubedSounds.LASER_BEAM_MUSIC_EVENT, SoundSource.BLOCKS, SoundInstance.createUnseededRandom()) {
                {
                    volume = 0.025f;
                    looping = true;
                }

                @Override
                public void tick() {
                    if (isRemoved() || level == null || !level.getBlockState(worldPosition).getValue(LaserEmitterBlock.POWERED)) {
                        musicInstance = null;
                        stop();
                        return;
                    }
                    final Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                    Vec3 usePos = null;
                    double distanceSq = Double.POSITIVE_INFINITY;
                    for (final AdvancedEntityRaycast.Result segments : multiSegments) {
                        for (final AdvancedEntityRaycast.Result.Ray ray : segments.rays()) {
                            Vec3 tryPos = GeneralUtil.nearestPointOnLine(
                                ray.start(), ray.end().subtract(ray.start()), cameraPos
                            );
                            double tryDistance = tryPos.distanceToSqr(cameraPos);
                            if (tryPos.distanceToSqr(ray.start()) > ray.end().distanceToSqr(ray.start())) {
                                tryPos = ray.end();
                                tryDistance = tryPos.distanceToSqr(cameraPos);
                            } else if (tryPos.distanceToSqr(ray.end()) > ray.end().distanceToSqr(ray.start())) {
                                tryPos = ray.start();
                                tryDistance = tryPos.distanceToSqr(cameraPos);
                            }
                            if (tryDistance < distanceSq) {
                                usePos = tryPos;
                                distanceSq = tryDistance;
                            }
                        }
                    }
                    if (usePos == null) {
                        musicInstance = null;
                        stop();
                        return;
                    }
                    x = usePos.x;
                    y = usePos.y;
                    z = usePos.z;
                }
            };
            Minecraft.getInstance().getSoundManager().play(musicInstance);
        }
    }
}
