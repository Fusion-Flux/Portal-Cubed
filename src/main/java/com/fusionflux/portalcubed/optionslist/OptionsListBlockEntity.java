package com.fusionflux.portalcubed.optionslist;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class OptionsListBlockEntity extends BlockEntity {
    public OptionsListBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void updateListeners() {
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }
}
