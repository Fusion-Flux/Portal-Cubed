package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.entity.BlockCollisionLimiter;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.PortalCubedComponent;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.Gravity;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AdhesionGel extends GelFlat {

    private final BlockCollisionLimiter limiter = new BlockCollisionLimiter();

    public AdhesionGel(Settings settings) {
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

        BlockState state = world.getBlockState(pos);

        Vec3d direction = getDirections(state);

        Vec3d preChange;

            direction = RotationUtil.vecWorldToPlayer(direction, GravityChangerAPI.getGravityDirection( entity));
        GravityChangerAPI.addGravity( entity, new Gravity(GravityChangerAPI.getGravityDirection(entity),10,2,"adhesion_gel"));

            if (((EntityAttachments) entity).getGelTimer() == 0) {
                if (entity.verticalCollision) {

                    if (direction.y == 1 || Math.abs(direction.y) == 2 && vec3dLast.getY() > 0) {
                        //UP
                        ((EntityAttachments) entity).setGelTimer(10);
                        preChange = RotationUtil.vecPlayerToWorld(new Vec3d(0, 1, 0), GravityChangerAPI.getGravityDirection( entity));
                        //entity.setVelocity(0,0,0);
                        GravityChangerAPI.addGravity( entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z),10,2,"adhesion_gel"));
                    }
                }

                double defaultVelocity = Math.sqrt(2 * .08 * .25);
                if (entity.horizontalCollision) {
                    // if(GravityChangerAPI.getGravityDirection( entity) != Direction.NORTH)
                    if (direction.z == -1 || Math.abs(direction.z) == 2 && vec3dLast.getZ() < 0) {
                        //NORTH
                        ((EntityAttachments) entity).setGelTimer(10);
                        preChange = RotationUtil.vecPlayerToWorld(new Vec3d(0, 0, -1), GravityChangerAPI.getGravityDirection( entity));
                        //entity.setVelocity(0,0,0);
                        GravityChangerAPI.addGravity( entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z),10,2,"adhesion_gel"));
                    }

                    //if(GravityChangerAPI.getGravityDirection( entity) != Direction.SOUTH)
                    if (direction.z == 1 || Math.abs(direction.z) == 2 && vec3dLast.getZ() > 0) {
                        //SOUTH
                        ((EntityAttachments) entity).setGelTimer(10);
                        preChange = RotationUtil.vecPlayerToWorld(new Vec3d(0, 0, 1), GravityChangerAPI.getGravityDirection( entity));
                        //entity.setVelocity(0,0,0);
                        GravityChangerAPI.addGravity( entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z),10,2,"adhesion_gel"));
                    }

                    //if(GravityChangerAPI.getGravityDirection( entity) != Direction.EAST)
                    if (direction.x == 1 || Math.abs(direction.x) == 2 && vec3dLast.getX() > 0) {
                        // EAST
                        ((EntityAttachments) entity).setGelTimer(10);
                        preChange = RotationUtil.vecPlayerToWorld(new Vec3d(1, 0, 0), GravityChangerAPI.getGravityDirection( entity));
                        //entity.setVelocity(0,0,0);
                        GravityChangerAPI.addGravity( entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z),10,2,"adhesion_gel"));
                    }

                    //if(GravityChangerAPI.getGravityDirection( entity) != Direction.WEST)
                    if (direction.x == -1 || Math.abs(direction.x) == 2 && vec3dLast.getX() < 0) {
                        //WEST
                        ((EntityAttachments) entity).setGelTimer(10);
                        preChange = RotationUtil.vecPlayerToWorld(new Vec3d(-1, 0, 0), GravityChangerAPI.getGravityDirection( entity));
                        //entity.setVelocity(0,0,0);
                        GravityChangerAPI.addGravity( entity, new Gravity(Direction.fromVector((int) preChange.x, (int) preChange.y, (int) preChange.z),10,2,"adhesion_gel"));
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
