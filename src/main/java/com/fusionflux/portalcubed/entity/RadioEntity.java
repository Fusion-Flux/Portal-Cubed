package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.GeneralUtil;
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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.Optional;
import java.util.UUID;

public class RadioEntity extends CorePhysicsEntity  {
    private static final Box BASE_BOX = createFootBox(0.4375, 0.3125, 0.1875);

    private static final TrackedData<Boolean> MUTED = DataTracker.registerData(RadioEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ALLOW_MUTE = DataTracker.registerData(RadioEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Nullable
    private SoundEvent song = PortalCubedSounds.RADIO_MUSIC_EVENT;
    @Nullable
    private SoundEvent lastSong = song;

    public RadioEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(MUTED, false);
        dataTracker.startTracking(ALLOW_MUTE, true);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.isRemoved()) {
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (source.getAttacker() instanceof PlayerEntity || source == DamageSource.OUT_OF_WORLD) {
                if (source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().allowModifyWorld) {
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                        this.dropItem(PortalCubedItems.RADIO);
                    }
                    this.discard();
                }
                if (!(source.getAttacker() instanceof PlayerEntity)) {
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
        return GeneralUtil.rotate(BASE_BOX, headYaw, Direction.Axis.Y).offset(getPos());
    }

    public boolean isMuted() {
        return dataTracker.get(MUTED);
    }

    public void setMuted(boolean notPlaying) {
        dataTracker.set(MUTED, notPlaying);
    }

    public boolean isAllowMute() {
        return dataTracker.get(ALLOW_MUTE);
    }

    public void setAllowMute(boolean allowMute) {
        dataTracker.set(ALLOW_MUTE, allowMute);
    }

    @ClientOnly
    private void performPlay() {
        if (song == null) return;
        MinecraftClient.getInstance().getSoundManager().play(new RadioSoundInstance(song));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        performPlay();
    }

    @Override
    public void setHolderUUID(Optional<UUID> uuid) {
        super.setHolderUUID(uuid);
        if (uuid.isPresent() && isAllowMute()) {
            setMuted(!isMuted());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (getCustomName() != null) {
            final String name = getCustomName().getString();
            if (name.equalsIgnoreCase("exile") || name.equalsIgnoreCase("vilify") || name.equalsIgnoreCase("exile vilify")) {
                song = PortalCubedSounds.EXILE_MUSIC_EVENT;
            } else if (name.equalsIgnoreCase("silent")) {
                song = null;
            } else {
                song = PortalCubedSounds.RADIO_MUSIC_EVENT;
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
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!world.isClient && player.getStackInHand(hand).isOf(PortalCubedItems.HAMMER)) {
            setAllowMute(!isAllowMute());
            if (isAllowMute()) {
                player.sendMessage(Text.translatable("portalcubed.radio.allow_mute"), true);
            } else {
                setMuted(false);
                player.sendMessage(Text.translatable("portalcubed.radio.disallow_mute"), true);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Muted", isMuted());
        nbt.putBoolean("AllowMute", isAllowMute());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setMuted(nbt.getBoolean("Muted"));
        setAllowMute(nbt.getBoolean("AllowMute"));
    }

    @ClientOnly
    private class RadioSoundInstance extends MovingSoundInstance {
        private final SoundEvent song;

        RadioSoundInstance(SoundEvent song) {
            super(song, SoundCategory.RECORDS, SoundInstance.m_mglvabhn());
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
            volume = isMuted() ? 0f : 1f;
            x = RadioEntity.this.getX();
            y = RadioEntity.this.getY();
            z = RadioEntity.this.getZ();
        }
    }
}
