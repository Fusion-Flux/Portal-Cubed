package com.fusionflux.thinkingwithportatos.fluids;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public class ToxicGooFluid extends CustomFluid{
    @Override
    public Fluid getStill() {
        return ThinkingWithPortatosBlocks.STILL_TOXIC_GOO;
    }

    @Override
    public Fluid getFlowing() {
        return ThinkingWithPortatosBlocks.FLOWING_TOXIC_GOO;
    }

    @Override
    public Item getBucketItem() {
        return ThinkingWithPortatosBlocks.TOXIC_GOO_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        // method_15741 converts the LEVEL_1_8 of the fluid state to the LEVEL_15 the fluid block uses
        return ThinkingWithPortatosBlocks.TOXIC_GOO.getDefaultState().with(Properties.LEVEL_15, method_15741(fluidState));
    }

    public static class Flowing extends ToxicGooFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends ToxicGooFluid {
        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }



}
