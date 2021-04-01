package com.fusionflux.thinkingwithportatos.blocks.blockentities;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class NeurotoxinBlock extends BlockWithEntity {
    public NeurotoxinBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new NeurotoxinBlockEntity();
    }
}
