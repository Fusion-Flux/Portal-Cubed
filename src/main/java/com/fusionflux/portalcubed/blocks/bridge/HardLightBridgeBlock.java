package com.fusionflux.portalcubed.blocks.bridge;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import com.fusionflux.portalcubed.util.VoxelShaper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HardLightBridgeBlock extends Block implements HardLightBridgePart {
	public static final VoxelShape BASE_SHAPE_DOWN = Block.box(2, 1, 0, 14, 2, 16);
	public static final VoxelShape BASE_SHAPE_RIGHT = Block.box(1, 2, 0, 2, 14, 16);
	public static final VoxelShape BASE_SHAPE_UP = Block.box(2, 14, 0, 14, 15, 16);
	public static final VoxelShape BASE_SHAPE_LEFT = Block.box(14, 2, 0, 15, 14, 16);

	public static final Map<Edge, VoxelShaper> SHAPERS = Util.make(new HashMap<>(), map -> {
		map.put(Edge.DOWN, VoxelShaper.forDirectional(BASE_SHAPE_DOWN, Direction.SOUTH));
		map.put(Edge.RIGHT, VoxelShaper.forDirectional(BASE_SHAPE_RIGHT, Direction.SOUTH));
		map.put(Edge.UP, VoxelShaper.forDirectional(BASE_SHAPE_UP, Direction.SOUTH));
		map.put(Edge.LEFT, VoxelShaper.forDirectional(BASE_SHAPE_LEFT, Direction.SOUTH));
	});

	private static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

	public HardLightBridgeBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(
				stateDefinition.any()
						.setValue(EDGE, Edge.DOWN)
						.setValue(FACING, Direction.SOUTH)
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(EDGE, FACING);
	}

	@SuppressWarnings("deprecation")
	@Override
	@NotNull
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPES.computeIfAbsent(state, HardLightBridgeBlock::makeShape);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		boolean supported = hasSupportingBlockBehind(level, pos, state.getValue(FACING), state.getValue(EDGE), (behind, facing, edge) -> {
			if (behind.is(this) || behind.is(PortalCubedBlocks.HLB_EMITTER_BLOCK)) {
				return behind.getValue(FACING) == facing && behind.getValue(EDGE) == edge;
			}
			return false;
		});
		if (!supported)
			level.destroyBlock(pos, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (level instanceof ServerLevel serverLevel && !HardLightBridgeEmitterBlock.suppressUpdates)
			HardLightBridgeEmitterBlock.updateEmission(serverLevel, state, pos, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!(level instanceof ServerLevel serverLevel) || HardLightBridgeEmitterBlock.suppressUpdates)
			return;
		Direction facing = state.getValue(FACING);
		if (!pos.relative(facing).equals(fromPos))
			return; // only care about pos in front
		BlockState newState = level.getBlockState(fromPos);
		if (!newState.isAir()) // removed, try to extend
			return;
		HardLightBridgeEmitterBlock.updateEmission(serverLevel, state, pos, true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
		if (stateFrom.is(this) || stateFrom.is(PortalCubedBlocks.HLB_EMITTER_BLOCK)) {
			return stateFrom.getValue(FACING) == state.getValue(FACING);
		}
		return false;
	}

	static VoxelShape makeShape(BlockState state) {
		Direction facing = state.getValue(FACING);
		Edge edge = state.getValue(EDGE);
		if (facing.getAxis().isVertical())
			edge = edge.getOpposite();
		return SHAPERS.get(edge).get(facing);
	}

	@Override
	public void onPortalCreate(ServerLevel level, BlockState state, BlockPos pos, Portal portal) {
		Direction facing = state.getValue(FACING);
		if (facing != portal.getFacingDirection().getOpposite())
			return;
		// propagate through
		HardLightBridgeEmitterBlock.updateEmission(level, state, pos, true);
	}

	@Override
	public void beforePortalRemove(ServerLevel level, BlockState state, BlockPos pos, Portal portal) {
		Direction facing = state.getValue(FACING);
		if (facing == portal.getFacingDirection()) { // portal is behind, remove bridge since support is gone
			BlockPos start = pos.relative(facing.getOpposite());
			HardLightBridgeEmitterBlock.updateEmission(level, state, start, false);
		} else if (facing == portal.getFacingDirection().getOpposite()) { // portal in front, remove bridge on other side.
			HardLightBridgeEmitterBlock.updateEmission(level, state, pos, false);
		}
		// else: don't care
	}

	public static boolean hasSupportingBlockBehind(Level level, BlockPos pos, Direction facing, Edge edge, SupportTest test) {
		Direction back = facing.getOpposite();
		BlockState behind = level.getBlockState(pos.relative(back));
		if (test.test(behind, facing, edge))
			return true;
		// check through portals.
		for (Portal portal : level.getEntitiesOfClass(Portal.class, new AABB(pos), Portal::isGridAligned)) {
			Direction portalFacing = portal.getFacingDirection();
			if (portalFacing != facing)
				continue;
			Portal linked = portal.findLinkedPortal();
			if (linked == null)
				continue;
			Direction linkedFacing = linked.getFacingDirection();
			Vec3 portalToPos = Vec3.atCenterOf(pos).subtract(portal.getOriginPos())
					.with(portalFacing.getAxis(), 0); // fixme: this override is needed because the rotated offset is negated. why???
			Vec3 linkedToOtherPos = PortalDirectionUtils.rotateVector(portal, portalToPos)
					.with(linkedFacing.getAxis(), linkedFacing.getAxisDirection().getStep() / 2f);
			BlockPos otherSide = BlockPos.containing(linked.getOriginPos().add(linkedToOtherPos));
			BlockState otherSideState = level.getBlockState(otherSide);
			Direction otherSideFacing = linkedFacing.getOpposite();
			Edge otherSideEdge = Edge.teleport(edge, portal, facing, otherSideFacing);
			if (test.test(otherSideState, otherSideFacing, otherSideEdge))
				return true;
		}
		return false;
	}

	public interface SupportTest {
		boolean test(BlockState behind, Direction facing, Edge edge);
	}
}
