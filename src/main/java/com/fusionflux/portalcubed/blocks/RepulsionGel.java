package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.entity.BlockCollisionLimiter;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RepulsionGel extends GelFlat {

    private final BlockCollisionLimiter limiter = new BlockCollisionLimiter();

    public RepulsionGel(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
    }


    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity, pos);
    }

  /*  @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        //if (entity.bypassesLandingEffects()) {
        //    super.onLandedUpon(world, state, pos, entity, fallDistance);
        //} else {
            entity.handleFallDamage(fallDistance, 0.0F, DamageSource.FALL);
        //}

    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        //if (entity.bypassesLandingEffects()) {
        //    super.onEntityLand(world, entity);
        //} else {
            this.bounce(entity);
       // }

    }

    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
       // if (vec3d.y < 0.0) {
        double fall = 11+(11*.1);
        double velocity = Math.sqrt(2*.08*fall);
        entity.setVelocity(vec3d.x,velocity , vec3d.z);
        //entity.setVelocity(vec3d.x, ((-vec3d.y)+.12)/.98, vec3d.z);
       // }

    }
*/

    private void addCollisionEffects(World world, Entity entity, BlockPos pos) {
        Vec3d vec3d = entity.getVelocity();
        Vec3d vec3dLast = ((EntityAttachments) entity).getLastVel();
        // if (vec3d.y < 0.0) {
        BlockState state = world.getBlockState(pos);
        if (!entity.isSneaking()) {
            if (entity.verticalCollision) {
                if (state.get(DOWN) && vec3dLast.getY() < 0) {
                    double fall = ((EntityAttachments) entity).getMaxFallHeight();
                    if (fall != -100) {

                        fall = fall - entity.getPos().y;
                        if (fall < 5) {
                            fall = 5;
                        }
                        fall = fall + (fall * .1);
                        double velocity = Math.sqrt(2 * .08 * fall);
                        entity.setVelocity(vec3d.x, velocity, vec3d.z);
                        if (!world.isClient) {
                            world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                        }
                    }
                }
                if (state.get(UP) && vec3dLast.getY() > 0) {
                    entity.setVelocity(vec3d.x, -vec3dLast.y, vec3d.z);
                    if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                    }
                }
            }

            double defaultVelocity = Math.sqrt(2 * .08 * .25);
            if (entity.horizontalCollision) {
                if (state.get(NORTH) && vec3dLast.getZ() < 0) {
                    if (Math.abs(vec3dLast.z) < defaultVelocity) {
                        entity.setVelocity(vec3d.x, vec3d.y, defaultVelocity);
                    } else {
                        entity.setVelocity(vec3d.x, vec3d.y, -vec3dLast.z);
                    }

                    if (vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments) entity).getMaxFallHeight();
                        if (fall != -100) {

                            fall = fall - entity.getPos().y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            fall = fall + (fall * .1);
                            double velocity = Math.sqrt(2 * .08 * fall);
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments) entity).setMaxFallHeight(-100);
                        }
                        ///entity.setVelocity(vec3d.z, -vec3dLast.y, vec3d.z);
                    }
                    if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                    }
                }
                if (state.get(SOUTH) && vec3dLast.getZ() > 0) {
                    if (Math.abs(vec3dLast.z) < defaultVelocity) {
                        entity.setVelocity(vec3d.x, vec3d.y, -defaultVelocity);
                    } else {
                        entity.setVelocity(vec3d.x, vec3d.y, -vec3dLast.z);
                    }
                    if (vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments) entity).getMaxFallHeight();
                        if (fall != -100) {

                            fall = fall - entity.getPos().y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            fall = fall + (fall * .1);
                            double velocity = Math.sqrt(2 * .08 * fall);
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments) entity).setMaxFallHeight(-100);
                        }
                        ///entity.setVelocity(vec3d.z, -vec3dLast.y, vec3d.z);
                    }
                    if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                    }
                }
                if (state.get(EAST) && vec3dLast.getX() > 0) {

                    if (Math.abs(vec3dLast.x) < defaultVelocity) {
                        entity.setVelocity(-defaultVelocity, vec3d.y, vec3d.z);
                    } else {
                        entity.setVelocity(-vec3dLast.x, vec3d.y, vec3d.z);
                    }
                    if (vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments) entity).getMaxFallHeight();
                        if (fall != -100) {

                            fall = fall - entity.getPos().y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            fall = fall + (fall * .1);
                            double velocity = Math.sqrt(2 * .08 * fall);
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments) entity).setMaxFallHeight(-100);
                        }
                        ///entity.setVelocity(vec3d.z, -vec3dLast.y, vec3d.z);
                    }
                    if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                    }
                }
                if (state.get(WEST) && vec3dLast.getX() < 0) {
                    if (Math.abs(vec3dLast.x) < defaultVelocity) {
                        entity.setVelocity(defaultVelocity, vec3d.y, vec3d.z);
                    } else {
                        entity.setVelocity(-vec3dLast.x, vec3d.y, vec3d.z);
                    }
                    if (vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments) entity).getMaxFallHeight();
                        if (fall != -100) {

                            fall = fall - entity.getPos().y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            fall = fall + (fall * .1);
                            double velocity = Math.sqrt(2 * .08 * fall);
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments) entity).setMaxFallHeight(-100);
                        }
                        ///entity.setVelocity(vec3d.z, -vec3dLast.y, vec3d.z);
                    }
                    if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                    }
                }
            }
        }
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
