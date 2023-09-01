package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.gui.BlockPosScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.ToDoubleFunction;

public class VelocityHelperBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
	private static final List<Operator> OPERATORS = List.of(
		newOp("!", 1, false, Operator.PRECEDENCE_UNARY_MINUS, d -> d[0] == 0 ? 1 : 0),
		newOp("<", 2, true, Operator.PRECEDENCE_ADDITION - 1, d -> d[0] < d[1] ? 1 : 0),
		newOp("<=", 2, true, Operator.PRECEDENCE_ADDITION - 1, d -> d[0] <= d[1] ? 1 : 0),
		newOp(">", 2, true, Operator.PRECEDENCE_ADDITION - 1, d -> d[0] > d[1] ? 1 : 0),
		newOp(">=", 2, true, Operator.PRECEDENCE_ADDITION - 1, d -> d[0] >= d[1] ? 1 : 0),
		newOp("==", 2, true, Operator.PRECEDENCE_ADDITION - 2, d -> d[0] == d[1] ? 1 : 0),
		newOp("!=", 2, true, Operator.PRECEDENCE_ADDITION - 2, d -> d[0] != d[1] ? 1 : 0),
		newOp("&", 2, true, Operator.PRECEDENCE_ADDITION - 3, d -> (d[0] != 0 && d[1] != 0) ? 1 : 0),
		newOp("^", 2, true, Operator.PRECEDENCE_ADDITION - 4, d -> Math.pow(d[0], d[1])),
		newOp("|", 2, true, Operator.PRECEDENCE_ADDITION - 5, d -> (d[0] != 0 | d[1] != 0) ? 1 : 0)
	);

	private static final String DEFAULT_IC_STRING = "x";
	private static final Expression DEFAULT_IC = parseExpression(DEFAULT_IC_STRING, "x");

	private static final String DEFAULT_CONDITION_STRING = "1";
	private static final Expression DEFAULT_CONDITION = parseExpression(DEFAULT_CONDITION_STRING, "x", "y", "z");

	@Nullable
	private BlockPos destination = null;

	private String interpolationCurveString = DEFAULT_IC_STRING;
	private Expression interpolationCurve = DEFAULT_IC;

	private String conditionString = DEFAULT_CONDITION_STRING;
	private Expression condition = DEFAULT_CONDITION;

	private int flightDuration = 40;

	public VelocityHelperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public VelocityHelperBlockEntity(BlockPos pos, BlockState state) {
		this(PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY, pos, state);
	}

	@Override
	public void load(CompoundTag nbt) {
		final int[] destinationData = nbt.getIntArray("Destination");
		if (destinationData.length >= 3) {
			destination = new BlockPos(destinationData[0], destinationData[1], destinationData[2]);
		} else {
			destination = null;
		}
		setInterpolationCurve(nbt.getString("InterpolationCurve"));
		setCondition(nbt.getString("Condition"));
		flightDuration = nbt.getInt("FlightDuration");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		if (destination != null) {
			nbt.put("Destination", new IntArrayTag(new int[] {
				destination.getX(),
				destination.getY(),
				destination.getZ()
			}));
		}
		nbt.putString("InterpolationCurve", interpolationCurveString);
		nbt.putString("Condition", conditionString);
		nbt.putInt("FlightDuration", flightDuration);
	}

	@NotNull
	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Nullable
	public BlockPos getDestination() {
		return destination;
	}

	public void setDestination(@Nullable BlockPos destination) {
		this.destination = destination;
	}

	public Expression getInterpolationCurve() {
		return interpolationCurve;
	}

	public String getInterpolationCurveString() {
		return interpolationCurveString;
	}

	public void setInterpolationCurve(String curve) {
		final boolean reparse = !interpolationCurveString.equals(curve);
		interpolationCurveString = curve;
		if (reparse) {
			interpolationCurve = parseExpression(curve, "x");
		}
	}

	public Expression getCondition() {
		return condition;
	}

	public String getConditionString() {
		return conditionString;
	}

	public void setCondition(String condition) {
		final boolean reparse = !conditionString.equals(condition);
		conditionString = condition;
		if (reparse) {
			this.condition = parseExpression(condition, "x", "y", "z");
		}
	}

	public int getFlightDuration() {
		return flightDuration;
	}

	public void setFlightDuration(int flightDuration) {
		this.flightDuration = flightDuration;
	}

	public void updateListeners() {
		setChanged();
		assert level != null;
		level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return Component.translatable(getBlockState().getBlock().getDescriptionId());
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player playerEntity) {
		return new BlockPosScreenHandler(PortalCubed.VELOCITY_HELPER_SCREEN_HANDLER, syncId, getBlockPos());
	}

	@Override
	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeBlockPos(getBlockPos());
	}

	public static Expression parseExpression(String expression, String... variables) {
		return new ExpressionBuilder(expression)
			.operator(OPERATORS)
			.variables(variables)
			.build();
	}

	private static Operator newOp(String symbol, int numberOfOperands, boolean leftAssociative, int precedence, ToDoubleFunction<double[]> op) {
		return new Operator(symbol, numberOfOperands, leftAssociative, precedence) {
			@Override
			public double apply(double... args) {
				return op.applyAsDouble(args);
			}
		};
	}
}
