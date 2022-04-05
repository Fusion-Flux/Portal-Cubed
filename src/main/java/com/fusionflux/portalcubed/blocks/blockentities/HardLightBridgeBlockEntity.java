package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class HardLightBridgeBlockEntity extends BlockEntity {

    public List<BlockPos.Mutable> emitters;


    public HardLightBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.HLB_BLOCK_ENTITY,pos,state);
        this.emitters = new ArrayList<>();
    }
    public static void tick(World world, BlockPos pos, BlockState state, HardLightBridgeBlockEntity blockEntity) {
        assert world != null;
        if (!world.isClient) {
            if (!blockEntity.emitters.isEmpty()) {

                for (int i = blockEntity.emitters.size()-1; i >= 0; i--) {

                if (!(world.getBlockEntity(blockEntity.emitters.get(i)) instanceof HardLightBridgeEmitterBlockEntity && world.isReceivingRedstonePower(blockEntity.emitters.get(i)))) {
                    blockEntity.emitters.remove(i);
                    blockEntity.updateState(state,world,pos,blockEntity);
                } else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).bridges.contains(blockEntity.pos.mutableCopy())) {
                    blockEntity.emitters.remove(i);
                    blockEntity.updateState(state,world,pos,blockEntity);
                }
            }
            }else{
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }

        }
    }

    public void updateState(BlockState state, WorldAccess world, BlockPos pos,HardLightBridgeBlockEntity bridge) {
        if(!world.isClient()) {
            boolean MNorth = false;
            boolean MSouth = false;
            boolean MEast = false;
            boolean MWest = false;

            for (BlockPos emitterPos : bridge.emitters) {
                BlockState emitter = world.getBlockState(emitterPos);

                if (emitter.get(Properties.FACING).equals(Direction.NORTH)) {
                    MNorth = true;
                }
                if (emitter.get(Properties.FACING).equals(Direction.EAST)) {
                    MEast = true;
                }
                if (emitter.get(Properties.FACING).equals(Direction.SOUTH)) {
                    MSouth = true;
                }
                if (emitter.get(Properties.FACING).equals(Direction.WEST)) {
                    MWest = true;
                }
            }

            state = state.with(Properties.NORTH, MNorth).with(Properties.EAST, MEast).with(Properties.SOUTH, MSouth).with(Properties.WEST, MWest);
        }
        world.setBlockState(pos,state,3);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        for(BlockPos pos : emitters){
            posXList.add(pos.getX());
            posYList.add(pos.getY());
            posZList.add(pos.getZ());
        }

        tag.putIntArray("xList", posXList);
        tag.putIntArray("yList", posYList);
        tag.putIntArray("zList", posZList);

        tag.putInt("size", emitters.size());

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

        if(!emitters.isEmpty())
        emitters.clear();

        for (int i = 0; i < size; i++) {
            emitters.add(new BlockPos.Mutable(posXList.get(i), posYList.get(i), posZList.get(i)));
        }
    }

}