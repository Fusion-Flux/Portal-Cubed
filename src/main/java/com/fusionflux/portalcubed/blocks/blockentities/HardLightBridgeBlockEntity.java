package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HardLightBridgeBlockEntity extends BlockEntity {

    public BlockPos emitters = null;

    public HardLightBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.HLB_BLOCK_ENTITY,pos,state);
    }
    public static void tick(World world, BlockPos pos, BlockState state, HardLightBridgeBlockEntity blockEntity) {
        assert world != null;
        if (!world.isClient) {
            if (blockEntity.emitters != null) {
                if (!(world.getBlockEntity(blockEntity.emitters) instanceof HardLightBridgeEmitterBlockEntity && world.isReceivingRedstonePower(blockEntity.emitters))) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                } else if (!((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters))).posXList.contains(blockEntity.pos.getX()) && !((HardLightBridgeEmitterBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters))).posYList.contains(blockEntity.pos.getY()) && !((ExcursionFunnelEmitterEntity) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters))).posZList.contains(blockEntity.pos.getZ())) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("x", emitters.getX());
        tag.putInt("y", emitters.getY());
        tag.putInt("z", emitters.getZ());
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        emitters = (new BlockPos.Mutable(
                tag.getInt("x"),
                tag.getInt("y"),
                tag.getInt("z")
        ));
    }
}