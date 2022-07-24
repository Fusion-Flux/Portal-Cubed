package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.util.CustomProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class ExcursionFunnelMain extends BlockWithEntity {

    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final BooleanProperty RNORTH;
    public static final BooleanProperty REAST;
    public static final BooleanProperty RSOUTH;
    public static final BooleanProperty RWEST;
    public static final BooleanProperty RUP;
    public static final BooleanProperty RDOWN;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public ExcursionFunnelMain(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false).with(RNORTH, false).with(REAST, false).with(RSOUTH, false).with(RWEST, false).with(RUP, false).with(RDOWN, false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }


    static {
        NORTH = Properties.NORTH;
        EAST = Properties.EAST;
        SOUTH = Properties.SOUTH;
        WEST = Properties.WEST;
        UP = Properties.UP;
        DOWN = Properties.DOWN;
        RNORTH = CustomProperties.RNORTH;
        REAST = CustomProperties.REAST;
        RSOUTH = CustomProperties.RSOUTH;
        RWEST = CustomProperties.RWEST;
        RUP = CustomProperties.RUP;
        RDOWN = CustomProperties.RDOWN;
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, RNORTH, REAST, RWEST, RSOUTH, RUP, RDOWN);
    }


    @Override
    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }



    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity, pos ,state);
    }

    public static Vec3d getPushDirection(BlockState state) {
        Vec3d result = Vec3d.ZERO;
        boolean modifyX = false;
        boolean modifyY = false;
        boolean modifyZ = false;


        if(state.get(Properties.NORTH)){
            result =result.subtract(0, 0, 1);
        }
        if(state.get(Properties.SOUTH)){
            result =result.add(0, 0, 1);
        }
        if(state.get(Properties.EAST)){
            result =result.add(1, 0, 0);
        }
        if(state.get(Properties.WEST)){
            result =result.subtract(1, 0, 0);
        }
        if(state.get(Properties.UP)){
            result =result.add(0, 1, 0);
        }
        if(state.get(Properties.DOWN)){
            result =result.subtract(0, 1, 0);
        }

        if(state.get(Properties.NORTH) && state.get(Properties.SOUTH)){
            modifyZ = true;
        }
        if(state.get(Properties.EAST) && state.get(Properties.WEST)){
            modifyX = true;
        }
        if(state.get(Properties.UP) && state.get(Properties.DOWN)){
            modifyY = true;
        }

        if(state.get(CustomProperties.RSOUTH)){
            result =result.subtract(0, 0, 1);
        }
        if(state.get(CustomProperties.RNORTH)){
            result =result.add(0, 0, 1);
        }
        if(state.get(CustomProperties.RWEST)){
            result =result.add(1, 0, 0);
        }
        if(state.get(CustomProperties.REAST)){
            result =result.subtract(1, 0, 0);
        }
        if(state.get(CustomProperties.RUP)){
            result =result.subtract(0, 1, 0);
        }
        if(state.get(CustomProperties.RDOWN)){
            result = result.add(0, 1, 0);
        }

        if(state.get(CustomProperties.RNORTH) && state.get(CustomProperties.RSOUTH)){
            modifyZ = true;
        }
        if(state.get(CustomProperties.REAST) && state.get(CustomProperties.RWEST)){
            modifyX = true;
        }
        if(state.get(CustomProperties.RUP) && state.get(CustomProperties.RDOWN)){
            modifyY = true;
        }

        if(modifyX){
            result = new Vec3d(.00001,result.getY(), result.getZ() );
        }
        if(modifyY){
            result = new Vec3d(result.getX(),.00001, result.getZ() );
        }
        if(modifyZ){
            result = new Vec3d(result.getX(),result.getY(), .00001 );
        }


        return result;
    }


    private void addCollisionEffects(World world, Entity entity, BlockPos pos,BlockState state) {
        if(entity instanceof PlayerEntity) {
            if (world.isClient()) {
                //RotationUtil.boxWorldToPlayer(entity.getBoundingBox(),GravityChangerAPI.getGravityDirection((PlayerEntity) entity))
                Vec3d entityCenter =entity.getBoundingBox().getCenter();
                double xoffset = (entityCenter.getX() - pos.getX()-.5);
                double yoffset = (entityCenter.getY() - pos.getY()-.5);
                double zoffset = (entityCenter.getZ() - pos.getZ()-.5);
                Vec3d direction = getPushDirection(state);

                direction = direction.multiply(.125);


                Vec3d preDirection = direction;
                direction = RotationUtil.vecWorldToPlayer(direction, GravityChangerAPI.getGravityDirection((PlayerEntity) entity));

                entity.setNoGravity(true);

                    if (!((EntityAttachments) entity).isInFunnel()) {
                        ((EntityAttachments) entity).setInFunnel(true);
                        entity.setVelocity(0, 0, 0);
                    }

                    ((EntityAttachments) entity).setFunnelTimer(2);

                Vec3d gotVelocity = entity.getVelocity();

                if (direction.x != 0) {
                    gotVelocity = new Vec3d(direction.getX(), gotVelocity.y, gotVelocity.z);
                }else{
                    gotVelocity = gotVelocity.add( RotationUtil.vecWorldToPlayer(new Vec3d((-(xoffset / Math.abs(xoffset)) * .004), 0, 0), GravityChangerAPI.getGravityDirection((PlayerEntity) entity)));
                }
                if (direction.y != 0) {
                    gotVelocity = new Vec3d (gotVelocity.x, direction.getY(), gotVelocity.z);
                }else{
                    gotVelocity = gotVelocity.add( RotationUtil.vecWorldToPlayer(new Vec3d(0, (-(yoffset / Math.abs(yoffset)) * .004), 0), GravityChangerAPI.getGravityDirection((PlayerEntity) entity)));
                }
                if (direction.z != 0) {
                    gotVelocity = new Vec3d(gotVelocity.x, gotVelocity.y, direction.getZ());
                }
                else{
                    gotVelocity = gotVelocity.add( RotationUtil.vecWorldToPlayer(new Vec3d(0, 0, (-(zoffset / Math.abs(zoffset)) * .004)), GravityChangerAPI.getGravityDirection((PlayerEntity) entity)));
                }
                entity.setVelocity(gotVelocity);
                entity.velocityModified = true;
            }
        }else{
            if (!world.isClient()) {
                double xoffset = (entity.getPos().getX() - pos.getX()) - .5;
                double yoffset = ((entity.getPos().getY()+entity.getHeight()/2) - pos.getY()) - .5;
                double zoffset = (entity.getPos().getZ() - pos.getZ()) - .5;
                Vec3d direction = getPushDirection(state);
                direction = direction.multiply(.125);

                entity.setNoGravity(true);

                if (!((EntityAttachments) entity).isInFunnel()) {
                    ((EntityAttachments) entity).setInFunnel(true);
                    entity.setVelocity(0, 0, 0);
                }

                entity.fallDistance = 0;

                ((EntityAttachments) entity).setFunnelTimer(2);

                if (direction.x != 0) {
                    entity.setVelocity(direction.getX(), entity.getVelocity().y, entity.getVelocity().z);
                }else{
                    entity.addVelocity((-(xoffset / Math.abs(xoffset)) * .004), 0, 0);
                }
                if (direction.y != 0) {
                    entity.setVelocity(entity.getVelocity().x, direction.getY(), entity.getVelocity().z);
                }else{
                    entity.addVelocity(0, (-(yoffset / Math.abs(yoffset)) * .004), 0);
                }
                if (direction.z != 0) {
                    entity.setVelocity(entity.getVelocity().x, entity.getVelocity().y, direction.getZ());
                }
                else{
                    entity.addVelocity(0, 0, (-(zoffset / Math.abs(zoffset)) * .004));
                }
                entity.velocityModified = true;
            }
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExcursionFunnelEntityMain(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.EXCURSION_FUNNEL_ENTITY, ExcursionFunnelEntityMain::tick);
    }

}