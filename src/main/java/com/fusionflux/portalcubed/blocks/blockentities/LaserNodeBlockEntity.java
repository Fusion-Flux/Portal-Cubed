package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class LaserNodeBlockEntity extends BlockEntity {
    private ResourceLocation sound = PortalCubedSounds.LASER_NODE_MUSIC;
    private int laserCount;

    @ClientOnly
    private SoundInstance musicInstance;

    public LaserNodeBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY, pos, state);
    }

    public void updateState(boolean toggle) {
        assert level != null;
        level.playSound(null, worldPosition, toggle ? PortalCubedSounds.LASER_NODE_ACTIVATE_EVENT : PortalCubedSounds.LASER_NODE_DEACTIVATE_EVENT, SoundSource.BLOCKS, 0.3f, 1f);
        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.ENABLED, toggle));
        setChanged();
    }

    public void addLaser() {
        if (laserCount++ == 0) {
            updateState(true);
        }
        if (laserCount < 0) {
            if (laserCount == Integer.MIN_VALUE) {
                PortalCubed.LOGGER.warn("More than 2 *billion* lasers hit the laser node at {}!?", worldPosition);
                laserCount = Integer.MAX_VALUE;
            } else {
                PortalCubed.LOGGER.warn("Laser node at {} has a negative number of lasers hitting it?", worldPosition);
                laserCount = 0;
            }
        }
    }

    public void removeLaser() {
        if (--laserCount <= 0) {
            updateState(false);
        }
        if (laserCount < 0) {
            PortalCubed.LOGGER.warn("Laser node at {} has a negative number of lasers hitting it?", worldPosition);
            laserCount = 0;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("LaserCount", laserCount);
        nbt.putString("Sound", sound.toString());
    }

    @Override
    public void load(CompoundTag nbt) {
        laserCount = nbt.getInt("LaserCount");

        final String soundStr = nbt.getString("Sound");
        if (!soundStr.isEmpty()) {
            sound = new ResourceLocation(soundStr);
        }
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, LaserNodeBlockEntity blockEntity) {
        if (world.isClientSide) {
            blockEntity.clientTick(state);
        }
    }

    @ClientOnly
    protected void clientTick(BlockState state) {
        if (musicInstance == null && level != null && state.getValue(BlockStateProperties.ENABLED)) {
            // The sounds played here may actually be stereo, so we may need to handle our own fadeout
            musicInstance = new AbstractTickableSoundInstance(SoundEvent.createVariableRangeEvent(sound), SoundSource.BLOCKS, SoundInstance.createUnseededRandom()) {
                final ResourceLocation soundId = LaserNodeBlockEntity.this.sound;
                final boolean isCustomSound = !soundId.equals(PortalCubedSounds.LASER_NODE_MUSIC);

                {
                    if (!isCustomSound) {
                        volume = 0.5f;
                    }
                    x = getBlockPos().getX() + 0.5;
                    y = getBlockPos().getY() + 0.5;
                    z = getBlockPos().getZ() + 0.5;
                    looping = true;
                }

                @Override
                public void tick() {
                    if (level != null && !isRemoved() && LaserNodeBlockEntity.this.sound.equals(soundId)) {
                        BlockState state = level.getBlockState(worldPosition);
                        if (state.hasProperty(BlockStateProperties.ENABLED) && state.getValue(BlockStateProperties.ENABLED)) {
                            if (!isCustomSound) return;
                            final Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                            final float newVolume = Mth.clampedLerp(0.01f, 1f, 1f - (float)cameraPos.subtract(x, y, z).length() / 16f);
                            if (newVolume != volume) {
                                volume = newVolume;
                                Minecraft.getInstance().getSoundManager().updateSourceVolume(null, 0);
                            }
                            return;
                        }
                    }

                    musicInstance = null;
                    stop();
                }
            };
            Minecraft.getInstance().getSoundManager().play(musicInstance);
        }
    }

    public ResourceLocation getSound() {
        return sound;
    }

    public void setSound(ResourceLocation sound) {
        this.sound = sound;
        updateListeners();
    }

    public void updateListeners() {
        setChanged();
        assert level != null;
        final BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }
}
