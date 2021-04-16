package com.fusionflux.thinkingwithportatos.physics;

import com.fusionflux.thinkingwithportatos.entity.PhysicsFallingBlockEntity;
import com.qouteall.immersive_portals.portal.Portal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public class GrabUtil {
    public static @Nullable Entity getEntityToGrab(PlayerEntity player) {
        Vec3d vec3d = player.getCameraPosVec(1.0f);
        Vec3d vec3d2 = player.getRotationVec(1.0f);
        double d = 5.0;
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        Box box = player.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);
        EntityHitResult result = raycast(player, vec3d, vec3d3, box, GrabUtil::isEntityGrabbable, d);

        if (result != null && result.getEntity() != null) {
            if (result.getEntity().hasVehicle()) {
                return result.getEntity().getVehicle();
            } else {
                return result.getEntity();
            }
        }

        return null;
    }

    public static @Nullable PhysicsFallingBlockEntity getBlockToGrab(PlayerEntity player) {
        HitResult result = player.raycast(4.5, 1.0f, false);

        if (result.getType() != HitResult.Type.MISS) {
            BlockPos pos = ((BlockHitResult) result).getBlockPos();
            BlockState state = player.world.getBlockState(pos);

            if (!state.getBlock().canMobSpawnInside() && player.world.getBlockEntity(pos) == null) {
                PhysicsFallingBlockEntity fallingBlock = new PhysicsFallingBlockEntity(player.world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);
                player.world.removeBlock(pos, false);
                player.world.spawnEntity(fallingBlock);
                return fallingBlock;
            }
        }

        return null;
    }

    public static EntityHitResult raycast(Entity entity, Vec3d vec3d, Vec3d vec3d2, Box box, Predicate<Entity> predicate, double d) {
        World world = entity.world;
        double e = d;
        Entity entity2 = null;
        Vec3d vec3d3 = null;
        Iterator var12 = world.getOtherEntities(entity, box, predicate).iterator();

        while (true) {
            while (var12.hasNext()) {
                Entity entity3 = (Entity) var12.next();
                Box box2 = entity3.getBoundingBox().expand((double) entity3.getTargetingMargin());
                Optional<Vec3d> optional = box2.raycast(vec3d, vec3d2);
                if (box2.contains(vec3d)) {
                    if (e >= 0.0D) {
                        entity2 = entity3;
                        vec3d3 = (Vec3d) optional.orElse(vec3d);
                        e = 0.0D;
                    }
                } else if (optional.isPresent()) {
                    Vec3d vec3d4 = (Vec3d) optional.get();
                    double f = vec3d.squaredDistanceTo(vec3d4);
                    if (f < e || e == 0.0D) {
                        if (entity3.getRootVehicle() == entity.getRootVehicle()) {
                            if (e == 0.0D) {
                                entity2 = entity3;
                                vec3d3 = vec3d4;
                            }
                        } else {
                            entity2 = entity3;
                            vec3d3 = vec3d4;
                            e = f;
                        }
                    }
                }
            }

            if (entity2 == null) {
                return null;
            }

            return new EntityHitResult(entity2, vec3d3);
        }
    }

    public static boolean isEntityGrabbable(Entity entity) {
        return !(entity instanceof Portal) && !(entity instanceof PlayerEntity);
    }
}
