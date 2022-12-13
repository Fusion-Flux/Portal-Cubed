package com.fusionflux.portalcubed.blocks;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.entity.BlockCollisionLimiter;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RepulsionGel extends GelFlat {

    public RepulsionGel(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
    }


    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity, pos);
    }

    public Vec3d getDirections(BlockState state){
        Vec3d result = Vec3d.ZERO;

        if(state.get(Properties.NORTH)){
            result =result.subtract(0, 0, 1);
        }
        if(state.get(Properties.SOUTH)){
            result =result.add(0, 0, 1);
        }

        if(state.get(Properties.SOUTH) && state.get(Properties.NORTH)){
            result =result.add(0, 0, 2);
        }

        if(state.get(Properties.EAST)){
            result =result.add(1, 0, 0);
        }
        if(state.get(Properties.WEST)){
            result =result.subtract(1, 0, 0);
        }

        if(state.get(Properties.EAST) && state.get(Properties.WEST)){
            result =result.add(2, 0, 0);
        }

        if(state.get(Properties.UP)){
            result =result.add(0, 1, 0);
        }
        if(state.get(Properties.DOWN)){
            result =result.subtract(0, 1, 0);
        }

        if(state.get(Properties.UP) && state.get(Properties.DOWN)){
            result =result.add(0, 2, 0);
        }



        return result;
    }

    private void addCollisionEffects(World world, Entity entity, BlockPos pos) {
        Vec3d vec3dLast = ((EntityAttachments) entity).getLastVel();
        Vec3d vec3d = new Vec3d(Math.max(entity.getVelocity().getX(),vec3dLast.getX()),Math.max(entity.getVelocity().getY(),vec3dLast.getY()),Math.max(entity.getVelocity().getZ(),vec3dLast.getZ()));
        // if (vec3d.y < 0.0) {
        BlockState state = world.getBlockState(pos);

        Vec3d direction = getDirections(state);
        Vec3d rotatedPos = entity.getPos();
        //if(entity instanceof PlayerEntity){
            direction = RotationUtil.vecWorldToPlayer(direction, GravityChangerAPI.getGravityDirection(entity));
            rotatedPos = RotationUtil.vecWorldToPlayer(rotatedPos, GravityChangerAPI.getGravityDirection(entity));
        //}
        boolean canBounce = !(entity instanceof PlayerEntity) || world.isClient;

        if (!entity.isSneaking()) {
            if (entity.verticalCollision) {
                if ((direction.y == -1 || Math.abs(direction.y) == 2)  && (vec3dLast.getY() < 0 || Math.abs(vec3d.getX()) + Math.abs(vec3d.getZ()) > 0.6)) {
                    double fall = ((EntityAttachments) entity).getMaxFallHeight();
                    if (fall != rotatedPos.y || Math.abs(vec3d.getX()) + Math.abs(vec3d.getZ()) > 0.6) {

                        fall = fall - rotatedPos.y;
                        //System.out.println(fall);
                        //System.out.println(vec3dLast+"last");
                        //System.out.println(vec3d+"max");
                        if (fall < 5) {
                            fall = 5;
                        }
                        //fall = fall + (fall * .1);
                        //System.out.println(fall);
                            //fall = fall * 1.21;
                        //+ (1-.799999999999983)
                        //+ (1-0.9908902300206535)
                        //+ (1-0.9991905510704413)
                        //+ (1-0.9999265614187323)
                        //+ (1-0.9999933249809487)
                        //1-1.7197979746446634
                        //+ (2-1.7197979746446634) + (2-1.9880945919865383) + (2-1.9991181013993327) + (2-1.9999335328092798) + (2-1.9999949841262605)
                        double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                        entity.setOnGround(false);
                        entity.setVelocity(vec3d.x, velocity, vec3d.z);
                        ((EntityAttachments) entity).setMaxFallHeight(rotatedPos.y);
                        //if (!world.isClient) {
                            world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                        //}
                    }
                }
                if (direction.y == 1 || Math.abs(direction.y) == 2 && vec3dLast.getY() > 0) {
                    entity.setVelocity(vec3d.x, -vec3dLast.y, vec3d.z);
                    //if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                    //}
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
                if(Math.abs(vec3dLast.z) > .1)
                    if (vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments) entity).getMaxFallHeight();
                        if (fall != rotatedPos.y) {

                            fall = fall - rotatedPos.y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }

                            double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments) entity).setMaxFallHeight(rotatedPos.y);
                        }
                        ///entity.setVelocity(vec3d.z, -vec3dLast.y, vec3d.z);
                    }

                    //if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                    //}
                }
                if (direction.z == 1 || Math.abs(direction.z) == 2 && vec3dLast.getZ() > 0) {
                    if (Math.abs(vec3dLast.z) < defaultVelocity) {
                        entity.setVelocity(vec3d.x, vec3d.y, -defaultVelocity);
                    } else {
                        entity.setVelocity(vec3d.x, vec3d.y, -vec3dLast.z);
                    }
                    if(Math.abs(vec3dLast.z) > .1)
                    if (vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments) entity).getMaxFallHeight();
                        if (fall != rotatedPos.y) {

                            fall = fall - rotatedPos.y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments) entity).setMaxFallHeight(rotatedPos.y);
                        }
                        ///entity.setVelocity(vec3d.z, -vec3dLast.y, vec3d.z);
                    }
                    //if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                    //}
                }
                if (direction.x == 1 || Math.abs(direction.x) == 2 && vec3dLast.getX() > 0) {

                    if (Math.abs(vec3dLast.x) < defaultVelocity) {
                        entity.setVelocity(-defaultVelocity, vec3d.y, vec3d.z);
                    } else {
                        entity.setVelocity(-vec3dLast.x, vec3d.y, vec3d.z);
                    }
                    if(Math.abs(vec3dLast.x) > .1)
                    if (vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments) entity).getMaxFallHeight();
                        if (fall != rotatedPos.y) {

                            fall = fall - rotatedPos.y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments) entity).setMaxFallHeight(rotatedPos.y);
                        }
                        ///entity.setVelocity(vec3d.z, -vec3dLast.y, vec3d.z);
                    }
                    //if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                    //}
                }
                if (direction.x == -1 || Math.abs(direction.x) == 2 && vec3dLast.getX() < 0) {
                    if (Math.abs(vec3dLast.x) < defaultVelocity) {
                        entity.setVelocity(defaultVelocity, vec3d.y, vec3d.z);
                    } else {
                        entity.setVelocity(-vec3dLast.x, vec3d.y, vec3d.z);
                    }
                    if(Math.abs(vec3dLast.x) > .1)
                    if (vec3dLast.getY() != 0) {
                        double fall = ((EntityAttachments) entity).getMaxFallHeight();
                        if (fall != rotatedPos.y) {

                            fall = fall - rotatedPos.y;
                            if (fall < 1.5) {
                                fall = 1.5;
                            }
                            double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
                            entity.setVelocity(vec3d.x, velocity, vec3d.z);
                            ((EntityAttachments) entity).setMaxFallHeight(rotatedPos.y);
                        }
                        ///entity.setVelocity(vec3d.z, -vec3dLast.y, vec3d.z);
                    }
                   // if (!world.isClient) {
                        world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_BOUNCE_EVENT, SoundCategory.BLOCKS, .3F, 1F);
                    //}
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
