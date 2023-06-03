package com.fusionflux.portalcubed.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockCollisionTrigger {
    default void onEntityEnter(BlockState state, Level world, BlockPos pos, Entity entity) {
    }

    default void onEntityLeave(BlockState state, Level world, BlockPos pos, Entity entity) {
    }

    VoxelShape getTriggerShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);
}
