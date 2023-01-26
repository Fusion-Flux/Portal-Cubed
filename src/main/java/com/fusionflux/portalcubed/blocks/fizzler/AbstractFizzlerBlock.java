package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.UUID;

public abstract class AbstractFizzlerBlock extends Block {
    public static final BooleanProperty NS = BooleanProperty.of("ns");
    public static final BooleanProperty EW = BooleanProperty.of("ew");

    public AbstractFizzlerBlock(Settings settings) {
        super(settings);
        setDefaultState(
            getStateManager().getDefaultState()
                .with(NS, false)
                .with(EW, false)
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
        builder.add(NS, EW);
    }

    protected final void removePortals(Entity entity) {
        if (entity.world.isClient) return;
        for (final UUID portal : CalledValues.getPortals(entity)) {
            final Entity checkPortal = ((ServerWorld)entity.world).getEntity(portal);
            if (checkPortal != null) {
                checkPortal.kill();
            }
        }
        if (entity instanceof PlayerEntity player) {
            player.playSound(PortalCubedSounds.ENTITY_PORTAL_FIZZLE, SoundCategory.NEUTRAL, 0.5f, 1f);
        }
    }

    protected final void fizzlePhysicsEntity(Entity entity) {
        if (entity.world.isClient) return;
        if (entity instanceof CorePhysicsEntity physicsEntity) {
            physicsEntity.fizzle();
        }
    }

    protected final void fizzlePlayer(Entity entity) {
        if (entity.world.isClient) return;
        if (entity instanceof PlayerEntity) {
            entity.damage(PortalCubedDamageSources.FIZZLE, PortalCubedConfig.fizzlerDamage);
        }
    }
}
