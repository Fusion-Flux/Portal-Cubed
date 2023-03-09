package com.fusionflux.portalcubed.blocks;

import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.LichenSpreadBehavior;

public class SimpleMultiSidedBlock extends AbstractLichenBlock {
    public SimpleMultiSidedBlock(Settings settings) {
        super(settings);
    }

    @Override
    public LichenSpreadBehavior getLichenSpreadBehavior() {
        return new LichenSpreadBehavior(this);
    }
}
