package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.config.ThinkingWithPortatosConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class NeurotoxinEmitterBlockEntity extends BlockEntity implements Tickable {

    public final int MAX_RANGE = ThinkingWithPortatosConfig.get().numbersblock.maxBridgeLength;
    public final int BLOCKS_PER_TICK = 1;
    public final int EXTENSION_TIME = MAX_RANGE / BLOCKS_PER_TICK;
    private final List<BlockPos> repairPos = new ArrayList<>();
    public int extensionTicks = 0;
    public boolean bridgeComplete = false;
    public boolean alreadyPowered = false;
    public boolean shouldExtend = false;
    public boolean shouldRepair = false;
    private BlockPos.Mutable obstructorPos;

    public NeurotoxinEmitterBlockEntity() {
        super(ThinkingWithPortatosBlocks.NEUROTOXIN_EMITTER_ENTITY);
        this.obstructorPos = pos.mutableCopy();
    }

    @Override
    public void tick() {
        assert world != null;
        if (this.world.getTime() % 40L == 0L) {

            if (world.getBlockState(pos).get(Properties.POWERED)) {

            }
        }

        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(getPos());

            if (redstonePowered) {
                // Update blockstate
                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    togglePowered(world.getBlockState(pos));
                }

                // Prevents an issue with the emitter overwriting itself
                if (obstructorPos.equals(getPos())) {
                    obstructorPos.move(this.getCachedState().get(Properties.FACING));
                }
                if(world.isAir(obstructorPos)){
                    world.setBlockState(obstructorPos,ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK.getDefaultState());
                }
                // Starts the extension logic by checking the frontal adjacent position for non-obstruction
               ///if (extensionTicks <= EXTENSION_TIME) {
                    if (world.isAir(obstructorPos) || world.getBlockState(obstructorPos).getHardness(world, obstructorPos) <= 0.1F || world.getBlockState(obstructorPos).getBlock().equals(ThinkingWithPortatosBlocks.HLB_BLOCK)) {
                        world.setBlockState(obstructorPos,ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK.getDefaultState());
                    }
               // }


            }
            if (!redstonePowered) {
                // Update blockstate
                if (world.getBlockState(pos).get(Properties.POWERED)) {
                    togglePowered(world.getBlockState(pos));
                }

               // obstructorPos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());
               // obstructorPos.move(getCachedState().get(Properties.FACING));
              //  extensionTicks = 0;
              //  shouldExtend = false;
            }

            markDirty();
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