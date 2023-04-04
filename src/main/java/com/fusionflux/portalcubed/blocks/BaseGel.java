package com.fusionflux.portalcubed.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockView;

public class BaseGel extends SimpleMultiSidedBlock {
    public BaseGel(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
        if (world.hasRain(pos.up())) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
}
