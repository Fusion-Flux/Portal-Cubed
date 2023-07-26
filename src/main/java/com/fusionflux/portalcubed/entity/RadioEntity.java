package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.Optional;
import java.util.UUID;

public class RadioEntity extends CorePhysicsEntity  {
    private static final AABB BASE_BOX = createFootBox(0.4375, 0.3125, 0.1875);

    private static final EntityDataAccessor<Boolean> MUTED = SynchedEntityData.defineId(RadioEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ALLOW_MUTE = SynchedEntityData.defineId(RadioEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private SoundEvent song = PortalCubedSounds.RADIO_MUSIC_EVENT;
    @Nullable
    private SoundEvent lastSong = song;

    public RadioEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(MUTED, false);
        entityData.define(ALLOW_MUTE, true);
    }

    @NotNull
    @Override
    protected AABB makeBoundingBox() {
        return GeneralUtil.rotate(BASE_BOX, yHeadRot, Direction.Axis.Y).move(position());
    }

    public boolean isMuted() {
        return entityData.get(MUTED);
    }

    public void setMuted(boolean notPlaying) {
        entityData.set(MUTED, notPlaying);
    }

    public boolean isAllowMute() {
        return entityData.get(ALLOW_MUTE);
    }

    public void setAllowMute(boolean allowMute) {
        entityData.set(ALLOW_MUTE, allowMute);
    }

    @ClientOnly
    private void performPlay() {
        if (song == null) return;
        Minecraft.getInstance().getSoundManager().play(new RadioSoundInstance(song));
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
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
            if (level().isClientSide) {
                performPlay();
            }
        }
    }

    @Override
    protected InteractionResult physicsEntityInteraction(Player player, InteractionHand hand) {
        if (!level().isClientSide && player.getItemInHand(hand).is(PortalCubedItems.WRENCHES)) {
            setAllowMute(!isAllowMute());
            if (isAllowMute()) {
                player.displayClientMessage(Component.translatable("portalcubed.radio.allow_mute"), true);
            } else {
                setMuted(false);
                player.displayClientMessage(Component.translatable("portalcubed.radio.disallow_mute"), true);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Muted", isMuted());
        nbt.putBoolean("AllowMute", isAllowMute());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setMuted(nbt.getBoolean("Muted"));
        setAllowMute(nbt.getBoolean("AllowMute"));
    }

    @ClientOnly
    private class RadioSoundInstance extends AbstractTickableSoundInstance {
        private final SoundEvent song;

        RadioSoundInstance(SoundEvent song) {
            super(song, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
            this.song = song;
            volume = 1f;
            pitch = 1f;
            looping = true;
            x = RadioEntity.this.getX();
            y = RadioEntity.this.getY();
            z = RadioEntity.this.getZ();
        }

        @Override
        public void tick() {
            if (isRemoved() || song != RadioEntity.this.song) {
                stop();
                return;
            }
            volume = isMuted() ? 0f : 1f;
            x = RadioEntity.this.getX();
            y = RadioEntity.this.getY();
            z = RadioEntity.this.getZ();
        }
    }
}
