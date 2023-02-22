package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LaserCatcherBlockEntity extends AbstractLaserNodeBlockEntity {


    public LaserCatcherBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_CATCHER_ENTITY, pos, state);

    }

    public static void tick1(World world, BlockPos pos, BlockState state, LaserCatcherBlockEntity blockEntity) {
        if (!world.isClient) {
            boolean isPowered = false;
            blockEntity.updateState(state, isPowered);
        } else {
            blockEntity.clientTick(state);
        }
    }

}
