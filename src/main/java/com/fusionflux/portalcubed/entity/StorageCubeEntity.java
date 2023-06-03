package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class StorageCubeEntity extends CorePhysicsEntity  {

    public StorageCubeEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            boolean bl = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
            if (source.getEntity() instanceof Player || source == DamageSource.OUT_OF_WORLD) {
                if (source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().mayBuild) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.STORAGE_CUBE);
                    }
                    this.discard();
                }
                if (!(source.getEntity() instanceof Player)) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.STORAGE_CUBE);
                    }
                    this.discard();
                }
            }

        }
        return false;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(PortalCubedSounds.CUBE_LOW_HIT_EVENT, PortalCubedSounds.CUBE_HIGH_HIT_EVENT);
    }

    private int buttonTimer = 0;

    public void setButtonTimer(int time) {
        buttonTimer = time;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (buttonTimer <= 0) {
                setOnButton(false);
            } else {
                setOnButton(true);
                buttonTimer -= 1;
            }
        }
    }

}
