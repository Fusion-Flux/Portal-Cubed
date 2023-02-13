package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public abstract class AbstractLaserNodeBlockEntity extends BlockEntity {
    @ClientOnly
    private SoundInstance musicInstance;

    public AbstractLaserNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void updateState(BlockState state, boolean toggle) {
        if (world != null && toggle != state.get(Properties.ENABLED)) {
            world.playSound(null, pos, toggle ? PortalCubedSounds.LASER_NODE_ACTIVATE_EVENT : PortalCubedSounds.LASER_NODE_DEACTIVATE_EVENT, SoundCategory.BLOCKS, 0.5f, 1f);
            world.setBlockState(pos, state.with(Properties.ENABLED, toggle), 3);
        }
    }

    @ClientOnly
    protected void clientTick(BlockState state) {
        if (musicInstance == null && state.get(Properties.ENABLED)) {
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
