package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.util.CustomProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.function.Supplier;

public class FaithPlateBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty HORIFACING = CustomProperties.HORIFACING;

    private final Supplier<BlockEntityType<? extends FaithPlateBlockEntity>> blockEntityType;

    public FaithPlateBlock(Properties settings, Supplier<BlockEntityType<? extends FaithPlateBlockEntity>> blockEntityType) {
        super(settings);
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(HORIFACING, Direction.NORTH)
        );
        this.blockEntityType = blockEntityType;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide && canConfigure(player, hand)) {
            //This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity cast to
            //a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
            MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);

            if (screenHandlerFactory != null) {
                //With this call the server will request the client to open the appropriate ScreenHandler
                player.openMenu(screenHandlerFactory);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public boolean canConfigure(Player player, InteractionHand hand) {
        if (player.isCreative())
            return true;
        return player.getItemInHand(hand).is(PortalCubedItems.HAMMER);
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
        builder.add(FACING, HORIFACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction look = ctx.getNearestLookingDirection();
        if (look.getAxis().isVertical()) {
            return defaultBlockState()
                .setValue(FACING, look.getOpposite())
                .setValue(HORIFACING, ctx.getHorizontalDirection());
        }
        return defaultBlockState()
            .setValue(FACING, look.getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.getBlockEntity(pos, blockEntityType.get()).ifPresent(entity -> {
            entity.setVelY(1.25);
            final Direction facing = state.getValue(FACING).getAxis().isVertical() ? state.getValue(HORIFACING) : state.getValue(FACING);
            entity.setVelX(facing.getStepX() * 0.75);
            entity.setVelZ(facing.getStepZ() * 0.75);
        });
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == blockEntityType.get()
            ? (world1, pos, state1, entity) -> ((FaithPlateBlockEntity)entity).tick(world1, pos, state1)
            : null;
    }
}
