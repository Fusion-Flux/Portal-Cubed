package com.fusionflux.thinkingwithportatos.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CustomFluidBlock extends FluidBlock {

    protected CustomFluidBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
    }
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity, pos);
    }

    private void addCollisionEffects(World world, Entity entity, BlockPos pos) {
        entity.damage(DamageSource.WITHER,2);
    }
}
