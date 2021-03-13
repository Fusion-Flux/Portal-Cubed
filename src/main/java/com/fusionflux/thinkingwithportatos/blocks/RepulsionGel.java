package com.fusionflux.thinkingwithportatos.blocks;

import com.fusionflux.thinkingwithportatos.entity.BlockCollisionLimiter;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RepulsionGel extends Gel {

    private final BlockCollisionLimiter limiter = new BlockCollisionLimiter();

    public RepulsionGel(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
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
                    if (entity.getVelocity().y < 1.65)
                        entity.setVelocity(entity.getVelocity().add(0, 1.65D, 0));
                    if (limiter.check(world, entity)) {
                        entity.playSound(ThinkingWithPortatosSounds.GEL_BOUNCE_EVENT, .4F, 1F);
                    }
                }
            }
        }
    }

}
