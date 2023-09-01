package com.fusionflux.portalcubed.fluids;

import com.fusionflux.portalcubed.entity.Fizzleable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import static com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources.pcSources;

public abstract class ToxicGooFluid extends FlowingFluid {
	@NotNull
	@Override
	public Fluid getSource() {
		return PortalCubedFluids.TOXIC_GOO.still;
	}

	@NotNull
	@Override
	public Fluid getFlowing() {
		return PortalCubedFluids.TOXIC_GOO.flowing;
	}

	@NotNull
	@Override
	public Item getBucket() {
		return PortalCubedFluids.TOXIC_GOO.bucket;
	}

	@NotNull
	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return PortalCubedFluids.TOXIC_GOO.getBlock().defaultBlockState().setValue(BlockStateProperties.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == getSource() || fluid == getFlowing();
	}

	@Override
	protected boolean canConvertToSource(Level level) {
		return true;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState state) {
		Block.dropResources(state, world, pos, state.hasBlockEntity() ? world.getBlockEntity(pos) : null);
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos, Fluid fluid,
			Direction direction) {
		return false;
	}

	@Override
	protected int getSlopeFindDistance(LevelReader world) {
		return 4;
	}

	@Override
	protected int getDropOff(LevelReader world) {
		return 2;
	}

	@Override
	public int getTickDelay(LevelReader world) {
		return 20;
	}

	@Override
	protected float getExplosionResistance() {
		return 100;
	}

	public static class Flowing extends ToxicGooFluid {
		@Override
		protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
			builder.add(FALLING, LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Still extends ToxicGooFluid {
		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Block extends LiquidBlock {
		public Block(FlowingFluid flowableFluid, Properties settings) {
			super(flowableFluid, settings);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
			if (!entity.isAlive()) return;
			if (entity instanceof Fizzleable fizzleable) {
				if (!level.isClientSide && fizzleable.fizzlesInGoo()) {
					fizzleable.fizzle();
				}
			} else {
				entity.hurt(pcSources(level).acid(), level.getRandom().nextIntBetweenInclusive(7, 10));
			}
		}
	}
}
