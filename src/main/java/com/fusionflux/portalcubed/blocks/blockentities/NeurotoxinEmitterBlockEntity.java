package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class NeurotoxinEmitterBlockEntity extends BlockEntity {

    private final BlockPos.Mutable obstructorPos;

    public NeurotoxinEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.NEUROTOXIN_EMITTER_ENTITY,pos,state);
        this.obstructorPos = pos.mutableCopy();
    }



    public static void tick(World world, BlockPos pos, @SuppressWarnings("unused") BlockState state, NeurotoxinEmitterBlockEntity blockEntity) {
        assert world != null;
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (redstonePowered) {
                // Update blockstate
                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }
            }
            if (!redstonePowered) {
                // Update blockstate
                if (world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }
            }
            if (world.isAir(blockEntity.getPos().offset(blockEntity.getCachedState().get(Properties.FACING))) && world.getBlockState(pos).get(Properties.POWERED)) {
                world.setBlockState(blockEntity.getPos().offset(blockEntity.getCachedState().get(Properties.FACING)), PortalCubedBlocks.NEUROTOXIN_BLOCK.getDefaultState());
            }

        }
    }

    public void spookyUpdateObstructor(BlockPos ownerPos) {
        this.obstructorPos.set(ownerPos);
    }

    private void togglePowered(BlockState state) {
        assert world != null;
        world.setBlockState(pos, state.cycle(Properties.POWERED));
    }

}