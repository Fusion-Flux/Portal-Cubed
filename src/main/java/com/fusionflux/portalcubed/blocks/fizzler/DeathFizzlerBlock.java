package com.fusionflux.portalcubed.blocks.fizzler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DeathFizzlerBlock extends AbstractFizzlerBlock {
    public DeathFizzlerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityEnter(BlockState state, World world, BlockPos pos, Entity entity) {
        fizzlePhysicsEntity(entity);
        fizzleLiving(entity);
    }
}
