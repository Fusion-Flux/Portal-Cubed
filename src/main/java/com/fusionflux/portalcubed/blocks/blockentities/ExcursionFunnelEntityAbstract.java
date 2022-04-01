package com.fusionflux.portalcubed.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;


public abstract class ExcursionFunnelEntityAbstract extends BlockEntity {

    public BlockPos emitters = null;

    public ExcursionFunnelEntityAbstract(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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