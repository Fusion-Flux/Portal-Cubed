package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;


import java.util.Random;

public class NeurotoxinBlockEntity extends BlockEntity implements Tickable {
    public NeurotoxinBlockEntity() {
        super(ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK_ENTITY);
    }

    @Override
    public void tick() {
        assert this.world != null;
        if(!this.world.isClient) {
            Random random = world.getRandom();
            if (this.world.getBlockState(this.getPos().offset(Direction.random(random))).isAir()) {
                this.world.setBlockState(this.getPos(), Blocks.AIR.getDefaultState());
                this.world.setBlockState(this.getPos().offset(Direction.random(random)), ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK.getDefaultState());
            }
        }
    }
}
