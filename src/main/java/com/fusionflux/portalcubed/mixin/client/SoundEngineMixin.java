package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
    @Shadow public abstract void play(SoundInstance sound);

    @Inject(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;add(Ljava/lang/Object;)Z",
            ordinal = 1
        )
    )
    private void ohFiddlesticks(SoundInstance sound, CallbackInfo ci) {
        if (
            !sound.getLocation().getNamespace().equals(PortalCubed.MOD_ID) ||
                BuiltInRegistries.SOUND_EVENT.getOrCreateTag(PortalCubedSounds.NO_ERROR_SOUND)
                    .contains(BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(
                        ResourceKey.create(Registries.SOUND_EVENT, sound.getLocation())
                    ))
        ) return;
        play(SimpleSoundInstance.forUI(PortalCubedSounds.ERROR_EVENT, 1f));
    }
}
