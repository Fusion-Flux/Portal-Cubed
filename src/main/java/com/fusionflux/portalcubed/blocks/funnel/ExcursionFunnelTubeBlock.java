package com.fusionflux.portalcubed.blocks.funnel;

import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.HasMovementInputAccessor;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock.Mode;
import com.fusionflux.portalcubed.blocks.properties.PortalCubedProperties;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.sound.ExcursionFunnelEnterSoundInstance;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.fusionflux.portalcubed.util.TwoByTwo;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;


public class ExcursionFunnelTubeBlock extends Block implements TwoByTwoFacingMultiblockBlock {
    public static final BooleanProperty REVERSED = PortalCubedProperties.REVERSED;

    public ExcursionFunnelTubeBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(QUADRANT, 1)
                        .setValue(FACING, Direction.NORTH)
                        .setValue(REVERSED, false)
        );
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(QUADRANT, FACING, REVERSED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        // this makes the end texture only render on the very end
        if (!adjacentState.is(this) && !adjacentState.is(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER))
            return false;
        Direction facing = state.getValue(FACING);
        if (facing != direction)
            return false;
        Direction adjacentFacing = adjacentState.getValue(FACING);
        return adjacentFacing == facing;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Direction back = state.getValue(FACING).getOpposite();
        BlockState stateBehind = level.getBlockState(pos.relative(back));
        if (!stateBehind.is(this) && !state.is(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER))
            level.removeBlock(pos, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level instanceof ServerLevel serverLevel) || ExcursionFunnelEmitterBlock.suppressUpdates)
            return;
        Direction facing = state.getValue(FACING);
        int quadrant = state.getValue(QUADRANT);
        TwoByTwo funnelTube = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(pos, quadrant, facing);

        // remove rest of adjacent funnel blocks
        ExcursionFunnelEmitterBlock.withUpdatesSuppressed(() -> funnelTube.forEach(part -> {
            if (!part.equals(pos) && level.getBlockState(part).is(this))
                serverLevel.removeBlock(part, false);
        }));
        // remove remaining funnel in front
        Mode newMode = state.getValue(REVERSED) ? Mode.REVERSED_OFF : Mode.FORWARD_OFF;
        ExcursionFunnelEmitterBlock.updateEmissionSuppressed(serverLevel, funnelTube, facing, newMode);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!(level instanceof ServerLevel serverLevel) || ExcursionFunnelEmitterBlock.suppressUpdates)
            return;
        Direction facing = state.getValue(FACING);
        if (!fromPos.equals(pos.relative(facing))) // only care about block in front changing
            return;
        BlockState newState = level.getBlockState(fromPos);
        if (!newState.isAir())
            return;
        // block in front removed. try to extend the funnel
        int quadrant = state.getValue(QUADRANT);
        TwoByTwo funnelTube = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(pos, quadrant, facing);
        Mode newMode = state.getValue(REVERSED) ? Mode.REVERSED_ON : Mode.FORWARD_ON;
        ExcursionFunnelEmitterBlock.updateEmissionSuppressed(serverLevel, funnelTube, facing, newMode);
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        Direction facing = state.getValue(FACING);
        TwoByTwo multiblock = TwoByTwoFacingMultiblockBlock.makeMultiblockFromQuadrant(pos, state.getValue(QUADRANT), facing);
        Direction motion = state.getValue(REVERSED) ? facing.getOpposite() : facing;
        applyEffects(entity, multiblock.getCenter(), motion);
    }

    public static void applyEffects(Entity entity, Vec3 tubeCenter, Direction motionDirection) {
        if (entity instanceof Player player && player.getAbilities().flying)
            return;
        Vec3 entityCenter = entity.getBoundingBox().getCenter();
        Vec3 motion = Vec3.atLowerCornerOf(motionDirection.getNormal()).scale(0.125);

        RayonIntegration.INSTANCE.setNoGravity(entity, true);
        entity.resetFallDistance();

        EntityExt entityEx = (EntityExt) entity;
        if (!entityEx.isInFunnel()) {
            entityEx.setInFunnel(true);
            entity.setDeltaMovement(0, 0, 0);
            if (entity instanceof Player player && player.isLocalPlayer())
                playEnterSound();
        }
        entityEx.setFunnelTimer(2);

        Vec3 velocity = entity.getDeltaMovement();
        // check for inputs
        if (entity instanceof HasMovementInputAccessor inputProvider && inputProvider.hasMovementInputPublic()) {
            if (motion.x == 0)
                motion = motion.add(velocity.x, 0, 0);
            if (motion.y == 0)
                motion = motion.add(0, velocity.y, 0);
            if (motion.z == 0)
                motion = motion.add(0, 0, velocity.z);
        }

        // move entity towards center
        double dx = entityCenter.x - tubeCenter.x + velocity.x;
        double dy = entityCenter.y - tubeCenter.y + velocity.y;
        double dz = entityCenter.z - tubeCenter.z + velocity.z;

        if (motion.x == 0)
            motion = motion.add(-Math.copySign(Math.sqrt(Math.abs(dx)), dx) / 20, 0, 0);
        if (motion.y == 0)
            motion = motion.add(0, -Math.copySign(Math.sqrt(Math.abs(dy)), dy) / 20, 0);
        if (motion.z == 0)
            motion = motion.add(0, 0, -Math.copySign(Math.sqrt(Math.abs(dz)), dz) / 20);

        entity.setDeltaMovement(motion);

        if (entity.isShiftKeyDown() && motion.lengthSqr() < 0.15 * 0.15 && !entity.isFree(motion.x, motion.y, motion.z)) {
            entityEx.setCFG();
        }
    }

    @ClientOnly
    private static void playEnterSound() {
        Minecraft.getInstance().getSoundManager().play(new ExcursionFunnelEnterSoundInstance());
    }
}
