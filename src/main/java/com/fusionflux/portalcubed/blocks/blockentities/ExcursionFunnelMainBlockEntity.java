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


public class ExcursionFunnelMainBlockEntity extends BlockEntity {

    public final List<BlockPos> emitters;
    public final List<BlockPos> portalEmitters;
    public final List<Direction> facing;

    public ExcursionFunnelMainBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.EXCURSION_FUNNEL_ENTITY, pos, state);
        this.emitters = new ArrayList<>();
        this.portalEmitters = new ArrayList<>();
        this.facing = new ArrayList<>();
    }


    public void updateState(BlockState state, WorldAccess world, BlockPos pos, ExcursionFunnelMainBlockEntity bridge) {
        if (!world.isClient()) {
            boolean mNorth = false;
            boolean mSouth = false;
            boolean mEast = false;
            boolean mWest = false;
            boolean mUp = false;
            boolean mDown = false;
            boolean mRNorth = false;
            boolean mRSouth = false;
            boolean mREast = false;
            boolean mRWest = false;
            boolean mRUp = false;
            boolean mRDown = false;


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
                if (emitter.getBlock() == PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER || emitter.getBlock() == PortalCubedBlocks.REVERSED_EXCURSION_FUNNEL_EMITTER || emitter.getBlock() == PortalCubedBlocks.DUEL_EXCURSION_FUNNEL_EMITTER || portalPresent) {

                    if (!emitter.get(CustomProperties.REVERSED)) {
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
                        }
                        if (facing.equals(Direction.DOWN)) {
                            mDown = true;
                        }
                    }
                    if (emitter.get(CustomProperties.REVERSED)) {
                        if (facing.equals(Direction.NORTH)) {
                            mRNorth = true;
                        }
                        if (facing.equals(Direction.EAST)) {
                            mREast = true;
                        }
                        if (facing.equals(Direction.SOUTH)) {
                            mRSouth = true;
                        }
                        if (facing.equals(Direction.WEST)) {
                            mRWest = true;
                        }
                        if (facing.equals(Direction.UP)) {
                            mRUp = true;
                        }
                        if (facing.equals(Direction.DOWN)) {
                            mRDown = true;
                        }
                    }


                }
            }

            state = state.with(Properties.NORTH, mNorth).with(Properties.EAST, mEast).with(Properties.SOUTH, mSouth).with(Properties.WEST, mWest).with(Properties.UP, mUp).with(Properties.DOWN, mDown)
                    .with(CustomProperties.RNORTH, mRNorth).with(CustomProperties.REAST, mREast).with(CustomProperties.RSOUTH, mRSouth).with(CustomProperties.RWEST, mRWest).with(CustomProperties.RUP, mRUp).with(CustomProperties.RDOWN, mRDown);
        }

        world.setBlockState(pos, state, 3);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

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
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        List<Integer> posXList;
        List<Integer> posYList;
        List<Integer> posZList;

        posXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("xList")));
        posYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("yList")));
        posZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("zList")));

        int size = tag.getInt("size");

        if (!emitters.isEmpty())
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

        if (!facing.isEmpty())
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

        if (!portalEmitters.isEmpty())
            portalEmitters.clear();

        for (int i = 0; i < size; i++) {
            portalEmitters.add(new BlockPos.Mutable(portalXList.get(i), portalYList.get(i), portalZList.get(i)));
        }
    }


    public static void tick(World world, BlockPos pos, BlockState state, ExcursionFunnelMainBlockEntity blockEntity) {
        assert world != null;
        if (!world.isClient) {
            if (!blockEntity.emitters.isEmpty()) {
                for (int i = blockEntity.emitters.size() - 1; i >= 0; i--) {
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
                    if (!(world.getBlockEntity(blockEntity.emitters.get(i)) instanceof DualExcursionFunnelEmitterBlockEntity) && !(world.isReceivingRedstonePower(blockEntity.emitters.get(i)))) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!portalPresent && !blockEntity.emitters.get(i).equals(blockEntity.portalEmitters.get(i))) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!(world.getBlockEntity(blockEntity.emitters.get(i)) instanceof AbstractExcursionFunnelEmitterBlockEntity)) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!((AbstractExcursionFunnelEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).funnels.contains(blockEntity.pos.mutableCopy())) {
                        blockEntity.emitters.remove(i);
                        blockEntity.portalEmitters.remove(i);
                        blockEntity.facing.remove(i);
                        blockEntity.updateState(state, world, pos, blockEntity);
                    } else if (!((AbstractExcursionFunnelEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters.get(i)))).portalFunnels.contains(blockEntity.pos.mutableCopy())) {
                        if (portalPresent) {
                            blockEntity.emitters.remove(i);
                            blockEntity.portalEmitters.remove(i);
                            blockEntity.facing.remove(i);
                            blockEntity.updateState(state, world, pos, blockEntity);
                        }
                    }
                }
            } else {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

}
