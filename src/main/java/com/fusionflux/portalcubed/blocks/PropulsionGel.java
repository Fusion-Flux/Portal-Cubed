package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.entity.BlockCollisionLimiter;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PropulsionGel extends GelFlat {

    private final BlockCollisionLimiter limiter = new BlockCollisionLimiter();

    public PropulsionGel(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity);
    }


    private void addCollisionEffects(World world, Entity entity) {
        if (entity.getType().equals(EntityType.BOAT)) {
            entity.damage(DamageSource.MAGIC, 200);
        } else {
            if (entity.isOnGround()) {
                if (!entity.isSneaking()) {
                    if (limiter.check(world, entity)) {
                        if (Math.abs(entity.getVelocity().x) < 2 && Math.abs(entity.getVelocity().z) < 2) {
                            entity.setVelocity(entity.getVelocity().multiply(1.7, 1.0D, 1.7));
                        } else if (Math.abs(entity.getVelocity().x) > 2 && Math.abs(entity.getVelocity().z) > 2) {
                            entity.setVelocity(entity.getVelocity().multiply(1.01, 1.0D, 1.01));
                        }
                        if (((EntityAttachments) entity).getMaxFallSpeed() == 0) {
                            world.playSound(null, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), PortalCubedSounds.GEL_RUN_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                        }
                        ((EntityAttachments) entity).setMaxFallSpeed(10);
                    }
                }
            }
        }
    }
    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
