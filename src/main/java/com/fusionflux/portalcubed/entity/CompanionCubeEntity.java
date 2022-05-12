package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
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

    private int t = 1500;

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) && !(source.getAttacker() instanceof PlayerEntity)) {
            return false;
        } else if (!this.world.isClient && !this.isRemoved()) {
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (source.getAttacker() instanceof PlayerEntity) {
                if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                    this.dropItem(PortalCubedItems.COMPANION_CUBE);
                }
                this.discard();
            }
            return true;
        } else {
            return true;
        }
    }


    @Override
    public void tick() {
        if (!this.world.isClient) {
            if (t == 1500) {
                world.playSoundFromEntity(null,this, PortalCubedSounds.COMPANION_CUBE_AMBIANCE_EVENT,this.getSoundCategory(),1f,1f);
            }
            t--;
            if (t == 0) {
                t = 1500;
            }

        }
        super.tick();
    }

}
