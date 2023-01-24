package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;

public class AutoPortalBlockEntity extends BlockEntity {
    private int color = 0x1d86db;

    public AutoPortalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public AutoPortalBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(PortalCubedBlocks.AUTO_PORTAL_BLOCK_ENTITY, blockPos, blockState);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        color = nbt.contains("Color", NbtElement.INT_TYPE) ? nbt.getInt("Color") : 0x1d86db;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Color", color);
    }
}
