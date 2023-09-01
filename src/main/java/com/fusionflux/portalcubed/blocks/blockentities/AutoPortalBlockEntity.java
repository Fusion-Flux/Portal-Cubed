package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
	public void load(CompoundTag nbt) {
		color = nbt.contains("Color", Tag.TAG_INT) ? nbt.getInt("Color") : 0x1d86db;
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		nbt.putInt("Color", color);
	}
}
