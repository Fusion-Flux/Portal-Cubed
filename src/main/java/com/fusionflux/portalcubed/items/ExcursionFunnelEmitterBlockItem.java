package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock;
import com.fusionflux.portalcubed.util.TwoByTwo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class ExcursionFunnelEmitterBlockItem extends BlockItem implements MultiblockItem {
    public static final Collection<Property<?>> REQUIRED_PROPERTIES = List.of(
            ExcursionFunnelEmitterBlock.QUADRANT, BlockStateProperties.FACING
    );

    public ExcursionFunnelEmitterBlockItem(Block block, Properties properties) {
        super(block, properties);
        if (!block.getStateDefinition().getProperties().containsAll(REQUIRED_PROPERTIES))
            throw new IllegalArgumentException("Cannot create an ExcursionFunnelEmitterBlockItem for a block without the required properties");
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        Direction playerFacing = context.getHorizontalDirection();
        Level level = context.getLevel();
        TwoByTwo placement = findValidPlacement(level, state, context.getClickedPos(), playerFacing);
        if (placement == null)
            return false;
        // placing is handled with facing as player facing, but facing is actually the direction the funnel faces
        Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();
        BlockState baseToPlace = state.setValue(BlockStateProperties.FACING, facing);
        // for each quadrant
        for (int i = 1; i <= 4; i++) {
            BlockPos pos = placement.byQuadrant(i);
            BlockState toPlace = baseToPlace.setValue(ExcursionFunnelEmitterBlock.QUADRANT, i);
            level.setBlock(pos, toPlace, 11);
        }
        // trigger an update, check for power
        level.blockUpdated(placement.byQuadrant(1), Blocks.AIR);
        return true;
    }

    @Nullable
    @Override
    public TwoByTwo findValidPlacement(Level level, BlockState state, BlockPos initial, Direction playerFacing) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        TwoByTwo bases = findBasePlacements(initial, facing, playerFacing);
        Direction left = getLeftOf(facing, Direction.SOUTH);
        Direction up = getDownOf(facing, Direction.SOUTH).getOpposite();
        basesLoop: for (BlockPos base : bases.quadrants(2, 1, 3, 4)) {
            TwoByTwo toPlace = TwoByTwo.fromBottomRightCorner(base, left, up);
            for (BlockPos pos : toPlace) {
                if (!canPlaceAt(level, pos))
                    continue basesLoop;
            }
            return toPlace;
        }
        return null;
    }

    protected TwoByTwo findBasePlacements(BlockPos initial, Direction facing, Direction playerFacing) {
        Direction right = getLeftOf(facing, playerFacing).getOpposite();
        Direction down = getDownOf(facing, playerFacing);
        return TwoByTwo.fromTopLeftCorner(initial, right, down);
    }

    protected boolean canPlaceAt(Level level, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced();
    }

    public static Direction getLeftOf(Direction facing, Direction perspectiveFacing) {
        if (facing.getAxis().isHorizontal())
            return facing.getCounterClockWise();
        return perspectiveFacing.getCounterClockWise();
    }

    public static Direction getDownOf(Direction facing, Direction perspectiveFacing) {
        if (facing.getAxis().isHorizontal())
            return Direction.DOWN;
        return facing == Direction.DOWN ? perspectiveFacing.getOpposite() : perspectiveFacing;
    }
}
