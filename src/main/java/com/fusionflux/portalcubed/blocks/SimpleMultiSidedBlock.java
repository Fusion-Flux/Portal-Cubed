package com.fusionflux.portalcubed.blocks;

import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LichenSpreadBehavior;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SimpleMultiSidedBlock extends AbstractLichenBlock {
    public SimpleMultiSidedBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
         return VoxelShapes.empty();
    }

    @Override
    public LichenSpreadBehavior getLichenSpreadBehavior() {
        return new LichenSpreadBehavior(this);
    }
}
