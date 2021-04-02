package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class NeurotoxinBlockEntity extends BlockEntity implements Tickable {

    private int age = 1;

    public NeurotoxinBlockEntity() {
        super(ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK_ENTITY);
    }



    @Override
    public void tick() {
        assert this.world != null;
        if(!this.world.isClient){
            age++;
        }
        if(!this.world.isClient&&this.age % 5 == 0) {
            Direction dir = Direction.random(world.getRandom());
            if (this.world.getBlockState(this.getPos().offset(dir)).isAir()) {
                this.world.setBlockState(this.getPos(), Blocks.AIR.getDefaultState());
                this.world.setBlockState(this.getPos().offset(dir), ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK.getDefaultState());
            }
        }
    }
}
