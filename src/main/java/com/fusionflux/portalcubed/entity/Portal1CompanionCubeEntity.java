package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources.pcSources;

public class Portal1CompanionCubeEntity extends CorePhysicsEntity  {
    public Portal1CompanionCubeEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            boolean bl = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
            if (source.getEntity() instanceof Player || source == damageSources().outOfWorld()) {
                if (source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().mayBuild) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.PORTAL_1_COMPANION_CUBE);
                    }
                    this.discard();
                }
                if (!(source.getEntity() instanceof Player)) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.PORTAL_1_COMPANION_CUBE);
                    }
                    this.discard();
                }
            }

        }
        return false;
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        if (onGround && landedState.isAir()) {
            final List<Entity> collisions = level.getEntitiesOfClass(Entity.class, getBoundingBox().expandTowards(0, -0.1, 0), this::canCollideWith);
            for (final Entity collision : collisions) {
                collision.causeFallDamage(fallDistance + 3, 1.5f, pcSources(level).cube());
            }
        }
        super.checkFallDamage(heightDifference, onGround, landedState, landedPosition);
    }


}
