package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.properties.FluidType;
import com.fusionflux.portalcubed.blocks.properties.PortalCubedProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface SimpleLoggedBlock extends BucketPickupEx, LiquidBlockContainer {
	@Override
	default boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		final Fluid existingFluid = state.getValue(PortalCubedProperties.LOGGING).fluid;
		return existingFluid == Fluids.EMPTY || fluid == existingFluid;
	}

	@Override
	default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		if (state.getValue(PortalCubedProperties.LOGGING) != FluidType.EMPTY) {
			return false;
		}
		if (!level.isClientSide()) {
			final FluidType fluidType = FluidType.getByFluid(fluidState.getType());
			if (fluidType == null) {
				return false;
			}
			level.setBlock(pos, state.setValue(PortalCubedProperties.LOGGING, fluidType), Block.UPDATE_ALL);
			level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
		}
		return true;
	}

	@NotNull
	@Override
	default ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		final Fluid fluid = state.getValue(PortalCubedProperties.LOGGING).fluid;
		if (fluid == Fluids.EMPTY) {
			return ItemStack.EMPTY;
		}
		level.setBlock(pos, state.setValue(PortalCubedProperties.LOGGING, FluidType.EMPTY), Block.UPDATE_ALL);
		if (!state.canSurvive(level, pos)) {
			level.destroyBlock(pos, true);
		}
		return new ItemStack(fluid.getBucket());
	}

	@NotNull
	@Override
	default Optional<SoundEvent> getPickupSound(BlockState state) {
		return state.getValue(PortalCubedProperties.LOGGING).fluid.getPickupSound();
	}
}
