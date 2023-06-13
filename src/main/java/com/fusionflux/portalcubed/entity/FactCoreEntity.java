package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class FactCoreEntity extends CorePhysicsEntity  {

    public FactCoreEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    private int t = 0;

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (t == 0) {
                level.playSound(null, this, PortalCubedSounds.FACT_CORE_EVENT, this.getSoundSource(), 1f, 1f);
                t = 5500;
            }
            t--;
        }
        super.tick();
    }
}
