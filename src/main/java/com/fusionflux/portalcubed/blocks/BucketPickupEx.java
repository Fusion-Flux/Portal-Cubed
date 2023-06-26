package com.fusionflux.portalcubed.blocks;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BucketPickupEx extends BucketPickup {
    @NotNull
    Optional<SoundEvent> getPickupSound(BlockState state);

    @NotNull
    @Override
    default Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }
}
