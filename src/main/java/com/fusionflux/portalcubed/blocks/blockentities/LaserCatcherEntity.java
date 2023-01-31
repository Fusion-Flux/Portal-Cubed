package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.util.CustomProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class LaserCatcherEntity extends BlockEntity {

    public final int MAX_RANGE = PortalCubedConfig.maxBridgeLength;


    public LaserCatcherEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_CATCHER_ENTITY,pos,state);

    }

    public static void tick1(World world, BlockPos pos, BlockState state, LaserCatcherEntity blockEntity) {
        if (!world.isClient) {
            Direction storedDirec = blockEntity.getCachedState().get(Properties.FACING);
            BlockPos transPos = pos.offset(storedDirec);
            BlockState neighborState = world.getBlockState(transPos);
            boolean isPowered = false;
            if(neighborState.getBlock().equals(PortalCubedBlocks.LASER)){
                if(!neighborState.get(CustomProperties.REFLECT)){
                    if(neighborState.get(Properties.NORTH) && storedDirec.equals(Direction.SOUTH)){
                        isPowered = true;
                    }
                    if(neighborState.get(Properties.SOUTH) && storedDirec.equals(Direction.NORTH)){
                        isPowered = true;
                    }
                    if(neighborState.get(Properties.EAST) && storedDirec.equals(Direction.WEST)){
                        isPowered = true;
                    }
                    if(neighborState.get(Properties.WEST) && storedDirec.equals(Direction.EAST)){
                        isPowered = true;
                    }
                    if(neighborState.get(Properties.UP) && storedDirec.equals(Direction.DOWN)){
                        isPowered = true;
                    }
                    if(neighborState.get(Properties.DOWN) && storedDirec.equals(Direction.UP)){
                        isPowered = true;
                    }
                }else{
                    if(neighborState.getProperties().contains(Properties.FACING)) {
                        if(neighborState.get(Properties.FACING).equals(storedDirec)){
                            isPowered = true;
                        }
                    }
                }
            }
            blockEntity.updateState(state,isPowered);

        }
    }

    public void updateState(BlockState state, boolean toggle) {
        if(world != null) {
            world.setBlockState(pos,state.with(Properties.ENABLED,toggle),3);
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
    }

}