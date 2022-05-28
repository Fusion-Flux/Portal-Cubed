package com.fusionflux.portalcubed.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class ExcursionFunnelEntityAbstract extends BlockEntity {

    public List<BlockPos.Mutable> emitters;

    public ExcursionFunnelEntityAbstract(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.emitters = new ArrayList<>();
    }

    public void updateState(BlockState state, WorldAccess world, BlockPos pos, ExcursionFunnelEntityAbstract bridge) {
        if(!world.isClient()) {
            boolean MNorth = false;
            boolean MSouth = false;
            boolean MEast = false;
            boolean MWest = false;
            boolean MUp = false;
            boolean MDown = false;

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
                if (emitter.get(Properties.FACING).equals(Direction.UP)) {
                    MUp = true;
                }
                if (emitter.get(Properties.FACING).equals(Direction.DOWN)) {
                    MDown = true;
                }
            }

            state = state.with(Properties.NORTH, MNorth).with(Properties.EAST, MEast).with(Properties.SOUTH, MSouth).with(Properties.WEST, MWest).with(Properties.UP, MUp).with(Properties.DOWN, MDown);
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