package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NeurotoxinBlockEntity extends BlockEntity {

    private int age = 1;

    public NeurotoxinBlockEntity(BlockPos pos, BlockState state) {
        super(ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK_ENTITY,pos,state);
    }


    public static void tick(World world, BlockPos pos, BlockState state, NeurotoxinBlockEntity blockEntity) {
        assert blockEntity.world != null;
        if (!blockEntity.world.isClient) {
            blockEntity.age++;
        }
        if (!blockEntity.world.isClient && blockEntity.age % 5 == 0) {
            Direction dir = Direction.random(world.getRandom());
            if (blockEntity.world.getBlockState(blockEntity.getPos().offset(dir)).isAir()) {
                blockEntity.world.setBlockState(blockEntity.getPos().offset(dir), blockEntity.getCachedState());
                blockEntity.world.setBlockState(blockEntity.getPos(), Blocks.AIR.getDefaultState());
            }
        }
        /*if (this.age >= 100) {
            this.world.setBlockState(this.getPos(), Blocks.AIR.getDefaultState());
        }*/
    }
}
