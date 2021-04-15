package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class NeurotoxinEmitterBlockEntity extends BlockEntity implements Tickable {

    public boolean shouldRepair = false;
    private final BlockPos.Mutable obstructorPos;

    public NeurotoxinEmitterBlockEntity() {
        super(ThinkingWithPortatosBlocks.NEUROTOXIN_EMITTER_ENTITY);
        this.obstructorPos = pos.mutableCopy();
    }

    @Override
    public void tick() {
        assert world != null;
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(getPos());

            if (redstonePowered) {
                // Update blockstate
                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    togglePowered(world.getBlockState(pos));
                }
            }
            if (!redstonePowered) {
                // Update blockstate
                if (world.getBlockState(pos).get(Properties.POWERED)) {
                    togglePowered(world.getBlockState(pos));
                }
            }
            if (world.isAir(this.getPos().offset(this.getCachedState().get(Properties.FACING))) && world.getBlockState(pos).get(Properties.POWERED)) {
                world.setBlockState(this.getPos().offset(this.getCachedState().get(Properties.FACING)), ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK.getDefaultState());
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