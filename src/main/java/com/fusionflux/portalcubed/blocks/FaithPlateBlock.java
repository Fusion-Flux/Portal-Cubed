package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.util.CustomProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.function.Supplier;

public class FaithPlateBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final DirectionProperty HORIFACING = CustomProperties.HORIFACING;

    private final Supplier<BlockEntityType<? extends FaithPlateBlockEntity>> blockEntityType;

    public FaithPlateBlock(Settings settings, Supplier<BlockEntityType<? extends FaithPlateBlockEntity>> blockEntityType) {
        super(settings);
        this.setDefaultState(
            stateManager.getDefaultState()
                .with(FACING, Direction.UP)
                .with(HORIFACING, Direction.NORTH)
        );
        this.blockEntityType = blockEntityType;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            //This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity cast to
            //a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                //With this call the server will request the client to open the appropriate ScreenHandler
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
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
        builder.add(FACING, HORIFACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        final Direction look = ctx.getPlayerLookDirection();
        if (look.getAxis().isVertical()) {
            return getDefaultState()
                .with(FACING, look.getOpposite())
                .with(HORIFACING, ctx.getPlayerFacing());
        }
        return getDefaultState()
            .with(FACING, look.getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.getBlockEntity(pos, blockEntityType.get()).ifPresent(entity -> {
            entity.setVelY(1.25);
            final Direction facing = state.get(FACING).getAxis().isVertical() ? state.get(HORIFACING) : state.get(FACING);
            entity.setVelX(facing.getOffsetX() * 0.75);
            entity.setVelZ(facing.getOffsetZ() * 0.75);
        });
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().instantiate(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return type == blockEntityType.get()
            ? (world1, pos, state1, entity) -> ((FaithPlateBlockEntity)entity).tick(world1, pos, state1)
            : null;
    }
}
