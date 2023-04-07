package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
    @Shadow public abstract void play(SoundInstance sound);

    @Inject(
        method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;add(Ljava/lang/Object;)Z",
            ordinal = 1
        )
    )
    private void ohFiddlesticks(SoundInstance sound, CallbackInfo ci) {
        if (
            !sound.getId().getNamespace().equals(PortalCubed.MOD_ID) ||
                Registry.SOUND_EVENT.getHolder(RegistryKey.of(Registry.SOUND_EVENT_KEY, sound.getId()))
                    .map(h -> h.isIn(PortalCubedSounds.NO_ERROR_SOUND))
                    .orElse(true)
        ) return;
        play(PositionedSoundInstance.master(PortalCubedSounds.ERROR_EVENT, 1f));
    }
}
