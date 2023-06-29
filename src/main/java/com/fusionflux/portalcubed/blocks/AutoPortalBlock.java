package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.AutoPortalBlockEntity;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPQuaternion;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AutoPortalBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<PortalType> TYPE = EnumProperty.create("type", PortalType.class);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private static final VoxelShape PILLAR_SHAPE_NS = box(-2, 0, 0, 1, 16, 2);
    private static final VoxelShape PILLAR_SHAPE_EW = box(0, 0, -2, 2, 16, 1);
    public static final VoxelShape SOUTH_SHAPE = Shapes.or(PILLAR_SHAPE_NS, PILLAR_SHAPE_NS.move(17 / 16.0, 0, 0));
    public static final VoxelShape NORTH_SHAPE = SOUTH_SHAPE.move(0, 0, 14 / 16.0);
    public static final VoxelShape EAST_SHAPE = Shapes.or(PILLAR_SHAPE_EW, PILLAR_SHAPE_EW.move(0, 0, 17 / 16.0));
    public static final VoxelShape WEST_SHAPE = EAST_SHAPE.move(14 / 16.0, 0, 0);

    public AutoPortalBlock(Properties settings) {
        super(settings);
        registerDefaultState(
            getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(POWERED, false)
                .setValue(OPEN, false)
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutoPortalBlockEntity(pos, state);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> throw new AssertionError();
        };
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        final DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (direction == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
        }
        return neighborState.is(this) && neighborState.getValue(HALF) != half
            ? state.setValue(FACING, neighborState.getValue(FACING))
                .setValue(POWERED, neighborState.getValue(POWERED))
            : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide && player.isCreative()) {
            SlidingDoorBlock.onBreakInCreative(world, pos, state, player);
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockPos pos = ctx.getClickedPos();
        final Level world = ctx.getLevel();
        if (pos.getY() >= world.getMaxBuildHeight() - 1 || !world.getBlockState(pos.above()).canBeReplaced(ctx)) {
            return null;
        }
        final boolean powered = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
        return defaultBlockState()
            .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
            .setValue(HALF, DoubleBlockHalf.LOWER)
            .setValue(POWERED, powered);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
        openOrClosePortal(world, pos, state, true);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        final boolean poweredNow = level.hasNeighborSignal(pos) ||
            level.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!defaultBlockState().is(block) && poweredNow != state.getValue(POWERED)) {
            if (poweredNow) {
                openOrClosePortal(level, state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), state, false);
            }
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(POWERED, poweredNow));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        final Direction facing = state.getValue(FACING);
        final BlockPos onPos = pos.relative(facing.getOpposite());
        if (!world.getBlockState(onPos).isFaceSturdy(world, onPos, facing)) {
            return false;
        }
        final BlockPos otherPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
        final BlockPos otherOnPos = otherPos.relative(facing.getOpposite());
        if (!world.getBlockState(otherOnPos).isFaceSturdy(world, otherOnPos, facing)) {
            return false;
        }
        return state.getValue(HALF) == DoubleBlockHalf.LOWER || world.getBlockState(otherPos).is(this);
    }

    public static void openOrClosePortal(Level level, BlockPos lower, BlockState state, boolean forceClose) {
        if (level.isClientSide) return;
        final boolean open = state.getValue(OPEN);
        final int color = getColor(level, lower);
        final BlockPos upper = lower.above();
        final List<Portal> portals = level.getEntities(
            PortalCubedEntities.PORTAL,
            state.getCollisionShape(level, lower).bounds().move(lower).expandTowards(0, 1, 0),
            p -> true
        );
        if (!portals.isEmpty()) {
            portals.forEach(Portal::kill);
            level.playSound(null, lower.getX(), lower.getY(), lower.getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundSource.NEUTRAL, .1F, 1F);
        }
        if (forceClose) return;
        level.setBlockAndUpdate(lower, level.getBlockState(lower).setValue(OPEN, !open));
        level.setBlockAndUpdate(upper, level.getBlockState(upper).setValue(OPEN, !open));
        if (open) {
            return;
        }
        final Direction facing = state.getValue(FACING);
        final Direction facingOpposite = facing.getOpposite();
        final BlockPos placeOn = upper.relative(facingOpposite);
        final Portal portal = PortalCubedEntities.PORTAL.create(level);
        assert portal != null;
        final Vec3 portalPos = new Vec3(
            placeOn.getX() + 0.5 - 0.510 * facingOpposite.getStepX(),
            placeOn.getY(),
            placeOn.getZ() + 0.5 - 0.510 * facingOpposite.getStepZ()
        );
        portal.setOriginPos(portalPos);
        portal.setDestination(Optional.of(portalPos));
        portal.setColor(color);
        portal.setRotation(IPQuaternion.matrixToQuaternion(
            Vec3.atLowerCornerOf(Direction.UP.getNormal().cross(facingOpposite.getNormal())),
            Vec3.atLowerCornerOf(Direction.UP.getNormal()),
            Vec3.atLowerCornerOf(facingOpposite.getNormal())
        ).toQuaternionf());
        portal.setLinkedPortalUUID(Optional.empty());
        level.addFreshEntity(portal);
        PortalGun.getPotentialOpposite(level, portalPos, portal, color, true)
            .ifPresent(other -> PortalGun.linkPortals(portal, other, 0.9f));
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final boolean upper = state.getValue(HALF) == DoubleBlockHalf.UPPER;
        final BlockPos otherPos = upper ? pos.below() : pos.above();
        final BlockPos lowerPos = upper ? otherPos : pos;
        final ItemStack stack = player.getItemInHand(hand);
        if (stack.is(PortalCubedItems.WRENCHES)) {
            openOrClosePortal(level, lowerPos, state, true);
            if (player.isShiftKeyDown()) {
                for (BlockPos usePos = pos; usePos != null; usePos = usePos == pos ? otherPos : null) {
                    level.getBlockEntity(usePos, PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY)
                        .ifPresent(entity -> entity.setColor(0x1d86db));
                }
                player.displayClientMessage(
                    Component.translatable("portalcubed.auto_portal.set_portal_color.default")
                        .withStyle(s -> s.withColor(getColor(level, pos))),
                    true
                );
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            final PortalType newType = state.cycle(TYPE).getValue(TYPE);
            level.setBlockAndUpdate(pos, level.getBlockState(pos).cycle(TYPE));
            level.setBlockAndUpdate(otherPos, level.getBlockState(otherPos).cycle(TYPE));
            player.displayClientMessage(
                Component.translatable(
                    "portalcubed.auto_portal.set_portal_type",
                    Component.translatable("portalcubed.portal_type." + newType.getSerializedName())
                ).withStyle(s -> s.withColor(getColor(level, pos))),
                true
            );
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.getItem() instanceof DyeItem dye) {
            openOrClosePortal(level, lowerPos, state, true);
            final int dyeColor = PortalCubedItems.PORTAL_GUN.getColor(DyeableLeatherItem.dyeArmor(
                new ItemStack(PortalCubedItems.PORTAL_GUN), List.of(dye)
            ));
            for (BlockPos usePos = pos; usePos != null; usePos = usePos == pos ? otherPos : null) {
                level.getBlockEntity(usePos, PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY)
                    .ifPresent(entity -> entity.setColor(dyeColor));
            }
            player.displayClientMessage(
                Component.translatable(
                    "portalcubed.auto_portal.set_portal_color",
                    Component.translatable("color.minecraft." + dye.getDyeColor().getName())
                ).withStyle(s -> s.withColor(getColor(level, pos))),
                true
            );
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static int getColor(Level world, BlockPos pos) {
        return world.getBlockState(pos).getValue(TYPE).colorTransformer.applyAsInt(
            world.getBlockEntity(pos, PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY)
                .map(AutoPortalBlockEntity::getColor)
                .orElse(0x1d86db)
        );
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, POWERED, TYPE, OPEN);
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public enum PortalType implements StringRepresentable {
        PRIMARY("primary", Int2IntFunction.identity()),
        SECONDARY("secondary", c -> 0xffffff - c + 1);

        private final String id;
        public final Int2IntFunction colorTransformer;

        PortalType(String id, Int2IntFunction colorTransformer) {
            this.id = id;
            this.colorTransformer = colorTransformer;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return id;
        }
    }
}
