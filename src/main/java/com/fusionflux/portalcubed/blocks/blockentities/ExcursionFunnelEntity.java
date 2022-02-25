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


public class ExcursionFunnelEntity extends BlockEntity {

    public final List<BlockPos.Mutable> emitters = new ArrayList<>();

    public ExcursionFunnelEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.EXCURSION_FUNNEL_ENTITY,pos,state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExcursionFunnelEntity blockEntity) {
        assert world != null;


        if (!world.isClient) {
            // Iterate over supporting emitters to guarantee support
            ArrayList<BlockPos.Mutable> emittersToRemove = new ArrayList<>();
            BlockPos.Mutable emitter;

            for (BlockPos.Mutable mutable : blockEntity.emitters) {
                emitter = mutable;
                if (!(world.getBlockEntity(emitter) instanceof ExcursionFunnelEmitterEntity && world.isReceivingRedstonePower(mutable))) {
                    emittersToRemove.add(mutable);
                }
            }

// Clean up emitters
            for (BlockPos.Mutable pos2 : emittersToRemove) {
                blockEntity.emitters.remove(pos2);
            }

            // If no remaining emitters or no power, replace with AIR
            if (blockEntity.emitters.stream().noneMatch((emit) -> world.isReceivingRedstonePower(emit))) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            } else if (!world.getBlockState(pos).get(Properties.FACING)
                    .equals(world.getBlockState(blockEntity.emitters.get(blockEntity.emitters.size() - 1)).get(Properties.FACING))) {
                world.setBlockState(
                        pos,
                        PortalCubedBlocks.EXCURSION_FUNNEL.getDefaultState().with(Properties.FACING,
                                world.getBlockState(blockEntity.emitters.get(blockEntity.emitters.size() - 1)).get(Properties.FACING)),
                        3); // here, and with the comma in the line above
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        // Because NbtCompounds only support a few types, we have to decompose the emitter BlockPos' into ints
        tag.putInt("size", emitters.size());
        for (int i = 0; i < emitters.size(); i++) {
            tag.putInt(i + "x", emitters.get(i).getX());
            tag.putInt(i + "y", emitters.get(i).getY());
            tag.putInt(i + "z", emitters.get(i).getZ());
        }

    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        // Because NbtCompounds only support a few types, we have to recompose the emitter BlockPos' from ints
        int size = tag.getInt("size");
        for (int i = 0; i < size; i++) {
            emitters.add(new BlockPos.Mutable(
                    tag.getInt(i + "x"),
                    tag.getInt(i + "y"),
                    tag.getInt(i + "z")
            ));
        }
    }

    @Override
    public void markRemoved() {
        assert world != null;
        if (!world.isClient) {
            if (!emitters.isEmpty()) {
                // Repair callbacks
                emitters.forEach((emitter) -> {
                    ((ExcursionFunnelEmitterEntity) Objects.requireNonNull(world.getBlockEntity(emitter))).repairUpdate(pos);
                });
            }
        }
        super.markRemoved();
    }
}