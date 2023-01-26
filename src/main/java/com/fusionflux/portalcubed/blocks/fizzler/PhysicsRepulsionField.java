package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class PhysicsRepulsionField extends AbstractFizzlerBlock {
    public PhysicsRepulsionField(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return context instanceof EntityShapeContext entityCtx && entityCtx.getEntity() instanceof CorePhysicsEntity
            ? getOutlineShape(state, world, pos, context)
            : super.getCollisionShape(state, world, pos, context);
    }
}
