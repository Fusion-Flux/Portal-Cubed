package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AbsoluteFizzlerBlock extends AbstractFizzlerBlock implements BlockCollisionTrigger {
    public AbsoluteFizzlerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityEnter(BlockState state, World world, BlockPos pos, Entity entity) {
        removePortals(entity);
        maybeFizzleEntity(entity);
    }
}
