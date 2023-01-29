package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.util.CustomProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class HardLightBridgeBlockEntity extends BlockEntity {

    public List<BlockPos> emitters;
    public List<BlockPos> portalEmitters;
    public List<Direction> facing;
    public List<Direction> facingVert;

    public HardLightBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.HLB_BLOCK_ENTITY,pos,state);
        this.emitters = new ArrayList<>();
        this.portalEmitters = new ArrayList<>();
        this.facing = new ArrayList<>();
        this.facingVert = new ArrayList<>();
    }
    public static void tick(World world, BlockPos pos, BlockState state, HardLightBridgeBlockEntity blockEntity) {
        assert world != null;
        if (!world.isClient) {
            if (!blockEntity.emitters.isEmpty()) {

                for (int i = blockEntity.emitters.size()-1; i >= 0; i--) {
                    Box portalCheckBox = new Box(blockEntity.portalEmitters.get(i)).expand(.1);

                    List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);

                    boolean portalPresent = false;
                    for (ExperimentalPortal portal : list) {
                        if (portal.getFacingDirection().equals(blockEntity.facing.get(i))) {
                            if (portal.getActive()) {
                                portalPresent = true;
                            }
                        }
                    }

                    if(!portalPresent && !blockEntity.emitters.get(i).equals(blockEntity.portalEmitters.get(i))){
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.facingVert.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    }else if (!(world.getBlockEntity(blockEntity.emitters.get(i)) instanceof HardLightBridgeEmitterBlockEntity && world.isReceivingRedstonePower(blockEntity.emitters.get(i)))) {
                            blockEntity.emitters.remove(i);
                            blockEntity.portalEmitters.remove(i);
                            blockEntity.facing.remove(i);
                            blockEntity.facingVert.remove(i);
                            blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).bridges.contains(blockEntity.pos.mutableCopy())) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.facingVert.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    }else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).portalBridges.contains(blockEntity.pos.mutableCopy())) {
                        if(portalPresent) {
                            blockEntity.emitters.remove(i);
                            blockEntity.portalEmitters.remove(i);
                            blockEntity.facing.remove(i);
                            blockEntity.facingVert.remove(i);
                            blockEntity.updateState(state, world, pos, blockEntity);
                        }
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
            boolean MUp = false;
            boolean MDown = false;
            Direction resultUp = Direction.NORTH;
            Direction resultDown = Direction.NORTH;

            for (Direction facing : bridge.facing) {
                BlockState emitter = world.getBlockState(bridge.emitters.get(bridge.facing.indexOf(facing)));
                Direction facingVert = bridge.facingVert.get(bridge.facing.indexOf(facing));

                Box portalCheckBox = new Box(bridge.portalEmitters.get(bridge.facing.indexOf(facing)));

                List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);

                boolean portalPresent = false;
                for (ExperimentalPortal portal : list) {
                    if (portal.getFacingDirection().equals(facing)) {
                    if (portal.getActive()) {
                        portalPresent = true;
                    }
                     }
                }

                if (emitter.getBlock() == PortalCubedBlocks.HLB_EMITTER_BLOCK || portalPresent) {
                    if (facing.equals(Direction.NORTH)) {
                        MNorth = true;
                    }
                    if (facing.equals(Direction.EAST)) {
                        MEast = true;
                    }
                    if (facing.equals(Direction.SOUTH)) {
                        MSouth = true;
                    }
                    if (facing.equals(Direction.WEST)) {
                        MWest = true;
                    }
                    if(facing.equals(Direction.UP)){
                        MUp=true;
                        resultUp = facingVert;
                    }
                    if(facing.equals(Direction.DOWN)){
                        MDown = true;
                        resultDown = facingVert;
                    }
                }
            }

            state = state.with(Properties.NORTH, MNorth).with(Properties.EAST, MEast).with(Properties.SOUTH, MSouth).with(Properties.WEST, MWest).with(Properties.UP, MUp).with(Properties.DOWN, MDown)
                    .with(CustomProperties.HFACINGUP,resultUp).with(CustomProperties.HFACINGDOWN,resultDown);
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

        List<Integer> direcXList = new ArrayList<>();
        List<Integer> direcYList = new ArrayList<>();
        List<Integer> direcZList = new ArrayList<>();

        for(Direction direc : facing){
            direcXList.add(direc.getVector().getX());
            direcYList.add(direc.getVector().getY());
            direcZList.add(direc.getVector().getZ());
        }

        tag.putIntArray("direcxList", direcXList);
        tag.putIntArray("direcyList", direcYList);
        tag.putIntArray("direczList", direcZList);

        List<Integer> direcXListVert = new ArrayList<>();
        List<Integer> direcYListVert = new ArrayList<>();
        List<Integer> direcZListVert = new ArrayList<>();

        for(Direction direc : facingVert){
            direcXListVert.add(direc.getVector().getX());
            direcYListVert.add(direc.getVector().getY());
            direcZListVert.add(direc.getVector().getZ());
        }

        tag.putIntArray("direcxListVert", direcXListVert);
        tag.putIntArray("direcyListVert", direcYListVert);
        tag.putIntArray("direczListVert", direcZListVert);

        List<Integer> portalXList = new ArrayList<>();
        List<Integer> portalYList = new ArrayList<>();
        List<Integer> portalZList = new ArrayList<>();

        for(BlockPos direc : portalEmitters){
            portalXList.add(direc.getX());
            portalYList.add(direc.getY());
            portalZList.add(direc.getZ());
        }

        tag.putIntArray("portalxList", portalXList);
        tag.putIntArray("portalyList", portalYList);
        tag.putIntArray("portalzList", portalZList);

        tag.putInt("size", emitters.size());

    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        List<Integer> posXList;
        List<Integer> posYList;
        List<Integer> posZList;

        posXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("xList")));
        posYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("yList")));
        posZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("zList")));

        int size = tag.getInt("size");

        if(!emitters.isEmpty())
        emitters.clear();

        for (int i = 0; i < size; i++) {
            emitters.add(new BlockPos.Mutable(posXList.get(i), posYList.get(i), posZList.get(i)));
        }


        List<Integer> direcXList;
        List<Integer> direcYList;
        List<Integer> direcZList;

        direcXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direcxList")));
        direcYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direcyList")));
        direcZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direczList")));

        if(!facing.isEmpty())
            facing.clear();

        for (int i = 0; i < size; i++) {
            facing.add(Direction.fromVector(direcXList.get(i), direcYList.get(i), direcZList.get(i)));
        }

        List<Integer> direcXListVert;
        List<Integer> direcYListVert;
        List<Integer> direcZListVert;

        direcXListVert = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direcxListVert")));
        direcYListVert = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direcyListVert")));
        direcZListVert = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direczListVert")));

        if(!facingVert.isEmpty())
            facingVert.clear();

        for (int i = 0; i < size; i++) {
            facingVert.add(Direction.fromVector(direcXListVert.get(i), direcYListVert.get(i), direcZListVert.get(i)));
        }

        List<Integer> portalXList;
        List<Integer> portalYList;
        List<Integer> portalZList;

       portalXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalxList")));
       portalYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalyList")));
       portalZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalzList")));

        if(!portalEmitters.isEmpty())
            portalEmitters.clear();

        for (int i = 0; i < size; i++) {
            portalEmitters.add(new BlockPos.Mutable(portalXList.get(i), portalYList.get(i), portalZList.get(i)));
        }
    }

}