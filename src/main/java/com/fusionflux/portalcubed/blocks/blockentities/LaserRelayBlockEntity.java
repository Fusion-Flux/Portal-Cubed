package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LaserRelayBlockEntity extends AbstractLaserNodeBlockEntity {


    public LaserRelayBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_RELAY_ENTITY, pos, state);

    }

    public static void tick1(World world, BlockPos pos, BlockState state, LaserRelayBlockEntity blockEntity) {
        if (!world.isClient) {
            Direction storedDirec = blockEntity.getCachedState().get(Properties.FACING);
            BlockPos transPos = pos.offset(storedDirec);
            BlockState neighborState = world.getBlockState(transPos);
            boolean isPowered = neighborState.getBlock().equals(PortalCubedBlocks.LASER);
            blockEntity.updateState(state, isPowered);

        } else {
            blockEntity.clientTick(state);
        }


    }

}
