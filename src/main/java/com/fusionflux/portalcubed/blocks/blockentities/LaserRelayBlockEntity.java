package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LaserRelayBlockEntity extends AbstractLaserNodeBlockEntity {


    public LaserRelayBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_RELAY_ENTITY, pos, state);

    }

    public static void tick1(World world, BlockPos pos, BlockState state, LaserRelayBlockEntity blockEntity) {
        if (!world.isClient) {

        } else {
            blockEntity.clientTick(state);
        }
    }

}
