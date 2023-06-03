package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class SpaceCoreEntity extends CorePhysicsEntity {

    public SpaceCoreEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    private int t = 0;

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            boolean bl = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
            if (source.getEntity() instanceof Player || source == DamageSource.OUT_OF_WORLD) {
                if (source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().mayBuild) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.SPACE_CORE);
                    }
                    this.discard();
                }
                if (!(source.getEntity() instanceof Player)) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.SPACE_CORE);
                    }
                    this.discard();
                }
            }

        }
        return false;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (t == 0) {
                level.playSound(null, this, PortalCubedSounds.SPACE_CORE_EVENT, this.getSoundSource(), 1f, 1f);
                t = 2765;
            }
            t--;
        }
        super.tick();
    }
}
