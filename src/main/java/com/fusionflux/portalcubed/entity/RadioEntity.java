package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.UUID;

public class RadioEntity extends CorePhysicsEntity  {
    private static final TrackedData<Boolean> NOT_PLAYING = DataTracker.registerData(RadioEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private SoundEvent song = PortalCubedSounds.RADIO_MUSIC_EVENT;
    private SoundEvent lastSong = song;

    public RadioEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(NOT_PLAYING, false);
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

    public boolean isNotPlaying() {
        return dataTracker.get(NOT_PLAYING);
    }

    public void setNotPlaying(boolean notPlaying) {
        dataTracker.set(NOT_PLAYING, notPlaying);
    }

    private void performPlay() {
        MinecraftClient.getInstance().getSoundManager().play(new RadioSoundInstance(song));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        performPlay();
    }

    @Override
    public void setHolderUUID(UUID uuid) {
        super.setHolderUUID(uuid);
        if (uuid != null) {
            setNotPlaying(!isNotPlaying());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (getCustomName() != null) {
            if (getCustomName().getString().equalsIgnoreCase("exile") || this.getCustomName().getString().equalsIgnoreCase("vilify") || this.getCustomName().getString().equalsIgnoreCase("exile vilify")) {
                song = PortalCubedSounds.EXILE_MUSIC_EVENT;
            }
        } else {
            song = PortalCubedSounds.RADIO_MUSIC_EVENT;
        }
        if (song != lastSong) {
            lastSong = song;
            performPlay();
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("NotPlaying", isNotPlaying());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setNotPlaying(nbt.getBoolean("NotPlaying"));
    }

    @ClientOnly
    private class RadioSoundInstance extends MovingSoundInstance {
        private final SoundEvent song;

        public RadioSoundInstance(SoundEvent song) {
            super(song, SoundCategory.RECORDS, SoundInstance.method_43221());
            this.song = song;
            volume = 1f;
            pitch = 1f;
            repeat = true;
            x = RadioEntity.this.getX();
            y = RadioEntity.this.getY();
            z = RadioEntity.this.getZ();
        }

        @Override
        public void tick() {
            if (isRemoved() || song != RadioEntity.this.song) {
                setDone();
                return;
            }
            volume = isNotPlaying() ? 0f : 1f;
            x = RadioEntity.this.getX();
            y = RadioEntity.this.getY();
            z = RadioEntity.this.getZ();
        }
    }
}
