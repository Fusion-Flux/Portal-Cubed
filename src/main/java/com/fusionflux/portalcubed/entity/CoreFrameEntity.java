package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class CoreFrameEntity extends CorePhysicsEntity {

    public CoreFrameEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            boolean bl = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
            if (source.getEntity() instanceof Player || source == DamageSource.OUT_OF_WORLD) {
                if (source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().mayBuild) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.CORE_FRAME);
                    }
                    this.discard();
                }
                if (!(source.getEntity() instanceof Player)) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.CORE_FRAME);
                    }
                    this.discard();
                }
            }

        }
        return false;
    }

}
