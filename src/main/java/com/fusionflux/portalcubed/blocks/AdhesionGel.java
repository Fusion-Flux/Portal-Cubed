package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.entity.BlockCollisionLimiter;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.Gravity;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AdhesionGel extends GelFlat {

    private final BlockCollisionLimiter limiter = new BlockCollisionLimiter();

    public AdhesionGel(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
    }

    public static final BiMap<Direction, BooleanProperty> dirToProperty = ImmutableBiMap.of(
            Direction.NORTH, Properties.NORTH,
            Direction.SOUTH, Properties.SOUTH,
            Direction.EAST, Properties.EAST,
            Direction.WEST, Properties.WEST,
            Direction.UP, Properties.UP,
            Direction.DOWN, Properties.DOWN
    );


    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(!world.isClient)
        this.addCollisionEffects(world, entity, pos,state);
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

    public Vec3d getGravityFromState(BlockState state) {
        Vec3d result = Vec3d.ZERO;
        final Vec3d[] finalResult = {result}; // bruh
        BiMap<BooleanProperty, Direction> propertyToDir = dirToProperty.inverse();
        state.getProperties().stream().map(property -> ((BooleanProperty) property)).filter(property -> state.get(property) && !property.getName().equals("waterlogged")).map(property -> Vec3d.of(propertyToDir.get(property).getVector())).forEach(vec -> finalResult[0] = finalResult[0].add(vec));
        result = finalResult[0];
        if (state.get(Properties.SOUTH) && state.get(Properties.NORTH)) {
            result = result.add(0, 0, 2);
        }
        if (state.get(Properties.EAST) && state.get(Properties.WEST)) {
            result = result.add(2, 0, 0);
        }

//        if (state.get(Properties.UP) && state.get(Properties.DOWN)) { this probably shouldn't be here, if you're being squeezed you shouldn't fly up, todo, check this for me
//            result = result.add(0, 2, 0);
//        }

        return result;
    }

    private void addCollisionEffects(World world, Entity entity, BlockPos pos, BlockState state) {
        Vec3d vec3dLast = RotationUtil.vecWorldToPlayer(entity.getPos().subtract(((EntityAttachments) entity).getLastPos()), GravityChangerAPI.getGravityDirection(entity));

        Vec3d direction = getGravityFromState(state);

        Vec3d preChange;

        direction = RotationUtil.vecWorldToPlayer(direction, GravityChangerAPI.getGravityDirection(entity));

        if (((EntityAttachments) entity).getGelTimer() == 0) {
           // if (entity.verticalCollision) {
            if (direction.y == -1) {
                //    preChange = RotationUtil.vecPlayerToWorld(new Vec3d(0, -1, 0), GravityChangerAPI.getGravityDirection(entity));
                GravityChangerAPI.addGravity(entity, new Gravity(GravityChangerAPI.getGravityDirection(entity), 10, 2, "adhesion_gel"));
            }
            if (((EntityAttachments) entity).getGelChangeTimer() == 0) {
                if (direction.y == 1 && vec3dLast.getY() > 0) {
                    ((EntityAttachments) entity).setGelTimer(1);
                    ((EntityAttachments) entity).setGelChangeTimer(10);
                    preChange = RotationUtil.vecPlayerToWorld(new Vec3d(0, 1, 0), GravityChangerAPI.getGravityDirection(entity));
                    GravityChangerAPI.addGravity(entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z), 10, 2, "adhesion_gel"));
                }
            //}
            //if (entity.horizontalCollision) {
                if (direction.z == -1 && vec3dLast.getZ() < 0) {
                    ((EntityAttachments) entity).setGelTimer(1);
                    ((EntityAttachments) entity).setGelChangeTimer(10);
                    preChange = RotationUtil.vecPlayerToWorld(new Vec3d(0, 0, -1), GravityChangerAPI.getGravityDirection(entity));
                    GravityChangerAPI.addGravity(entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z), 10, 2, "adhesion_gel"));
                }
                if (direction.z == 1 && vec3dLast.getZ() > 0) {
                    ((EntityAttachments) entity).setGelTimer(1);
                    ((EntityAttachments) entity).setGelChangeTimer(10);
                    preChange = RotationUtil.vecPlayerToWorld(new Vec3d(0, 0, 1), GravityChangerAPI.getGravityDirection(entity));
                    GravityChangerAPI.addGravity(entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z), 10, 2, "adhesion_gel"));
                }
                if (direction.x == 1 && vec3dLast.getX() > 0) {
                    ((EntityAttachments) entity).setGelTimer(1);
                    ((EntityAttachments) entity).setGelChangeTimer(10);
                    preChange = RotationUtil.vecPlayerToWorld(new Vec3d(1, 0, 0), GravityChangerAPI.getGravityDirection(entity));
                    GravityChangerAPI.addGravity(entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z), 10, 2, "adhesion_gel"));
                }
                if (direction.x == -1 && vec3dLast.getX() < 0) {
                    ((EntityAttachments) entity).setGelTimer(1);
                    ((EntityAttachments) entity).setGelChangeTimer(10);
                    preChange = RotationUtil.vecPlayerToWorld(new Vec3d(-1, 0, 0), GravityChangerAPI.getGravityDirection(entity));
                    GravityChangerAPI.addGravity(entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z), 10, 2, "adhesion_gel"));
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
