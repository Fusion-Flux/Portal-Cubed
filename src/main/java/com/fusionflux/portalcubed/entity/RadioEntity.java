package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class RadioEntity extends CorePhysicsEntity  {
    public RadioEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    private int t = 0;

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
            if (this.getCustomName() != null) {
                if (Objects.equals(this.getCustomName().getString().toLowerCase(Locale.ROOT), "exile")||Objects.equals(this.getCustomName().getString().toLowerCase(Locale.ROOT), "vilify")||Objects.equals(this.getCustomName().getString().toLowerCase(Locale.ROOT), "exile vilify")) {
                    t--;
                    if (t <= 0) {
                        world.playSoundFromEntity(null, this, PortalCubedSounds.EXILE_MUSIC_EVENT, this.getSoundCategory(), .5f, 1f);
                        t = 5390;
                    }
                }else{
                    t--;
                    if (t <= 0) {
                        world.playSoundFromEntity(null, this, PortalCubedSounds.RADIO_MUSIC_EVENT, this.getSoundCategory(), 1f, 1f);

                        t = 6600;
                    }
                }
            } else {
                t--;
                if (t <= 0) {
                    world.playSoundFromEntity(null, this, PortalCubedSounds.RADIO_MUSIC_EVENT, this.getSoundCategory(), 1f, 1f);
                    t = 6600;
                }
            }
        }
        super.tick();
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        t = 0;
    }
}
