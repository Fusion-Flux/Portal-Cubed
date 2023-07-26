package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.blocks.funnel.TwoByTwoFacingMultiblockBlock;
import com.fusionflux.portalcubed.util.TwoByTwo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface MultiblockItem {
    <T extends Block & TwoByTwoFacingMultiblockBlock> T getMultiblockBlock();

    @Nullable
    TwoByTwo findValidPlacement(Level level, BlockState state, BlockPos initial, Direction playerFacing);
}
