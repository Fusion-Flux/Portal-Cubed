package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.Fizzleable;
import com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.UUID;

public abstract class AbstractFizzlerBlock extends Block implements BlockCollisionTrigger {
    public static final BooleanProperty NS = BooleanProperty.of("ns");
    public static final BooleanProperty EW = BooleanProperty.of("ew");
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    private static final VoxelShape NS_SHAPE = createCuboidShape(7.5, 0, 0, 8.5, 16, 16);
    private static final VoxelShape EW_SHAPE = createCuboidShape(0, 0, 7.5, 16, 16, 8.5);
    private static final VoxelShape BOTH_SHAPE = VoxelShapes.union(NS_SHAPE, EW_SHAPE);

    public AbstractFizzlerBlock(Settings settings) {
        super(settings);
        setDefaultState(
            getStateManager().getDefaultState()
                .with(NS, false)
                .with(EW, false)
                .with(HALF, DoubleBlockHalf.LOWER)
        );
    }

    public static BooleanProperty getStateForAxis(Direction.Axis axis) {
        return axis == Direction.Axis.Z ? NS : EW;
    }

    public static boolean isEmpty(BlockState state) {
        return !state.get(NS) && !state.get(EW);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(NS) && state.get(EW)) {
            return BOTH_SHAPE;
        }
        if (state.get(NS)) {
            return NS_SHAPE;
        }
        if (state.get(EW)) {
            return EW_SHAPE;
        }
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getTriggerShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NS, EW, HALF);
    }

    protected final void fizzlePortals(Entity entity) {
        if (entity.world.isClient) return;
        boolean foundPortal = false;
        for (final UUID portal : List.copyOf(CalledValues.getPortals(entity))) {
            final Entity checkPortal = ((ServerWorld)entity.world).getEntity(portal);
            if (checkPortal != null) {
                foundPortal = true;
                checkPortal.kill();
            }
        }
        if (foundPortal && entity instanceof ServerPlayerEntity player) {
            player.playSound(PortalCubedSounds.ENTITY_PORTAL_FIZZLE, SoundCategory.NEUTRAL, 0.5f, 1f);
            ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
        }
    }

    protected final void fizzlePhysicsEntity(Entity entity) {
        if (entity.world.isClient) return;
        if (entity instanceof Fizzleable fizzleable) {
            fizzleable.fizzle();
        }
    }

    protected final void fizzleLiving(Entity entity) {
        if (entity.world.isClient) return;
        // TODO: Fizzle players visually?
        if (entity instanceof LivingEntity && !(entity instanceof Fizzleable)) {
            entity.damage(PortalCubedDamageSources.FIZZLE, PortalCubedConfig.fizzlerDamage);
        }
    }
}
