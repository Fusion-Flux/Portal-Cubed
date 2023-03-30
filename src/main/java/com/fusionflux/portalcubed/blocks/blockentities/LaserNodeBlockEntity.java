package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class LaserNodeBlockEntity extends BlockEntity {
    private Identifier sound = PortalCubedSounds.LASER_NODE_MUSIC;
    private int laserCount;

    @ClientOnly
    private SoundInstance musicInstance;

    public LaserNodeBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY, pos, state);
    }

    public void updateState(boolean toggle) {
        assert world != null;
        world.playSound(null, pos, toggle ? PortalCubedSounds.LASER_NODE_ACTIVATE_EVENT : PortalCubedSounds.LASER_NODE_DEACTIVATE_EVENT, SoundCategory.BLOCKS, 0.5f, 1f);
        world.setBlockState(pos, world.getBlockState(pos).with(Properties.ENABLED, toggle));
        markDirty();
    }

    public void addLaser() {
        if (laserCount++ == 0) {
            updateState(true);
        }
        if (laserCount < 0) {
            if (laserCount == Integer.MIN_VALUE) {
                PortalCubed.LOGGER.warn("More than 2 *billion* lasers hit the laser node at {}!?", pos);
                laserCount = Integer.MAX_VALUE;
            } else {
                PortalCubed.LOGGER.warn("Laser node at {} has a negative number of lasers hitting it?", pos);
                laserCount = 0;
            }
        }
    }

    public void removeLaser() {
        if (--laserCount <= 0) {
            updateState(false);
        }
        if (laserCount < 0) {
            PortalCubed.LOGGER.warn("Laser node at {} has a negative number of lasers hitting it?", pos);
            laserCount = 0;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("LaserCount", laserCount);
        nbt.putString("Sound", sound.toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        laserCount = nbt.getInt("LaserCount");

        final String soundStr = nbt.getString("Sound");
        if (!soundStr.isEmpty()) {
            sound = new Identifier(soundStr);
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return toNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.of(this);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LaserNodeBlockEntity blockEntity) {
        if (world.isClient) {
            blockEntity.clientTick();
        }
    }

    @ClientOnly
    protected void clientTick() {
        if (musicInstance == null && world != null && world.getBlockState(pos).get(Properties.ENABLED)) {
            // The sounds played here may actually be stereo, so we may need to handle our own fadeout
            musicInstance = new MovingSoundInstance(new SoundEvent(sound), SoundCategory.BLOCKS, SoundInstance.createUnseededRandom()) {
                final Identifier soundId = LaserNodeBlockEntity.this.sound;
                final boolean isCustomSound = !soundId.equals(PortalCubedSounds.LASER_NODE_MUSIC);

                {
                    if (!isCustomSound) {
                        volume = 0.5f;
                    }
                    x = getPos().getX() + 0.5;
                    y = getPos().getY() + 0.5;
                    z = getPos().getZ() + 0.5;
                    repeat = true;
                }

                @Override
                public void tick() {
                    if (
                        isRemoved() ||
                            !LaserNodeBlockEntity.this.sound.equals(soundId) ||
                            world == null || !world.getBlockState(pos).get(Properties.ENABLED)
                    ) {
                        musicInstance = null;
                        setDone();
                    }
                    if (!isCustomSound) return;
                    final Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
                    final float newVolume = MathHelper.clampedLerp(0.01f, 1f, 1f - (float)cameraPos.subtract(x, y, z).length() / 16f);
                    if (newVolume != volume) {
                        volume = newVolume;
                        MinecraftClient.getInstance().getSoundManager().updateSoundVolume(null, 0);
                    }
                }
            };
            MinecraftClient.getInstance().getSoundManager().play(musicInstance);
        }
    }

    public Identifier getSound() {
        return sound;
    }

    public void setSound(Identifier sound) {
        this.sound = sound;
        updateListeners();
    }

    public void updateListeners() {
        markDirty();
        assert world != null;
        final BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
    }
}
