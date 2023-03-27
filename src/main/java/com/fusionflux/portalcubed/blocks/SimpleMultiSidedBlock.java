package com.fusionflux.portalcubed.blocks;

import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.LichenSpreadBehavior;

public class SimpleMultiSidedBlock extends AbstractLichenBlock {
    protected static final VoxelShape NO_COLLISION = VoxelShapes.empty();
    
    public SimpleMultiSidedBlock(Settings settings) {
        super(settings);
    }
    
    @Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return NO_COLLISION;
	}

    @Override
    public LichenSpreadBehavior getLichenSpreadBehavior() {
        return new LichenSpreadBehavior(this);
    }
}
