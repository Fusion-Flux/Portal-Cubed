package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.Fizzleable;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.UUID;

import static com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources.pcSources;

public abstract class AbstractFizzlerBlock extends Block {
	public static final BooleanProperty NS = BooleanProperty.create("ns");
	public static final BooleanProperty EW = BooleanProperty.create("ew");
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	private static final VoxelShape NS_SHAPE = box(7.5, 0, 0, 8.5, 16, 16);
	private static final VoxelShape EW_SHAPE = box(0, 0, 7.5, 16, 16, 8.5);
	private static final VoxelShape BOTH_SHAPE = Shapes.or(NS_SHAPE, EW_SHAPE);

	public AbstractFizzlerBlock(Properties settings) {
		super(settings);
		registerDefaultState(
			getStateDefinition().any()
				.setValue(NS, false)
				.setValue(EW, false)
				.setValue(HALF, DoubleBlockHalf.LOWER)
		);
	}

	public static BooleanProperty getStateForAxis(Direction.Axis axis) {
		return axis == Direction.Axis.Z ? NS : EW;
	}

	public static boolean isEmpty(BlockState state) {
		return !state.getValue(NS) && !state.getValue(EW);
	}

	@NotNull
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		if (state.getValue(NS) && state.getValue(EW)) {
			return BOTH_SHAPE;
		}
		if (state.getValue(NS)) {
			return NS_SHAPE;
		}
		if (state.getValue(EW)) {
			return EW_SHAPE;
		}
		return Shapes.empty();
	}

	@Override
	@SuppressWarnings("deprecation")
	public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
		return 1;
	}

	@Override
	public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean skipRendering(@NotNull BlockState state, BlockState stateFrom, @NotNull Direction direction) {
		return stateFrom.is(this) || super.skipRendering(state, stateFrom, direction);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NS, EW, HALF);
	}

	public abstract void applyEffectsTo(Entity entity);

	protected final void fizzlePortals(Entity entity) {
		if (entity.level().isClientSide) return;
		boolean foundPortal = false;
		for (final UUID portal : List.copyOf(CalledValues.getPortals(entity))) {
			final Entity checkPortal = ((ServerLevel)entity.level()).getEntity(portal);
			if (checkPortal != null) {
				foundPortal = true;
				checkPortal.kill();
			}
		}
		if (foundPortal && entity instanceof ServerPlayer player) {
			player.playNotifySound(PortalCubedSounds.ENTITY_PORTAL_FIZZLE, SoundSource.NEUTRAL, 0.5f, 1f);
			ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
		}
	}

	protected final void fizzlePhysicsEntity(Entity entity) {
		if (entity.level().isClientSide) return;
		if (entity instanceof Fizzleable fizzleable && fizzleable.getFizzleType() == Fizzleable.FizzleType.OBJECT) {
			fizzleable.fizzle();
		}
	}

	protected final void fizzleLiving(Entity entity) {
		if (entity.level().isClientSide) return;
		// TODO: Fizzle players visually?
		if (entity instanceof Fizzleable fizzleable ? fizzleable.getFizzleType() == Fizzleable.FizzleType.LIVING : entity instanceof LivingEntity) {
			entity.hurt(pcSources(entity.level()).fizzle(), PortalCubedConfig.fizzlerDamage);
			if (entity instanceof Fizzleable fizzleable && fizzleable.getFizzleType() != Fizzleable.FizzleType.NOT) {
				fizzleable.fizzle();
			}
		}
	}
}
