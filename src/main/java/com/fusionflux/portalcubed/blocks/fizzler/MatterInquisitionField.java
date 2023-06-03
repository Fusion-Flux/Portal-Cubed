package com.fusionflux.portalcubed.blocks.fizzler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MatterInquisitionField extends AbstractFizzlerBlock {
    public MatterInquisitionField(Properties settings) {
        super(settings);
    }

    @Override
    public void onEntityEnter(BlockState state, Level world, BlockPos pos, Entity entity) {
        fizzlePhysicsEntity(entity);
    }
}
