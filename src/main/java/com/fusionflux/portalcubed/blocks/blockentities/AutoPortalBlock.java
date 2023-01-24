package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.SlidingDoorBlock;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.util.IPQuaternion;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutoPortalBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty OPEN = Properties.OPEN;

    private static final VoxelShape PILLAR_SHAPE = createCuboidShape(0, 0, 0, 1, 15, 1);
    public static final VoxelShape TOP_SOUTH_SHAPE = VoxelShapes.union(PILLAR_SHAPE, PILLAR_SHAPE.offset(15 / 16.0, 0, 0));
    public static final VoxelShape TOP_NORTH_SHAPE = TOP_SOUTH_SHAPE.offset(0, 0, 15 / 16.0);
    public static final VoxelShape TOP_EAST_SHAPE = VoxelShapes.union(PILLAR_SHAPE, PILLAR_SHAPE.offset(0, 0, 15 / 16.0));
    public static final VoxelShape TOP_WEST_SHAPE = TOP_EAST_SHAPE.offset(15 / 16.0, 0, 0);
    public static final VoxelShape BOTTOM_NORTH_SHAPE = TOP_NORTH_SHAPE.offset(0, 1 / 16.0, 0);
    public static final VoxelShape BOTTOM_SOUTH_SHAPE = TOP_SOUTH_SHAPE.offset(0, 1 / 16.0, 0);
    public static final VoxelShape BOTTOM_WEST_SHAPE = TOP_WEST_SHAPE.offset(0, 1 / 16.0, 0);
    public static final VoxelShape BOTTOM_EAST_SHAPE = TOP_EAST_SHAPE.offset(0, 1 / 16.0, 0);

    public AutoPortalBlock(Settings settings) {
        super(settings);
        setDefaultState(
            getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(POWERED, false)
                .with(OPEN, false)
        );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoPortalBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        if (true) return VoxelShapes.fullCube();
        final boolean top = state.get(HALF) == DoubleBlockHalf.UPPER;
        return switch (state.get(FACING)) {
            default -> top ? TOP_NORTH_SHAPE : BOTTOM_NORTH_SHAPE; // and NORTH
            case SOUTH -> top ? TOP_SOUTH_SHAPE : BOTTOM_SOUTH_SHAPE;
            case WEST -> top ? TOP_WEST_SHAPE : BOTTOM_WEST_SHAPE;
            case EAST -> top ? TOP_EAST_SHAPE : BOTTOM_EAST_SHAPE;
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        final DoubleBlockHalf half = state.get(HALF);
        if (direction.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (direction == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        return neighborState.isOf(this) && neighborState.get(HALF) != half
            ? state.with(FACING, neighborState.get(FACING))
                .with(POWERED, neighborState.get(POWERED))
            : Blocks.AIR.getDefaultState();
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            SlidingDoorBlock.onBreakInCreative(world, pos, state, player);
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        final BlockPos pos = ctx.getBlockPos();
        final World world = ctx.getWorld();
        if (pos.getY() >= world.getTopY() - 1 || !world.getBlockState(pos.up()).canReplace(ctx)) {
            return null;
        }
        final boolean powered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        return getDefaultState()
            .with(FACING, ctx.getPlayerFacing().getOpposite())
            .with(HALF, DoubleBlockHalf.LOWER)
            .with(POWERED, powered)
            .with(OPEN, powered);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
        openOrClosePortal(world, pos, state.get(FACING), false);
        if (state.get(OPEN)) {
            openOrClosePortal(world, pos, state.get(FACING), true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        final boolean poweredNow = world.isReceivingRedstonePower(pos) ||
            world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!getDefaultState().isOf(block) && poweredNow != state.get(POWERED)) {
            BlockState newState = state.with(POWERED, poweredNow);
            if (poweredNow) {
                final boolean shouldBeOpen = !state.get(OPEN);
                openOrClosePortal(world, state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down(), state.get(FACING), shouldBeOpen);
                newState = newState.with(OPEN, shouldBeOpen);
            }
            world.setBlockState(pos, newState);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        final Direction facing = state.get(FACING);
        final BlockPos onPos = pos.offset(facing.getOpposite());
        if (!world.getBlockState(onPos).isSideSolidFullSquare(world, onPos, facing)) {
            return false;
        }
        return state.get(HALF) != DoubleBlockHalf.UPPER || world.getBlockState(pos.down()).isOf(this);
    }

    private static void openOrClosePortal(World world, BlockPos lower, Direction facing, boolean open) {
        if (world.isClient) return;
        final int color = 0x1d86db; // TODO: Replace with block entity usage
        final BlockPos upper = lower.up();
        if (open) {
            final Direction facingOpposite = facing.getOpposite();
            final BlockPos placeOn = upper.offset(facingOpposite);
            final ExperimentalPortal portal = PortalCubedEntities.EXPERIMENTAL_PORTAL.create(world);
            assert portal != null;
            final Vec3d portalPos = new Vec3d(
                placeOn.getX() + 0.5 - 0.510 * facingOpposite.getOffsetX(),
                placeOn.getY(),
                placeOn.getZ() + 0.5 - 0.510 * facingOpposite.getOffsetZ()
            );
            portal.setOriginPos(portalPos);
            CalledValues.setDestination(portal, portalPos);
            final Vec3i right = new Vec3i(0, 1, 0).crossProduct(facingOpposite.getVector());
            final Pair<Double, Double> rotAngles = IPQuaternion.getPitchYawFromRotation(
                PortalGun.getPortalOrientationQuaternion(Vec3d.of(right), new Vec3d(0, 1, 0))
            );
            portal.setYaw(rotAngles.getLeft().floatValue());
            portal.setPitch(rotAngles.getRight().floatValue());
            portal.setColor(color);
            CalledValues.setOrientation(portal, Vec3d.of(right), new Vec3d(0, -1, 0));
            portal.setLinkedPortalUuid("null");
            world.spawnEntity(portal);
            PortalGun.getPotentialOpposite(world, portalPos, portal, color, true)
                .ifPresent(other -> PortalGun.linkPortals(portal, other));
        } else {
            final List<ExperimentalPortal> portals = world.getEntitiesByType(
                PortalCubedEntities.EXPERIMENTAL_PORTAL,
                Box.from(BlockBox.create(lower, upper)),
                p -> p.getColor() == color
            );
            if (!portals.isEmpty()) {
                portals.forEach(ExperimentalPortal::kill);
            }
        }
    }

    private static int getComplementary(int color) {
        return 0xffffff - color + 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return mirror == BlockMirror.NONE ? state : state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, POWERED, OPEN);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
