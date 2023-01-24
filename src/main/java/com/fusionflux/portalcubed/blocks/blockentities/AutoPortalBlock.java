package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.SlidingDoorBlock;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPQuaternion;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
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
    public static final EnumProperty<PortalType> TYPE = EnumProperty.of("type", PortalType.class);

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
            .with(POWERED, powered);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
        openOrClosePortal(world, pos, state.get(FACING), true);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        final boolean poweredNow = world.isReceivingRedstonePower(pos) ||
            world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!getDefaultState().isOf(block) && poweredNow != state.get(POWERED)) {
            if (poweredNow) {
                openOrClosePortal(world, state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down(), state.get(FACING), false);
            }
            world.setBlockState(pos, state.with(POWERED, poweredNow));
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

    private static void openOrClosePortal(World world, BlockPos lower, Direction facing, boolean forceClose) {
        if (world.isClient) return;
        final int color = getColor(world, lower);
        final BlockPos upper = lower.up();
        final List<ExperimentalPortal> portals = world.getEntitiesByType(
            PortalCubedEntities.EXPERIMENTAL_PORTAL,
            Box.from(BlockBox.create(lower, upper)),
            p -> p.getColor() == color
        );
        if (!portals.isEmpty()) {
            portals.forEach(ExperimentalPortal::kill);
            world.playSound(null, lower.getX(), lower.getY(), lower.getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
            return;
        }
        if (forceClose) return;
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
            .ifPresent(other -> PortalGun.linkPortals(portal, other, 0.9f));
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final boolean upper = state.get(HALF) == DoubleBlockHalf.UPPER;
        final BlockPos otherPos = upper ? pos.down() : pos.up();
        final BlockPos lowerPos = upper ? otherPos : pos;
        final BlockState otherState = world.getBlockState(otherPos);
        final ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            openOrClosePortal(world, lowerPos, state.get(FACING), true);
            if (player.isSneaking()) {
                for (BlockPos usePos = pos; usePos != null; usePos = usePos == pos ? otherPos : null) {
                    world.getBlockEntity(usePos, PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY)
                        .ifPresent(entity -> entity.setColor(0x1d86db));
                }
                player.sendMessage(Text.translatable("portalcubed.auto_portal.set_portal_color.default"), true);
                return ActionResult.success(world.isClient);
            }
            final BlockState newState = state.cycle(TYPE);
            world.setBlockState(pos, newState);
            world.setBlockState(otherPos, otherState.cycle(TYPE));
            player.sendMessage(
                Text.translatable(
                    "portalcubed.auto_portal.set_portal_type",
                    Text.translatable("portalcubed.portal_type." + newState.get(TYPE).asString())
                ).styled(s -> s.withColor(getColor(world, pos))),
                true
            );
            return ActionResult.success(world.isClient);
        }
        if (stack.getItem() instanceof DyeItem dye) {
            openOrClosePortal(world, lowerPos, state.get(FACING), true);
            final int dyeColor = PortalCubedItems.PORTAL_GUN.getColor(DyeableItem.blendAndSetColor(
                new ItemStack(PortalCubedItems.PORTAL_GUN), List.of(dye)
            ));
            for (BlockPos usePos = pos; usePos != null; usePos = usePos == pos ? otherPos : null) {
                world.getBlockEntity(usePos, PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY)
                    .ifPresent(entity -> entity.setColor(dyeColor));
            }
            player.sendMessage(
                Text.translatable(
                    "portalcubed.auto_portal.set_portal_color",
                    Text.translatable("color.minecraft." + dye.getColor().getName())
                ).styled(s -> s.withColor(getColor(world, pos))),
                true
            );
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    public static int getColor(World world, BlockPos pos) {
        return world.getBlockState(pos).get(TYPE).colorTransformer.applyAsInt(
            world.getBlockEntity(pos, PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY)
                .map(AutoPortalBlockEntity::getColor)
                .orElse(0x1d86db)
        );
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
        builder.add(FACING, HALF, POWERED, TYPE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public enum PortalType implements StringIdentifiable {
        PRIMARY("primary", Int2IntFunction.identity()),
        SECONDARY("secondary", c -> 0xffffff - c + 1);

        private final String id;
        public final Int2IntFunction colorTransformer;

        PortalType(String id, Int2IntFunction colorTransformer) {
            this.id = id;
            this.colorTransformer = colorTransformer;
        }

        @Override
        public String asString() {
            return id;
        }
    }
}
