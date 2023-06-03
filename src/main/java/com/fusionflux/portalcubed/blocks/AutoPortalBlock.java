package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.AutoPortalBlockEntity;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPQuaternion;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AutoPortalBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<PortalType> TYPE = EnumProperty.create("type", PortalType.class);

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
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutoPortalBlockEntity(pos, state);
    }

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
        openOrClosePortal(world, pos, state.getValue(FACING), true);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        final boolean poweredNow = world.hasNeighborSignal(pos) ||
            world.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!defaultBlockState().is(block) && poweredNow != state.getValue(POWERED)) {
            if (poweredNow) {
                openOrClosePortal(world, state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), state.getValue(FACING), false);
            }
            world.setBlockAndUpdate(pos, state.setValue(POWERED, poweredNow));
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

    private static void openOrClosePortal(Level world, BlockPos lower, Direction facing, boolean forceClose) {
        if (world.isClientSide) return;
        openOrClosePortal(world, lower, facing, forceClose, true, true);
    }

    public static ExperimentalPortal openOrClosePortal(
        Level world, BlockPos lower, Direction facing, boolean forceClose, boolean playCloseSound, boolean spawnEntity
    ) {
        final int color = getColor(world, lower);
        final BlockPos upper = lower.above();
        final List<ExperimentalPortal> portals = world.getEntities(
            PortalCubedEntities.EXPERIMENTAL_PORTAL,
            AABB.of(BoundingBox.fromCorners(lower, upper)),
            p -> p.getColor() == color
        );
        if (!portals.isEmpty()) {
            portals.forEach(ExperimentalPortal::kill);
            if (playCloseSound) {
                world.playSound(null, lower.getX(), lower.getY(), lower.getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundSource.NEUTRAL, .1F, 1F);
            }
            return null;
        }
        if (forceClose) return null;
        final Direction facingOpposite = facing.getOpposite();
        final BlockPos placeOn = upper.relative(facingOpposite);
        final ExperimentalPortal portal = PortalCubedEntities.EXPERIMENTAL_PORTAL.create(world);
        assert portal != null;
        final Vec3 portalPos = new Vec3(
            placeOn.getX() + 0.5 - 0.510 * facingOpposite.getStepX(),
            placeOn.getY(),
            placeOn.getZ() + 0.5 - 0.510 * facingOpposite.getStepZ()
        );
        portal.setOriginPos(portalPos);
        portal.setDestination(Optional.of(portalPos));
        final Vec3i right = new Vec3i(0, 1, 0).cross(facingOpposite.getNormal());
        final Tuple<Double, Double> rotAngles = IPQuaternion.getPitchYawFromRotation(
            PortalGun.getPortalOrientationQuaternion(Vec3.atLowerCornerOf(right), new Vec3(0, 1, 0))
        );
        portal.setYRot(rotAngles.getA().floatValue());
        portal.setXRot(rotAngles.getB().floatValue());
        portal.setColor(color);
        portal.setOrientation(Vec3.atLowerCornerOf(right), new Vec3(0, -1, 0));
        portal.setLinkedPortalUUID(Optional.empty());
        if (spawnEntity) {
            world.addFreshEntity(portal);
            PortalGun.getPotentialOpposite(world, portalPos, portal, color, true)
                .ifPresent(other -> PortalGun.linkPortals(portal, other, 0.9f));
        }
        return portal;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final boolean upper = state.getValue(HALF) == DoubleBlockHalf.UPPER;
        final BlockPos otherPos = upper ? pos.below() : pos.above();
        final BlockPos lowerPos = upper ? otherPos : pos;
        final BlockState otherState = world.getBlockState(otherPos);
        final ItemStack stack = player.getItemInHand(hand);
        if (stack.is(PortalCubedItems.HAMMER)) {
            openOrClosePortal(world, lowerPos, state.getValue(FACING), true);
            if (player.isShiftKeyDown()) {
                for (BlockPos usePos = pos; usePos != null; usePos = usePos == pos ? otherPos : null) {
                    world.getBlockEntity(usePos, PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY)
                        .ifPresent(entity -> entity.setColor(0x1d86db));
                }
                player.displayClientMessage(
                    Component.translatable("portalcubed.auto_portal.set_portal_color.default")
                        .withStyle(s -> s.withColor(getColor(world, pos))),
                    true
                );
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
            final BlockState newState = state.cycle(TYPE);
            world.setBlockAndUpdate(pos, newState);
            world.setBlockAndUpdate(otherPos, otherState.cycle(TYPE));
            player.displayClientMessage(
                Component.translatable(
                    "portalcubed.auto_portal.set_portal_type",
                    Component.translatable("portalcubed.portal_type." + newState.getValue(TYPE).getSerializedName())
                ).withStyle(s -> s.withColor(getColor(world, pos))),
                true
            );
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        if (stack.getItem() instanceof DyeItem dye) {
            openOrClosePortal(world, lowerPos, state.getValue(FACING), true);
            final int dyeColor = PortalCubedItems.PORTAL_GUN.getColor(DyeableLeatherItem.dyeArmor(
                new ItemStack(PortalCubedItems.PORTAL_GUN), List.of(dye)
            ));
            for (BlockPos usePos = pos; usePos != null; usePos = usePos == pos ? otherPos : null) {
                world.getBlockEntity(usePos, PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY)
                    .ifPresent(entity -> entity.setColor(dyeColor));
            }
            player.displayClientMessage(
                Component.translatable(
                    "portalcubed.auto_portal.set_portal_color",
                    Component.translatable("color.minecraft." + dye.getDyeColor().getName())
                ).withStyle(s -> s.withColor(getColor(world, pos))),
                true
            );
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
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

    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, POWERED, TYPE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Component.translatable("portalcubed.auto_portal.tooltip").withStyle(ChatFormatting.GRAY));
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

        @Override
        public String getSerializedName() {
            return id;
        }
    }
}
