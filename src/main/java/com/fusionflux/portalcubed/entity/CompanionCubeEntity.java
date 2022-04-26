package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class CompanionCubeEntity extends StorageCubeEntity  {
    public CompanionCubeEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    private float storedDamage = 0.0F;

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isClient && !this.isRemoved()) {
            this.storedDamage += amount;
            //this.scheduleVelocityUpdate();
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (bl || this.storedDamage >= 20.0F) {
                if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    // TODO
                    this.dropItem(PortalCubedItems.COMPANION_CUBE);
                }

                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }
}
