package com.fusionflux.portalcubed.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface BlockCollisionTrigger {
    default void onEntityEnter(BlockState state, World world, BlockPos pos, Entity entity) {
    }

    default void onEntityLeave(BlockState state, World world, BlockPos pos, Entity entity) {
    }

    VoxelShape getTriggerShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
}
