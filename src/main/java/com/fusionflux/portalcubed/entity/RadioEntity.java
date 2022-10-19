package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class RadioEntity extends CorePhysicsEntity  {
    public RadioEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    private int t = 3084;

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.isRemoved()) {
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (source.getAttacker() instanceof PlayerEntity || source == DamageSource.OUT_OF_WORLD) {
                if(source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().allowModifyWorld){
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                        this.dropItem(PortalCubedItems.RADIO);
                    }
                    this.discard();
                }
                if(!(source.getAttacker() instanceof PlayerEntity)) {
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                        this.dropItem(PortalCubedItems.RADIO);
                    }
                    this.discard();
                }
            }

        }
        return false;
    }

    @Override
    protected Box calculateBoundingBox() {
        Vec3d movedPos = getPos().add(0,.3125/2,0);
        if(this.bodyYaw == 0 || this.bodyYaw == 180) {
            return new Box(movedPos.subtract(0.4375 / 2, .3125 / 2, .1875 / 2), movedPos.add(0.4375 / 2, .3125 / 2, .1875 / 2));
        }else{
            return new Box(movedPos.subtract(.1875 / 2, .3125 / 2, 0.4375 / 2), movedPos.add(.1875 / 2, .3125 / 2, 0.4375 / 2));
        }
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            if (t == 3084) {
                world.playSoundFromEntity(null,this, PortalCubedSounds.RADIO_MUSIC_EVENT,this.getSoundCategory(),1f,1f);
            }
            t--;
            if (t == 0) {
                t = 3084;
            }

        }
        super.tick();
    }

}