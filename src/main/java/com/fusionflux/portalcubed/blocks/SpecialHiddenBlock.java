package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.properties.FluidTypeProperty;
import com.fusionflux.portalcubed.blocks.properties.PortalCubedProperties;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.fabricmc.api.EnvType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

public abstract class SpecialHiddenBlock extends Block implements SimpleLoggedBlock {
    public static final FluidTypeProperty LOGGING = PortalCubedProperties.LOGGING;

    public SpecialHiddenBlock(Properties settings) {
        super(settings);
        registerDefaultState(
            getStateDefinition().any()
                .setValue(LOGGING, FluidTypeProperty.getEmpty())
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOGGING);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public final VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return isHolding(context, asItem()) || isHolding(context, PortalCubedItems.HAMMER)
            ? getVisibleOutlineShape(state, world, pos, context) : Shapes.empty();
    }

    protected VoxelShape getVisibleOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    private static boolean isHolding(CollisionContext context, Item item) {
        return context.isHoldingItem(item) ||
            (context instanceof EntityCollisionContext entityContext &&
                entityContext.getEntity() instanceof LivingEntity living &&
                living.isHolding(item)
            );
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state) {
        return MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT
            ? overrideRenderTypeClient() : RenderShape.INVISIBLE;
    }

    @ClientOnly
    private RenderShape overrideRenderTypeClient() {
        return PortalCubedClient.hiddenBlocksVisible() ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1f;
    }

    @NotNull
    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        final Fluid fluid = LOGGING.getFluid(state);
        if (fluid != Fluids.EMPTY) {
            world.scheduleTick(pos, fluid, fluid.getTickDelay(world));
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        final Fluid fluid = LOGGING.getFluid(state);
        return fluid instanceof FlowingFluid flowing ? flowing.getSource(false) : fluid.defaultFluidState();
    }
}
