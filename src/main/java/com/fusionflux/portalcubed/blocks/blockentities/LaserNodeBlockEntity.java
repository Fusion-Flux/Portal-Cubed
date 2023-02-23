package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class LaserNodeBlockEntity extends BlockEntity {
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
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        laserCount = nbt.getInt("LaserCount");
    }

    public static void tick(World world, BlockPos pos, BlockState state, LaserNodeBlockEntity blockEntity) {
        if (world.isClient) {
            blockEntity.clientTick();
        }
    }

    @ClientOnly
    protected void clientTick() {
        if (musicInstance == null && laserCount > 0) {
            musicInstance = new MovingSoundInstance(PortalCubedSounds.LASER_NODE_MUSIC_EVENT, SoundCategory.BLOCKS, SoundInstance.m_mglvabhn()) {
                {
                    volume = 0.5f;
                    x = getPos().getX() + 0.5;
                    y = getPos().getY() + 0.5;
                    z = getPos().getZ() + 0.5;
                    repeat = true;
                }

                @Override
                public void tick() {
                    if (isRemoved() || world == null || !world.getBlockState(pos).get(Properties.ENABLED)) {
                        musicInstance = null;
                        setDone();
                    }
                }
            };
            MinecraftClient.getInstance().getSoundManager().play(musicInstance);
        }
    }
}
