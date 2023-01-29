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


public class ExcursionFunnelEntityMain extends BlockEntity {

    public List<BlockPos> emitters;
    public List<BlockPos> portalEmitters;
    public List<Direction> facing;

    public ExcursionFunnelEntityMain(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.EXCURSION_FUNNEL_ENTITY, pos, state);
        this.emitters = new ArrayList<>();
        this.portalEmitters = new ArrayList<>();
        this.facing = new ArrayList<>();
    }


    public void updateState(BlockState state, WorldAccess world, BlockPos pos, ExcursionFunnelEntityMain bridge) {
            if (!world.isClient()) {
                boolean MNorth = false;
                boolean MSouth = false;
                boolean MEast = false;
                boolean MWest = false;
                boolean MUp = false;
                boolean MDown = false;
                boolean MRNorth = false;
                boolean MRSouth = false;
                boolean MREast = false;
                boolean MRWest = false;
                boolean MRUp = false;
                boolean MRDown = false;


                for (Direction facing : bridge.facing) {
                    BlockState emitter = world.getBlockState(bridge.emitters.get(bridge.facing.indexOf(facing)));

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
                    if(emitter.getBlock() == PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER || emitter.getBlock() == PortalCubedBlocks.REVERSED_EXCURSION_FUNNEL_EMITTER || emitter.getBlock() == PortalCubedBlocks.DUEL_EXCURSION_FUNNEL_EMITTER || portalPresent) {

                        if (!emitter.get(CustomProperties.REVERSED)) {
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
                            if (facing.equals(Direction.UP)) {
                                MUp = true;
                            }
                            if (facing.equals(Direction.DOWN)) {
                                MDown = true;
                            }
                        }
                        if (emitter.get(CustomProperties.REVERSED)) {
                            if (facing.equals(Direction.NORTH)) {
                                MRNorth = true;
                            }
                            if (facing.equals(Direction.EAST)) {
                                MREast = true;
                            }
                            if (facing.equals(Direction.SOUTH)) {
                                MRSouth = true;
                            }
                            if (facing.equals(Direction.WEST)) {
                                MRWest = true;
                            }
                            if (facing.equals(Direction.UP)) {
                                MRUp = true;
                            }
                            if (facing.equals(Direction.DOWN)) {
                                MRDown = true;
                            }
                        }


                    }
                }

                state = state.with(Properties.NORTH, MNorth).with(Properties.EAST, MEast).with(Properties.SOUTH, MSouth).with(Properties.WEST, MWest).with(Properties.UP, MUp).with(Properties.DOWN, MDown)
                        .with(CustomProperties.RNORTH, MRNorth).with(CustomProperties.REAST, MREast).with(CustomProperties.RSOUTH, MRSouth).with(CustomProperties.RWEST, MRWest).with(CustomProperties.RUP, MRUp).with(CustomProperties.RDOWN, MRDown);
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


    public static void tick(World world, BlockPos pos, BlockState state, ExcursionFunnelEntityMain blockEntity) {
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
                    if(!(world.getBlockEntity(blockEntity.emitters.get(i)) instanceof DuelExcursionFunnelEmitterEntity) && !(world.isReceivingRedstonePower(blockEntity.emitters.get(i)))){
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    }else if(!portalPresent && !blockEntity.emitters.get(i).equals(blockEntity.portalEmitters.get(i))){
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    }else if (!(world.getBlockEntity(blockEntity.emitters.get(i)) instanceof ExcursionFunnelEmitterEntityAbstract)) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!((ExcursionFunnelEmitterEntityAbstract) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).funnels.contains(blockEntity.pos.mutableCopy())) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    }else if (!((ExcursionFunnelEmitterEntityAbstract) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).portalFunnels.contains(blockEntity.pos.mutableCopy())) {
                        if(portalPresent) {
                            blockEntity.emitters.remove(i);
                            blockEntity.portalEmitters.remove(i);
                            blockEntity.facing.remove(i);
                            blockEntity.updateState(state, world, pos, blockEntity);
                        }
                    }
                }
            }else{
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

}