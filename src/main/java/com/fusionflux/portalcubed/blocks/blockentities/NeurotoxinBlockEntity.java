package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NeurotoxinBlockEntity extends BlockEntity {
    private int age = 1;

    public NeurotoxinBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.NEUROTOXIN_BLOCK_ENTITY,pos,state);
    }

    public static void tick(World world, BlockPos pos, @SuppressWarnings("unused") BlockState state, NeurotoxinBlockEntity blockEntity) {
        assert world != null;

        if (!world.isClient) {
            blockEntity.age++;
        }

        if (!world.isClient && blockEntity.age % 5 == 0) {
            for (int i = 0; i < 3; i++) {
                Direction dir = Direction.random(world.getRandom());
                if (world.getBlockState(blockEntity.getPos().offset(dir)).isAir()) {
                    world.setBlockState(blockEntity.getPos().offset(dir), blockEntity.getCachedState());
                    world.setBlockState(blockEntity.getPos(), Blocks.AIR.getDefaultState());
                    if (world.getBlockEntity(pos.offset(dir)) instanceof NeurotoxinBlockEntity newBE) {
                        newBE.age = blockEntity.age;
                    }
                    break;
                }
            }
        }

        if (world.random.nextInt(blockEntity.age) > 100)
            world.setBlockState(blockEntity.getPos(), Blocks.AIR.getDefaultState());
    }
}
