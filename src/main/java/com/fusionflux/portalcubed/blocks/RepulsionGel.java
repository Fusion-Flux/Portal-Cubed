package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.entity.BlockCollisionLimiter;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
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



    private void addCollisionEffects(World world, Entity entity, BlockPos pos) {
            /*if (entity.getType().equals(EntityType.BOAT)) {
                entity.damage(DamageSource.MAGIC, 200);
            } else {
                BlockState state = world.getBlockState(pos);
                if (!entity.isSneaking() && (entity.verticalCollision || entity.horizontalCollision)) {
                    //  if (entity.getVelocity().y < 1.65)
                    Vec3d direction = new Vec3d(0, 0, 0);
                    if (entity.verticalCollision) {
                        if (state.get(UP)) {
                            direction = direction.add(0, -1, 0);
                        }

                        if (state.get(DOWN)) {
                            direction = direction.add(0, 1, 0);
                           // System.out.println(direction);
                        }
                    }
                    if (entity.horizontalCollision) {
                        if (state.get(NORTH)) {
                            direction = direction.add(0, 0, 1);
                        }

                        if (state.get(SOUTH)) {
                            direction = direction.add(0, 0, -1);
                        }

                        if (state.get(EAST)) {
                            direction = direction.add(-1, 0, 0);
                        }

                        if (state.get(WEST)) {
                            direction = direction.add(1, 0, 0);
                        }
                        direction = direction.add(0, 0.45, 0);
                    }   //entity.setVelocity(entity.getVelocity().add(0, 1.65D, 0));

                    //if (limiter.check(world, entity)) {
                        if ( !direction.equals(new Vec3d(0, 0, 0))) {
                            if(world.isClient) {
                                if (limiter.check(world, entity)) {
                                    world.playSound((PlayerEntity) entity, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                                }
                                }else {
                                world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                            }
                            entity.setVelocity(entity.getVelocity().add(direction.x, direction.y, direction.z));
                        }
                   // }


                }
            }*/


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
