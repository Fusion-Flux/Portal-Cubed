package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;


public class ReversedExcursionFunnelEntity extends ExcursionFunnelEntityAbstract {

    public ReversedExcursionFunnelEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.REVERSED_EXCURSION_FUNNEL_ENTITY,pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ReversedExcursionFunnelEntity blockEntity) {
        assert world != null;
        if (!world.isClient) {
            if (blockEntity.emitters != null) {
                if (!(world.getBlockEntity(blockEntity.emitters) instanceof ExcursionFunnelEmitterEntityAbstract && world.isReceivingRedstonePower(blockEntity.emitters))) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                } else if (!((ExcursionFunnelEmitterEntityAbstract) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters))).posXList.contains(blockEntity.pos.getX()) && !((ExcursionFunnelEmitterEntityAbstract) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters))).posYList.contains(blockEntity.pos.getY()) && !((ExcursionFunnelEmitterEntityAbstract) Objects.requireNonNull(world.getBlockEntity(blockEntity.emitters))).posZList.contains(blockEntity.pos.getZ())) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }

}