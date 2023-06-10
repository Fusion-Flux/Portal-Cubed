package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.entity.BlockCollisionLimiter;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PropulsionGel extends BaseGel {
    private final BlockCollisionLimiter limiter = new BlockCollisionLimiter();

    public PropulsionGel(Properties settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity);
    }

    private void addCollisionEffects(Level level, Entity entity) {
        if (entity.getType().equals(EntityType.BOAT)) {
            entity.hurt(level.damageSources().magic(), 200);
        } else {
            if (entity.isOnGround()) {
                if (!entity.isShiftKeyDown()) {
                    if (limiter.check(level, entity)) {
                        if (Math.abs(entity.getDeltaMovement().x) < 2 && Math.abs(entity.getDeltaMovement().z) < 2) {
                            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.7, 1.0D, 1.7));
                        } else if (Math.abs(entity.getDeltaMovement().x) > 2 && Math.abs(entity.getDeltaMovement().z) > 2) {
                            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.01, 1.0D, 1.01));
                        }
                        if (((EntityAttachments) entity).getMaxFallSpeed() == 0) {
                            level.playSound(null, entity.position().x(), entity.position().y(), entity.position().z(), PortalCubedSounds.GEL_RUN_EVENT, SoundSource.NEUTRAL, .3F, 1F);
                        }
                        ((EntityAttachments) entity).setMaxFallSpeed(10);
                    }
                }
            }
        }
    }
}
