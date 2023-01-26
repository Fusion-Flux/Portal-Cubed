package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.UUID;

public class RadioEntity extends CorePhysicsEntity  {
    public RadioEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

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
        Vec3d movedPos = getPos().add(0, .3125 / 2, 0);
        if (this.bodyYaw == 0 || this.bodyYaw == 180) {
            return new Box(movedPos.subtract(0.4375 / 2, .3125 / 2, .1875 / 2), movedPos.add(0.4375 / 2, .3125 / 2, .1875 / 2));
        } else {
            return new Box(movedPos.subtract(.1875 / 2, .3125 / 2, 0.4375 / 2), movedPos.add(.1875 / 2, .3125 / 2, 0.4375 / 2));
        }
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if (getCustomName() != null) {
            if (getCustomName().getString().equalsIgnoreCase("exile") || this.getCustomName().getString().equalsIgnoreCase("vilify") || this.getCustomName().getString().equalsIgnoreCase("exile vilify")) {
                MinecraftClient.getInstance().getSoundManager().play(new RadioSoundInstance(PortalCubedSounds.EXILE_MUSIC_EVENT, 0.5f));
            }
        } else {
            MinecraftClient.getInstance().getSoundManager().play(new RadioSoundInstance(PortalCubedSounds.RADIO_MUSIC_EVENT, 0.5f));
        }
    }

    @Override
    public void setHolderUUID(UUID uuid) {
        super.setHolderUUID(uuid);
        if (uuid != null) {
            setSilent(!isSilent());
        }
    }

    @Environment(EnvType.CLIENT)
    private class RadioSoundInstance extends MovingSoundInstance {
        private final float defaultVolume;

        public RadioSoundInstance(SoundEvent event, float volume) {
            super(event, SoundCategory.NEUTRAL, SoundInstance.method_43221());
            this.volume = defaultVolume = volume;
            pitch = 1f;
            repeat = true;
            x = RadioEntity.this.getX();
            y = RadioEntity.this.getY();
            z = RadioEntity.this.getZ();
        }

        @Override
        public void tick() {
            if (isRemoved()) {
                setDone();
                return;
            }
            volume = isSilent() ? 0f : defaultVolume;
            x = RadioEntity.this.getX();
            y = RadioEntity.this.getY();
            z = RadioEntity.this.getZ();
        }
    }
}
