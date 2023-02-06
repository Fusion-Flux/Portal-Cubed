package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.ToDoubleFunction;

public class VelocityHelperBlockEntity extends BlockEntity {
    private static final List<Operator> OPERATORS = List.of(
        newOp("!", 1, false, Operator.PRECEDENCE_UNARY_MINUS, d -> d[0] == 0 ? 1 : 0),
        newOp("<", 2, true, Operator.PRECEDENCE_ADDITION - 1, d -> d[0] < d[1] ? 1 : 0),
        newOp("<=", 2, true, Operator.PRECEDENCE_ADDITION - 1, d -> d[0] <= d[1] ? 1 : 0),
        newOp(">", 2, true, Operator.PRECEDENCE_ADDITION - 1, d -> d[0] > d[1] ? 1 : 0),
        newOp(">=", 2, true, Operator.PRECEDENCE_ADDITION - 1, d -> d[0] >= d[1] ? 1 : 0),
        newOp("==", 2, true, Operator.PRECEDENCE_ADDITION - 2, d -> d[0] == d[1] ? 1 : 0),
        newOp("!=", 2, true, Operator.PRECEDENCE_ADDITION - 2, d -> d[0] != d[1] ? 1 : 0),
        newOp("&", 2, true, Operator.PRECEDENCE_ADDITION - 3, d -> (d[0] != 0 && d[1] != 0) ? 1 : 0),
        newOp("^", 2, true, Operator.PRECEDENCE_ADDITION - 4, d -> (d[0] != 0 ^ d[1] != 0) ? 1 : 0),
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

    public VelocityHelperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public VelocityHelperBlockEntity(BlockPos pos, BlockState state) {
        this(PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        final int[] destinationData = nbt.getIntArray("Destination");
        if (destinationData.length >= 3) {
            destination = new BlockPos(destinationData[0], destinationData[1], destinationData[2]);
        } else {
            destination = null;
        }
        setInterpolationCurve(nbt.getString("InterpolationCurve"));
        setInterpolationCurve(nbt.getString("Condition"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (destination != null) {
            nbt.put("Destination", new NbtIntArray(new int[] {
                destination.getX(),
                destination.getY(),
                destination.getZ()
            }));
        }
        nbt.putString("InterpolationCurve", interpolationCurveString);
        nbt.putString("Condition", conditionString);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return toNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.of(this);
    }

    @Nullable
    public BlockPos getDestination() {
        return destination;
    }

    public void setDestination(@Nullable BlockPos destination) {
        this.destination = destination;
        updateListeners();
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

    private void updateListeners() {
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    private static Expression parseExpression(String expression, String... variables) {
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
