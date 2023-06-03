package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.util.CustomProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class HardLightBridgeBlockEntity extends BlockEntity {

    public final List<BlockPos> emitters;
    public final List<BlockPos> portalEmitters;
    public final List<Direction> facing;
    public final List<Direction> facingVert;

    public HardLightBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.HLB_BLOCK_ENTITY, pos, state);
        this.emitters = new ArrayList<>();
        this.portalEmitters = new ArrayList<>();
        this.facing = new ArrayList<>();
        this.facingVert = new ArrayList<>();
    }
    public static void tick(Level world, BlockPos pos, BlockState state, HardLightBridgeBlockEntity blockEntity) {
        assert world != null;
        if (!world.isClientSide) {
            if (!blockEntity.emitters.isEmpty()) {

                for (int i = blockEntity.emitters.size() - 1; i >= 0; i--) {
                    AABB portalCheckBox = new AABB(blockEntity.portalEmitters.get(i)).inflate(.1);

                    List<ExperimentalPortal> list = world.getEntitiesOfClass(ExperimentalPortal.class, portalCheckBox);

                    boolean portalPresent = false;
                    for (ExperimentalPortal portal : list) {
                        if (portal.getFacingDirection().equals(blockEntity.facing.get(i))) {
                            if (portal.getActive()) {
                                portalPresent = true;
                            }
                        }
                    }

                    if (!portalPresent && !blockEntity.emitters.get(i).equals(blockEntity.portalEmitters.get(i))) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.facingVert.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!(world.getBlockEntity(blockEntity.emitters.get(i)) instanceof HardLightBridgeEmitterBlockEntity && world.hasNeighborSignal(blockEntity.emitters.get(i)))) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.facingVert.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).bridges.contains(blockEntity.worldPosition.mutable())) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.facingVert.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).portalBridges.contains(blockEntity.worldPosition.mutable())) {
                        if (portalPresent) {
                            blockEntity.emitters.remove(i);
                            blockEntity.portalEmitters.remove(i);
                            blockEntity.facing.remove(i);
                            blockEntity.facingVert.remove(i);
                            blockEntity.updateState(state, world, pos, blockEntity);
                        }
                    }

                }
            } else {
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }

        }
    }

    public void updateState(BlockState state, LevelAccessor world, BlockPos pos, HardLightBridgeBlockEntity bridge) {
        if (!world.isClientSide()) {
            boolean mNorth = false;
            boolean mSouth = false;
            boolean mEast = false;
            boolean mWest = false;
            boolean mUp = false;
            boolean mDown = false;
            Direction resultUp = Direction.NORTH;
            Direction resultDown = Direction.NORTH;

            for (Direction facing : bridge.facing) {
                BlockState emitter = world.getBlockState(bridge.emitters.get(bridge.facing.indexOf(facing)));
                Direction facingVert = bridge.facingVert.get(bridge.facing.indexOf(facing));

                AABB portalCheckBox = new AABB(bridge.portalEmitters.get(bridge.facing.indexOf(facing)));

                List<ExperimentalPortal> list = world.getEntitiesOfClass(ExperimentalPortal.class, portalCheckBox);

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
                        mNorth = true;
                    }
                    if (facing.equals(Direction.EAST)) {
                        mEast = true;
                    }
                    if (facing.equals(Direction.SOUTH)) {
                        mSouth = true;
                    }
                    if (facing.equals(Direction.WEST)) {
                        mWest = true;
                    }
                    if (facing.equals(Direction.UP)) {
                        mUp = true;
                        resultUp = facingVert;
                    }
                    if (facing.equals(Direction.DOWN)) {
                        mDown = true;
                        resultDown = facingVert;
                    }
                }
            }

            state = state.setValue(BlockStateProperties.NORTH, mNorth).setValue(BlockStateProperties.EAST, mEast).setValue(BlockStateProperties.SOUTH, mSouth).setValue(BlockStateProperties.WEST, mWest).setValue(BlockStateProperties.UP, mUp).setValue(BlockStateProperties.DOWN, mDown)
                    .setValue(CustomProperties.HFACINGUP, resultUp).setValue(CustomProperties.HFACINGDOWN, resultDown);
        }
        world.setBlock(pos, state, 3);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        for (BlockPos pos : emitters) {
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

        for (Direction direc : facing) {
            direcXList.add(direc.getNormal().getX());
            direcYList.add(direc.getNormal().getY());
            direcZList.add(direc.getNormal().getZ());
        }

        tag.putIntArray("direcxList", direcXList);
        tag.putIntArray("direcyList", direcYList);
        tag.putIntArray("direczList", direcZList);

        List<Integer> direcXListVert = new ArrayList<>();
        List<Integer> direcYListVert = new ArrayList<>();
        List<Integer> direcZListVert = new ArrayList<>();

        for (Direction direc : facingVert) {
            direcXListVert.add(direc.getNormal().getX());
            direcYListVert.add(direc.getNormal().getY());
            direcZListVert.add(direc.getNormal().getZ());
        }

        tag.putIntArray("direcxListVert", direcXListVert);
        tag.putIntArray("direcyListVert", direcYListVert);
        tag.putIntArray("direczListVert", direcZListVert);

        List<Integer> portalXList = new ArrayList<>();
        List<Integer> portalYList = new ArrayList<>();
        List<Integer> portalZList = new ArrayList<>();

        for (BlockPos direc : portalEmitters) {
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
    public void load(CompoundTag tag) {
        super.load(tag);

        List<Integer> posXList;
        List<Integer> posYList;
        List<Integer> posZList;

        posXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("xList")));
        posYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("yList")));
        posZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("zList")));

        int size = tag.getInt("size");

        emitters.clear();

        for (int i = 0; i < size; i++) {
            emitters.add(new BlockPos.MutableBlockPos(posXList.get(i), posYList.get(i), posZList.get(i)));
        }


        List<Integer> direcXList;
        List<Integer> direcYList;
        List<Integer> direcZList;

        direcXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direcxList")));
        direcYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direcyList")));
        direcZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direczList")));

        if (!facing.isEmpty())
            facing.clear();

        for (int i = 0; i < size; i++) {
            facing.add(Direction.fromNormal(direcXList.get(i), direcYList.get(i), direcZList.get(i)));
        }

        List<Integer> direcXListVert;
        List<Integer> direcYListVert;
        List<Integer> direcZListVert;

        direcXListVert = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direcxListVert")));
        direcYListVert = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direcyListVert")));
        direcZListVert = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("direczListVert")));

        if (!facingVert.isEmpty())
            facingVert.clear();

        for (int i = 0; i < size; i++) {
            facingVert.add(Direction.fromNormal(direcXListVert.get(i), direcYListVert.get(i), direcZListVert.get(i)));
        }

        List<Integer> portalXList;
        List<Integer> portalYList;
        List<Integer> portalZList;

        portalXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalxList")));
        portalYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalyList")));
        portalZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalzList")));

        if (!portalEmitters.isEmpty())
            portalEmitters.clear();

        for (int i = 0; i < size; i++) {
            portalEmitters.add(new BlockPos.MutableBlockPos(portalXList.get(i), portalYList.get(i), portalZList.get(i)));
        }
    }

}
