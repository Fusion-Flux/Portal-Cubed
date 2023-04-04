package com.fusionflux.portalcubed.blocks;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RepulsionGel extends BaseGel {

    public RepulsionGel(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity, pos);
    }

    public Vec3d getDirections(BlockState state) {
        Vec3d result = Vec3d.ZERO;

        if (state.get(Properties.NORTH)) {
            result = result.subtract(0, 0, 1);
        }
        if (state.get(Properties.SOUTH)) {
            result = result.add(0, 0, 1);
        }

        if (state.get(Properties.SOUTH) && state.get(Properties.NORTH)) {
            result = result.add(0, 0, 2);
        }

        if (state.get(Properties.EAST)) {
            result = result.add(1, 0, 0);
        }
        if (state.get(Properties.WEST)) {
            result = result.subtract(1, 0, 0);
        }

        if (state.get(Properties.EAST) && state.get(Properties.WEST)) {
            result = result.add(2, 0, 0);
        }

        if (state.get(Properties.UP)) {
            result = result.add(0, 1, 0);
        }
        if (state.get(Properties.DOWN)) {
            result = result.subtract(0, 1, 0);
        }

        if (state.get(Properties.UP) && state.get(Properties.DOWN)) {
            result = result.add(0, 2, 0);
        }

        return result;
    }

    private void addCollisionEffects(World world, Entity entity, BlockPos pos) {
        Vec3d vec3dLast = ((EntityAttachments) entity).getLastVel();
        Vec3d vec3d = new Vec3d(Math.max(entity.getVelocity().getX(), vec3dLast.getX()), Math.max(entity.getVelocity().getY(), vec3dLast.getY()), Math.max(entity.getVelocity().getZ(), vec3dLast.getZ()));
        BlockState state = world.getBlockState(pos);

        Vec3d direction = getDirections(state);
        Vec3d rotatedPos = entity.getPos();
        direction = RotationUtil.vecWorldToPlayer(direction, GravityChangerAPI.getGravityDirection(entity));
        rotatedPos = RotationUtil.vecWorldToPlayer(rotatedPos, GravityChangerAPI.getGravityDirection(entity));

        if (!entity.bypassesLandingEffects()) {
            final boolean jumping = entity instanceof LivingEntityAccessor living && living.isJumping();
            if (entity.verticalCollision || jumping) {
                final boolean speedGel = Math.abs(vec3d.getX()) + Math.abs(vec3d.getZ()) > 0.6;
                if ((direction.y == -1 || Math.abs(direction.y) == 2)  && (vec3dLast.getY() < 0 || speedGel || jumping)) {
                    double fall = ((EntityAttachments) entity).getMaxFallHeight();
                    if (fall != rotatedPos.y || speedGel) {

                        fall = fall - rotatedPos.y;
                        if (fall < 5) {
                            fall = 5;
                        }
                        double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                        entity.setOnGround(false);
                        entity.setVelocity(vec3d.x, velocity, vec3d.z);
                        ((EntityAttachments) entity).setMaxFallHeight(rotatedPos.y);
                        PortalCubed.playBounceSound(entity);
                        if (entity instanceof PlayerEntity && world.isClient && (jumping || speedGel)) {
                            PortalCubed.playBounceSoundRemotely();
                        }
                    }
                }
                if (direction.y == 1 || Math.abs(direction.y) == 2 && vec3dLast.getY() > 0) {
                    entity.setVelocity(vec3d.x, -vec3dLast.y, vec3d.z);
                    PortalCubed.playBounceSound(entity);
                }
            }

            double defaultVelocity = Math.sqrt(2 * .08 * .25);
            if (entity.horizontalCollision) {
                if (direction.z == -1 || Math.abs(direction.z) == 2 && vec3dLast.getZ() < 0) {
                    if (Math.abs(vec3dLast.z) < defaultVelocity) {
                        entity.setVelocity(vec3d.x, vec3d.y, defaultVelocity);
                    } else {
                        entity.setVelocity(vec3d.x, vec3d.y, -vec3dLast.z);
                    }
                    if (Math.abs(vec3dLast.z) > .1) {
                        if (vec3dLast.getY() != 0) {
                            double fall = ((EntityAttachments)entity).getMaxFallHeight();
                            if (fall != rotatedPos.y) {

                                fall = fall - rotatedPos.y;
                                if (fall < 1.5) {
                                    fall = 1.5;
                                }

                                double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                                entity.setVelocity(vec3d.x, velocity, vec3d.z);
                                ((EntityAttachments)entity).setMaxFallHeight(rotatedPos.y);
                            }
                        }
                    }

                    PortalCubed.playBounceSound(entity);
                }
                if (direction.z == 1 || Math.abs(direction.z) == 2 && vec3dLast.getZ() > 0) {
                    if (Math.abs(vec3dLast.z) < defaultVelocity) {
                        entity.setVelocity(vec3d.x, vec3d.y, -defaultVelocity);
                    } else {
                        entity.setVelocity(vec3d.x, vec3d.y, -vec3dLast.z);
                    }
                    if (Math.abs(vec3dLast.z) > .1 && vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments)entity).getMaxFallHeight();
                        if (fall != rotatedPos.y) {

                            fall = fall - rotatedPos.y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments)entity).setMaxFallHeight(rotatedPos.y);
                        }
                    }
                    PortalCubed.playBounceSound(entity);
                }
                if (direction.x == 1 || Math.abs(direction.x) == 2 && vec3dLast.getX() > 0) {

                    if (Math.abs(vec3dLast.x) < defaultVelocity) {
                        entity.setVelocity(-defaultVelocity, vec3d.y, vec3d.z);
                    } else {
                        entity.setVelocity(-vec3dLast.x, vec3d.y, vec3d.z);
                    }
                    if (Math.abs(vec3dLast.x) > .1 && vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments)entity).getMaxFallHeight();
                        if (fall != rotatedPos.y) {

                            fall = fall - rotatedPos.y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments)entity).setMaxFallHeight(rotatedPos.y);
                        }
                    }
                    PortalCubed.playBounceSound(entity);
                }
                if (direction.x == -1 || Math.abs(direction.x) == 2 && vec3dLast.getX() < 0) {
                    if (Math.abs(vec3dLast.x) < defaultVelocity) {
                        entity.setVelocity(defaultVelocity, vec3d.y, vec3d.z);
                    } else {
                        entity.setVelocity(-vec3dLast.x, vec3d.y, vec3d.z);
                    }
                    if (Math.abs(vec3dLast.x) > .1 && vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments)entity).getMaxFallHeight();
                        if (fall != rotatedPos.y) {

                            fall = fall - rotatedPos.y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments)entity).setMaxFallHeight(rotatedPos.y);
                        }
                    }
                    PortalCubed.playBounceSound(entity);
                }
            }
        }
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.bypassesLandingEffects()) {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        } else {
            entity.handleFallDamage(fallDistance, 0.0F, DamageSource.FALL);
        }
    }
}
