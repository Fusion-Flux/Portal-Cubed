package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class HardLightBridgeEmitterBlock extends BlockWithEntity {

    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 12.0D, 0.0D, 14.0D, 13.0D, 16.0D);
    protected static final VoxelShape SHAPEROTATED = Block.createCuboidShape(0.0D, 12.0D, 2.0D, 16.0D, 13.0D, 14.0D);

    protected static final VoxelShape MAINNORTH = Block.createCuboidShape(0.0D, 9.5D, 13.25D, 16.0D, 15.5D, 16.0D);
    protected static final VoxelShape MAINSOUTH = Block.createCuboidShape(0.0D, 9.5D, 0D, 16.0D, 15.5D, 2.75D);
    protected static final VoxelShape MAINEAST = Block.createCuboidShape(0D, 9.5D, 0D, 2.75D, 15.5D, 16.0D);
    protected static final VoxelShape MAINWEST = Block.createCuboidShape(13.25D, 9.5D, 0D, 16.0D, 15.5D, 16.0D);


    private final Map<BlockState, VoxelShape> field_26659;


    public HardLightBridgeEmitterBlock(Settings settings) {
        super(settings);
        this.field_26659 = ImmutableMap.copyOf((Map) this.stateManager.getStates().stream().collect(Collectors.toMap(Function.identity(), HardLightBridgeEmitterBlock::method_31018)));

    }

    private static VoxelShape method_31018(BlockState blockState) {
        VoxelShape voxelShape = VoxelShapes.empty();


        if (blockState.get(Properties.FACING)==Direction.NORTH) {
            voxelShape =MAINNORTH;
            if (blockState.get(Properties.POWERED)) {
                voxelShape =VoxelShapes.union(voxelShape, SHAPE);
            }
        }

        if (blockState.get(Properties.FACING)==Direction.SOUTH) {
            voxelShape = MAINSOUTH;
            if (blockState.get(Properties.POWERED)) {
                voxelShape =VoxelShapes.union(voxelShape, SHAPE);
            }
        }

        if (blockState.get(Properties.FACING)==Direction.EAST) {
            voxelShape = MAINEAST;
            if (blockState.get(Properties.POWERED)) {
                voxelShape =VoxelShapes.union(voxelShape, SHAPEROTATED);
            }
        }

        if (blockState.get(Properties.FACING)==Direction.WEST) {
            voxelShape = MAINWEST;
            if (blockState.get(Properties.POWERED)) {
                voxelShape =VoxelShapes.union(voxelShape, SHAPEROTATED);
            }
        }

        return voxelShape;
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING, Properties.POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return PortalCubedBlocks.HLB_EMITTER_BLOCK.getDefaultState().with(Properties.FACING, ctx.getPlayerFacing().getOpposite()).with(Properties.POWERED, false);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.field_26659.get(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.field_26659.get(state);
    }


    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(PortalCubedBlocks.HLB_EMITTER_BLOCK)) {
            return stateFrom.get(Properties.POWERED);
        } else return stateFrom.isOf(PortalCubedBlocks.HLB_BLOCK);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HardLightBridgeEmitterBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.HLB_EMITTER_ENTITY, HardLightBridgeEmitterBlockEntity::tick);
    }

}