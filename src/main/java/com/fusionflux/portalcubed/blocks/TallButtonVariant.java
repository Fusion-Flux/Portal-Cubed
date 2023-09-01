package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class TallButtonVariant extends FaceAttachedHorizontalDirectionalBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty OFFSET = BooleanProperty.create("offset");

	private static final VoxelShape[] CEILING_X_SHAPE = createShapePair(5.5, -4, 5.5, 10.5, 16, 10.5);
	private static final VoxelShape[] CEILING_Z_SHAPE = createShapePair(5.5, -4, 5.5, 10.5, 16, 10.5);
	private static final VoxelShape[] FLOOR_X_SHAPE = createShapePair(5.5, 0, 5.5, 10.5, 20, 10.5);
	private static final VoxelShape[] FLOOR_Z_SHAPE = createShapePair(5.5, 0, 5.5, 10.5, 20, 10.5);
	private static final VoxelShape[] NORTH_SHAPE = createShapePair(5.5, 5.5, -4, 10.5, 10.5, 16);
	private static final VoxelShape[] SOUTH_SHAPE = createShapePair(5.5, 5.5, 0, 10.5, 10.5, 20);
	private static final VoxelShape[] WEST_SHAPE = createShapePair(-4, 5.5, 5.5, 16, 10.5, 10.5);
	private static final VoxelShape[] EAST_SHAPE = createShapePair(0, 5.5, 5.5, 20, 10.5, 10.5);

	protected static final Map<BlockState, VoxelShape> SHAPE_CACHE = new HashMap<>();

	protected TallButtonVariant(Properties settings) {
		super(settings);
		registerDefaultState(
			stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(POWERED, false)
				.setValue(FACE, AttachFace.WALL)
				.setValue(OFFSET, false)
		);
	}

	private int getPressTicks() {
		return 30;
	}

	private static VoxelShape[] createShapePair(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return new VoxelShape[] {
			box(minX, minY, minZ, maxX, maxY, maxZ),
			box(
				maybeShrink(minX), maybeShrink(minY), maybeShrink(minZ),
				maybeShrink(maxX), maybeShrink(maxY), maybeShrink(maxZ)
			)
		};
	}

	private static double maybeShrink(double v) {
		return v == 20 ? 18 : v == -4 ? -2 : v;
	}

	@NotNull
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		final VoxelShape[] baseShape = getBaseShape(state);
		VoxelShape shape = SHAPE_CACHE.get(state);
		if (shape == null) {
			if (state.getValue(OFFSET)) {
				final Vec3 offsetDir = Vec3.atLowerCornerOf(getOffsetDir(state).getNormal()).scale(0.25);
				shape = baseShape[1].move(offsetDir.x, offsetDir.y, offsetDir.z);
			} else {
				shape = baseShape[0];
			}
			SHAPE_CACHE.put(state, shape);
		}
		return shape;
	}

	private VoxelShape[] getBaseShape(BlockState state) {
		Direction direction = state.getValue(FACING);
		return switch (state.getValue(FACE)) {
			case FLOOR -> direction.getAxis() == Direction.Axis.X ? FLOOR_X_SHAPE : FLOOR_Z_SHAPE;
			case WALL -> switch (direction) {
				case EAST -> EAST_SHAPE;
				case WEST -> WEST_SHAPE;
				case SOUTH -> SOUTH_SHAPE;
				case NORTH -> NORTH_SHAPE;
				default -> throw new AssertionError();
			};
			case CEILING -> direction.getAxis() == Direction.Axis.X ? CEILING_X_SHAPE : CEILING_Z_SHAPE;
		};
	}

	@NotNull
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (player.getItemInHand(hand).is(PortalCubedItems.WRENCHES)) {
			world.setBlockAndUpdate(pos, state.cycle(OFFSET));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		if (state.getValue(POWERED)) {
			return InteractionResult.PASS;
		} else {
			this.powerOn(state, world, pos);
			this.playClickSound(player, world, pos, true);
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
	}

	public void powerOn(BlockState state, Level world, BlockPos pos) {
		world.setBlock(pos, state.setValue(POWERED, true), 3);
		this.updateNeighbors(state, world, pos);
		world.scheduleTick(pos, this, this.getPressTicks());
	}

	protected void playClickSound(@Nullable Player player, LevelAccessor world, BlockPos pos, boolean powered) {
		world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundSource.BLOCKS, 0.8f, 1f);
	}

	public abstract SoundEvent getClickSound(boolean powered);

	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.is(newState.getBlock())) {
			if (state.getValue(POWERED)) {
				this.updateNeighbors(state, world, pos);
			}

			super.onRemove(state, world, pos, newState, false);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.getValue(POWERED) && getConnectedDirection(state) == direction ? 15 : 0;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (state.getValue(POWERED)) {
			world.setBlock(pos, state.setValue(POWERED, false), 3);
			this.updateNeighbors(state, world, pos);
			this.playClickSound(null, world, pos, false);
		}
	}

	private void updateNeighbors(BlockState state, Level world, BlockPos pos) {
		world.updateNeighborsAt(pos, this);
		world.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, FACE, OFFSET);
	}

	public static Direction getOffsetDir(BlockState state) {
		if (state.getValue(FACE) == AttachFace.WALL) {
			return Direction.UP;
		}
		return state.getValue(FACING);
	}

}
