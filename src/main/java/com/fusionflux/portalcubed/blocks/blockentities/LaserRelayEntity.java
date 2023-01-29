package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
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
public class LaserRelayEntity extends BlockEntity {

    public final int MAX_RANGE = PortalCubedConfig.maxBridgeLength;


    public LaserRelayEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_RELAY_ENTITY,pos,state);

    }

    public static void tick1(World world, BlockPos pos, BlockState state, LaserRelayEntity blockEntity) {
        if (!world.isClient) {
            Direction storedDirec = blockEntity.getCachedState().get(Properties.FACING);
            BlockPos transPos = pos.offset(storedDirec);
            BlockState neighborState = world.getBlockState(transPos);
            boolean isPowered = neighborState.getBlock().equals(PortalCubedBlocks.LASER);
            blockEntity.updateState(state,isPowered);

        }


    }

    public void playSound(SoundEvent soundEvent) {
        assert this.world != null;
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.1F, 3.0F);
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