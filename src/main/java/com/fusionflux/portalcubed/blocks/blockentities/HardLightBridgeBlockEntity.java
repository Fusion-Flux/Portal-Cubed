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

    public BlockPos emitter;
    public final List<BlockPos> portalEmitters;

    public HardLightBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.HLB_BLOCK_ENTITY, pos, state);
        this.emitter = new BlockPos(0, 0, 0);
        this.portalEmitters = new ArrayList<>();
    }


    public static void tick(World world, BlockPos pos, BlockState state, HardLightBridgeBlockEntity blockEntity) {
        assert world != null;
        if (!world.isClient) {
            if (!blockEntity.portalEmitters.isEmpty()) {
                for (int i = blockEntity.portalEmitters.size() - 1; i >= 0; i--) {
                    Box portalCheckBox = new Box(blockEntity.portalEmitters.get(i)).expand(.1);

                    List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);

                    boolean portalPresent = false;
                    for (ExperimentalPortal portal : list) {
                        if (portal.getFacingDirection().equals(state.get(Properties.FACING))) {
                            if (portal.getActive()) {
                                portalPresent = true;
                            }
                        }
                    }

                    if (!portalPresent && !blockEntity.emitter.equals(blockEntity.portalEmitters.get(i))) {
                        blockEntity.portalEmitters.remove(i);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    } else if (!(world.getBlockEntity(blockEntity.emitter) instanceof HardLightBridgeEmitterBlockEntity && world.isReceivingRedstonePower(blockEntity.emitter))) {
                        blockEntity.portalEmitters.remove(i);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    } else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitter))).bridges.contains(blockEntity.pos.mutableCopy())) {
                        blockEntity.portalEmitters.remove(i);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    } else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitter))).portalBridges.contains(blockEntity.pos.mutableCopy())) {
                        if (portalPresent) {
                            blockEntity.portalEmitters.remove(i);
                            world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        }
                    }

                }
            } else {
                if (!(world.getBlockEntity(blockEntity.emitter) instanceof HardLightBridgeEmitterBlockEntity && world.isReceivingRedstonePower(blockEntity.emitter))) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                } else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitter))).bridges.contains(blockEntity.pos.mutableCopy())) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }

        }
    }

    public void updateState(BlockState state, WorldAccess world, BlockPos pos, Direction facing, Direction facingVert) {
        if (!world.isClient()) {
            state = state.with(Properties.FACING, facing).with(CustomProperties.H_FACING_UP, facingVert).with(CustomProperties.H_FACING_DOWN, facingVert);
        }
        world.setBlockState(pos, state, 3);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        tag.putInt("emitterX", emitter.getX());
        tag.putInt("emitterY", emitter.getY());
        tag.putInt("emitterZ", emitter.getZ());

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

        tag.putInt("size", portalEmitters.size());

    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        emitter = new BlockPos(tag.getInt("emitterX"), tag.getInt("emitterY"), tag.getInt("emitterZ"));

        int size = tag.getInt("size");

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

}
