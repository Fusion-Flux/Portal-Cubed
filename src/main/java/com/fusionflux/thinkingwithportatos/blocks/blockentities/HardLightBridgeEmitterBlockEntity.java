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
public class HardLightBridgeEmitterBlockEntity extends BlockEntity implements Tickable {

    public final int MAX_RANGE = ThinkingWithPortatosConfig.get().numbersblock.maxBridgeLength;
    public final int BLOCKS_PER_TICK = 1;
    public final int EXTENSION_TIME = MAX_RANGE / BLOCKS_PER_TICK;

    public int extensionTicks = 0;

    public boolean bridgeComplete = false;
    public boolean alreadyPowered = false;
    public boolean shouldExtend = false;

    public boolean shouldRepair = false;
    private final List<BlockPos> repairPos = new ArrayList<>();

    private BlockPos.Mutable obstructorPos;

    public HardLightBridgeEmitterBlockEntity() {
        super(ThinkingWithPortatosBlocks.HLB_EMITTER_ENTITY);
        this.obstructorPos = pos.mutableCopy();
    }

    @Override
    public void tick() {
        assert world != null;
        if (this.world.getTime() % 40L == 0L) {

            if (world.getBlockState(pos).get(Properties.POWERED)) {
                this.playSound2(SoundEvents.BLOCK_BEACON_AMBIENT);
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

                // Starts the extension logic by checking the frontal adjacent position for non-obstruction
                if (extensionTicks <= EXTENSION_TIME) {
                    if (world.isAir(obstructorPos) || world.getBlockState(obstructorPos).getHardness(world, obstructorPos) <= 0.1F || world.getBlockState(obstructorPos).getBlock().equals(ThinkingWithPortatosBlocks.HLB_BLOCK)) {
                        shouldExtend = true;
                        extendBridge(getCachedState(), (ServerWorld) world, getPos());
                    }
                }

                maintainBridge();
            }
            if (!redstonePowered) {
                // Update blockstate
                if (world.getBlockState(pos).get(Properties.POWERED)) {
                    togglePowered(world.getBlockState(pos));
                }

                obstructorPos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());
                obstructorPos.move(getCachedState().get(Properties.FACING));
                extensionTicks = 0;
                shouldExtend = false;
            }

            markDirty();
        }
    }

    public void playSound(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.1F, 3.0F);
    }

    public void playSound2(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.05F, 3.0F);
    }

    /**
     * Handles the extension of a bridge over multiple ticks. The {@link #MAX_RANGE} and
     * {@link #BLOCKS_PER_TICK} fields can be adjusted to change the maximum length of
     * bridges and allow bridges to place multiple blocks per tick. {@link #EXTENSION_TIME}
     * is derived based on these values.
     * <p>The initial {@link BlockPos.Mutable#move(Direction) move} calls serve to orient the
     * iterator at the appropriate starting distance each tick.</p>
     *
     * @param state not gonna lie
     * @param world these parameters should be optimized at some point,
     * @param pos   maybe even removed
     */
    private void extendBridge(BlockState state, ServerWorld world, BlockPos pos) {
        Direction facing = state.get(Properties.FACING);
        BlockPos.Mutable extendPos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());

        extendPos.move(facing);
        extendPos.move(facing, extensionTicks);
        ++extensionTicks;
        for (int i = 0; i < BLOCKS_PER_TICK; i++) {
            if (extensionTicks <= EXTENSION_TIME) {
                if (world.getBlockState(extendPos).getBlock().equals(ThinkingWithPortatosBlocks.HLB_BLOCK)) {
                    ((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(extendPos))).emitters.add(new BlockPos.Mutable(getPos().getX(), getPos().getY(), getPos().getZ()));
                } else if (world.isAir(extendPos) || world.getBlockState(obstructorPos).getHardness(world, obstructorPos) <= 0.1F) {
                    world.setBlockState(extendPos, ThinkingWithPortatosBlocks.HLB_BLOCK.getDefaultState().with(Properties.FACING, facing));
                    ((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(extendPos))).emitters.add(new BlockPos.Mutable(getPos().getX(), getPos().getY(), getPos().getZ()));
                } else {
                    bridgeComplete = true;
                    shouldExtend = false;
                    break;
                }

            } else {
                bridgeComplete = true;
                shouldExtend = false;
                break;
            }
            extendPos.move(facing);
        }

        obstructorPos.set(extendPos);
        // System.out.println("Obstructor at end of extension call this tick: " + obstructorPos);
    }

    /**
     * Primary method for fixing any erroneously broken {@link HardLightBridgeBlock}s due to explosions,
     * manual breaking, etc:
     * <p>1) Iterates from frontal adjacent {@link BlockPos} to the current {@link #obstructorPos}.
     * For each position, if it contains air, create a {@link HardLightBridgeBlock}, and add
     * {@link HardLightBridgeEmitterBlockEntity this}' {@link BlockPos} to its {@link HardLightBridgeBlockEntity#emitters emitters} list.
     * If the position contains a bridge, add an entry to that instead. Else, update the active
     * {@link #obstructorPos} and break the loop.</p>
     * <p>2) If the obstructor position has been updated, iterate from the old obstructor position to
     * the new one, removing {@link HardLightBridgeEmitterBlockEntity this}' {@link BlockPos} from each {@link HardLightBridgeBlockEntity}'s
     * {@link HardLightBridgeBlockEntity#emitters emitters} list.</p>
     */
    private void maintainBridge() {
        assert world != null;
        if (!world.isClient) {
            Direction facing = this.getCachedState().get(Properties.FACING);
            BlockPos.Mutable maintainPos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());
            maintainPos.move(facing);
            boolean shouldRetract = false;

            // Iterate from first bridge block out to obstructor
            while (!maintainPos.equals(obstructorPos)) {
                // If position is empty (read: destroyed), create a bridge block
                if (world.isAir(maintainPos)) {
                    world.setBlockState(maintainPos, ThinkingWithPortatosBlocks.HLB_BLOCK.getDefaultState().with(Properties.FACING, facing), 3);
                    ((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(maintainPos))).emitters.add(new BlockPos.Mutable(getPos().getX(), getPos().getY(), getPos().getZ()));
                }
                // Else if position is not another bridge block, mark it as the obstructor and exit the loop
                else if (!world.getBlockState(maintainPos).getBlock().equals(ThinkingWithPortatosBlocks.HLB_BLOCK)) {
                    shouldRetract = true;
                    break;
                } else if (!((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(maintainPos))).emitters.contains(pos.mutableCopy())) {
                    ((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(maintainPos))).emitters.add(new BlockPos.Mutable(getPos().getX(), getPos().getY(), getPos().getZ()));
                }

                // Step forward if still looping
                maintainPos.move(facing);
            }

            if (shouldRetract) {
                int retractedDistance = 0; // <-- add this line
                BlockPos.Mutable cullPos = getPos().mutableCopy();
                while (!obstructorPos.equals(maintainPos)) {
                    ++retractedDistance; // <-- add this line
                    obstructorPos.move(facing.getOpposite());
                    if (world.getBlockState(obstructorPos).getBlock().equals(ThinkingWithPortatosBlocks.HLB_BLOCK)) {
                        ((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(obstructorPos))).emitters.remove(cullPos);
                    }
                }
                extensionTicks -= retractedDistance; // <-- add this line
            }
        }
    }

    /**
     * Callback method called by {@link HardLightBridgeBlockEntity}'s when removed.
     * <p>Supplies an entry of the {@link BlockEntity}'s {@link BlockPos} to the {@link #repairPos} list.</p>
     * Marks the need for repair, though this may be refactored later.
     *
     * @param pos the BlockPos passed by the calling bridge block.
     * @see HardLightBridgeBlock
     * @see BlockEntity
     */
    public void repairUpdate(BlockPos pos) {
        repairPos.add(pos);
        shouldRepair = true;
    }

    /**
     * Used to correct a bug with the initial assignment and manipulation of {@link #obstructorPos}
     * during its first {@link #tick()}. Without this method or some other solution, the position will
     * always be [ 0, 0, 0 ].
     *
     * @param ownerPos the {@link BlockPos} of the owning {@link HardLightBridgeEmitterBlock}.
     */
    public void spookyUpdateObstructor(BlockPos ownerPos) {
        this.obstructorPos.set(ownerPos);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putBoolean("bridgeComplete", bridgeComplete);
        tag.putBoolean("alreadyPowered", alreadyPowered);
        tag.putBoolean("shouldExtend", shouldExtend);

        tag.putInt("extTicks", extensionTicks);

        // Due to limitations of CompoundTag, we have to separately write each part of any BlockPos
        tag.putInt("obsx", obstructorPos.getX());
        tag.putInt("obsy", obstructorPos.getY());
        tag.putInt("obsz", obstructorPos.getZ());

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        bridgeComplete = tag.getBoolean("bridgeComplete");
        alreadyPowered = tag.getBoolean("alreadyPowered");
        shouldExtend = tag.getBoolean("shouldExtend");

        tag.getInt("extTicks");


        // Due to limitations of CompoundTag, we have to separately read each part of any BlockPos
        obstructorPos = new BlockPos.Mutable(
                tag.getInt("obsx"),
                tag.getInt("obsy"),
                tag.getInt("obsz")
        );
    }

    private void togglePowered(BlockState state) {
        assert world != null;
        world.setBlockState(pos, state.cycle(Properties.POWERED));
        if (world.getBlockState(pos).get(Properties.POWERED)) {
            this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);
        }
        if (!world.getBlockState(pos).get(Properties.POWERED)) {
            this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
        }
    }

}