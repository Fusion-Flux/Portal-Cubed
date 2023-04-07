package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
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
                PortalCubedClient.NO_ERROR_SOUND.contains(sound.getId())
        ) return;
        play(PositionedSoundInstance.master(PortalCubedSounds.ERROR_EVENT, 1f));
    }
}
