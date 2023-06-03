package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.FloorButtonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;


public class FloorButtonBlock extends BaseEntityBlock {
    public static final BooleanProperty ENABLE;
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    protected static final VoxelShape SHAPE_UP = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 18.0D, 15.0D);

    protected static final VoxelShape SHAPE_UP_2 = Block.box(-1.0D, 16.0D, -1.0D, 17.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPE_DOWN = Block.box(1.0D, -2.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPE_DOWN_2 = Block.box(-1.0D, -1.0D, -1.0D, 17.0D, 0.0D, 17.0D);


    protected static final VoxelShape SHAPE_NORTH = Block.box(1.0D, 1.0D, -2.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPE_NORTH_2 = Block.box(-1.0D, -1.0D, -1.0D, 17.0D, 17.0D, 0.0D);


    protected static final VoxelShape SHAPE_SOUTH = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 18.0D);

    protected static final VoxelShape SHAPE_SOUTH_2 = Block.box(-1.0D, -1.0D, 16.0D, 17.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPE_WEST = Block.box(-2.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPE_WEST_2 = Block.box(-1.0D, -1.0D, -1.0D, 0.0D, 17.0D, 17.0D);


    protected static final VoxelShape SHAPE_EAST = Block.box(0.0D, 1.0D, 1.0D, 18.0D, 15.0D, 15.0D);

    protected static final VoxelShape SHAPE_EAST_2 = Block.box(16.0D, -1.0D, -1.0D, 17.0D, 17.0D, 17.0D);

    public static boolean enableEasterEgg = false;


    public FloorButtonBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(ENABLE, false));
    }

    static {
        ENABLE = BlockStateProperties.ENABLED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return state.getValue(BlockStateProperties.ENABLED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        if (state.getValue(BlockStateProperties.ENABLED)) {
            return 15;
        }
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);

        VoxelShape voxelShape = Shapes.empty();

        if (facing == Direction.UP)
            voxelShape = Shapes.or(voxelShape, SHAPE_UP);
        if (facing == Direction.DOWN)
            voxelShape = Shapes.or(voxelShape, SHAPE_DOWN);
        if (facing == Direction.NORTH)
            voxelShape = Shapes.or(voxelShape, SHAPE_NORTH);
        if (facing == Direction.SOUTH)
            voxelShape = Shapes.or(voxelShape, SHAPE_SOUTH);
        if (facing == Direction.EAST)
            voxelShape = Shapes.or(voxelShape, SHAPE_EAST);
        if (facing == Direction.WEST)
            voxelShape = Shapes.or(voxelShape, SHAPE_WEST);

        voxelShape = Shapes.or(voxelShape, SHAPE);
        return voxelShape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);

        VoxelShape voxelShape = Shapes.empty();

        if (facing == Direction.UP) {
            voxelShape = Shapes.or(voxelShape, SHAPE_UP);
            voxelShape = Shapes.or(voxelShape, SHAPE_UP_2);
        }
        if (facing == Direction.DOWN) {
            voxelShape = Shapes.or(voxelShape, SHAPE_DOWN);
            voxelShape = Shapes.or(voxelShape, SHAPE_DOWN_2);
        }
        if (facing == Direction.NORTH) {
            voxelShape = Shapes.or(voxelShape, SHAPE_NORTH);
            voxelShape = Shapes.or(voxelShape, SHAPE_NORTH_2);
        }
        if (facing == Direction.SOUTH) {
            voxelShape = Shapes.or(voxelShape, SHAPE_SOUTH);
            voxelShape = Shapes.or(voxelShape, SHAPE_SOUTH_2);
        }
        if (facing == Direction.EAST) {
            voxelShape = Shapes.or(voxelShape, SHAPE_EAST);
            voxelShape = Shapes.or(voxelShape, SHAPE_EAST_2);
        }
        if (facing == Direction.WEST) {
            voxelShape = Shapes.or(voxelShape, SHAPE_WEST);
            voxelShape = Shapes.or(voxelShape, SHAPE_WEST_2);
        }

        voxelShape = Shapes.or(voxelShape, SHAPE);

        return voxelShape;
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.ENABLED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PortalCubedBlocks.FLOOR_BUTTON.defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.FACING, rotation.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FloorButtonBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PortalCubedBlocks.FLOOR_BUTTON_BLOCK_ENTITY, FloorButtonBlockEntity::tick1);
    }

    @Override
    public String getDescriptionId() {
        return enableEasterEgg ? "block.portalcubed.floor_button.easter_egg" : "block.portalcubed.floor_button";
    }
}
