package com.fusionflux.portalcubed.fluids;

import com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class ToxicGooFluid extends FlowableFluid {
    @Override
    public Fluid getStill() {
        return PortalCubedFluids.TOXIC_GOO.still;
    }

    @Override
    public Fluid getFlowing() {
        return PortalCubedFluids.TOXIC_GOO.flowing;
    }

    @Override
    public Item getBucketItem() {
        return PortalCubedFluids.TOXIC_GOO.bucket;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return PortalCubedFluids.TOXIC_GOO.getBlock().getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    protected boolean isInfinite() {
        return true;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        Block.dropStacks(state, world, pos, state.hasBlockEntity() ? world.getBlockEntity(pos) : null);
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid,
            Direction direction) {
        return false;
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return 4;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 2;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 20;
    }

    @Override
    protected float getBlastResistance() {
        return 100;
    }

    public static class Flowing extends ToxicGooFluid {
        @Override
        protected void appendProperties(Builder<Fluid, FluidState> builder) {
            builder.add(FALLING, LEVEL);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Still extends ToxicGooFluid {
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Block extends FluidBlock {
        public Block(FlowableFluid flowableFluid, Settings settings) {
            super(flowableFluid, settings);
        }

        @Override
        public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
            if (!entity.isAlive()) return;
            entity.damage(PortalCubedDamageSources.ACID, world.getRandom().range(7, 10));
        }
    }
}
