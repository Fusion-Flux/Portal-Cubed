package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class HardLightBridgeEmitterBlockEntity extends ExcursionFunnelEmitterEntityAbstract {

    public final int MAX_RANGE = PortalCubedConfig.get().numbersblock.maxBridgeLength;
    public List<BlockPos> bridges;

    public HardLightBridgeEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.HLB_EMITTER_ENTITY,pos,state);
        this.bridges = new ArrayList<>();
    }
    public static void tick(World world, BlockPos pos, BlockState state, HardLightBridgeEmitterBlockEntity blockEntity) {
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;

                if (blockEntity.bridges != null) {
                    List<BlockPos> modfunnels = new ArrayList<>();


                    for (int i = 0; i <= blockEntity.MAX_RANGE; i++) {
                        translatedPos = translatedPos.offset(blockEntity.getCachedState().get(Properties.FACING));
                        if (world.isAir(translatedPos) || world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.HLB_BLOCK)) {

                            if(!world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.HLB_BLOCK)) {
                                world.setBlockState(translatedPos, PortalCubedBlocks.HLB_BLOCK.getDefaultState());
                            }
                            HardLightBridgeBlockEntity bridge = ((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modfunnels.add(bridge.getPos());
                            blockEntity.bridges.add(bridge.getPos());

                            if(!bridge.emitters.contains(pos) ) {
                                bridge.emitters.add(pos);
                            }
                            bridge.updateState(world.getBlockState(translatedPos),world,translatedPos,bridge);


                        } else {
                            blockEntity.bridges = modfunnels;
                            break;
                        }
                    }
                }

            }

            if (!redstonePowered) {
                if (world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }
            }

        }


    }

    public void playSound(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.1F, 3.0F);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        for(BlockPos pos : bridges){
            posXList.add(pos.getX());
            posYList.add(pos.getY());
            posZList.add(pos.getZ());
        }

        tag.putIntArray("xList", posXList);
        tag.putIntArray("yList", posYList);
        tag.putIntArray("zList", posZList);

        tag.putInt("size", bridges.size());
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        posXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("xList")));
        posYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("yList")));
        posZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("zList")));

        int size = tag.getInt("size");

        if(!bridges.isEmpty())
            bridges.clear();

        for (int i = 0; i < size; i++) {
            bridges.add(new BlockPos.Mutable(posXList.get(i), posYList.get(i), posZList.get(i)));
        }
    }


}