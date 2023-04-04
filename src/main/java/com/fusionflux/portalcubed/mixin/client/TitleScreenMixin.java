package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.AdvancementTitles;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Mutable
    @Shadow @Final private RotatingCubeMapRenderer backgroundRenderer;

    private RotatingCubeMapRenderer pc$vanillaRotatingCube;
    private final AdvancementTitles.CustomCubeMapRenderer customCubeMapRenderer = new AdvancementTitles.CustomCubeMapRenderer();

    @Inject(method = "<init>(Z)V", at = @At("TAIL"))
    private void setVanillaCubemap(boolean doBackgroundFade, CallbackInfo ci) {
        pc$vanillaRotatingCube = backgroundRenderer;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void changeCubemap(CallbackInfo ci) {
        backgroundRenderer = PortalCubedConfig.titleScreenMode == PortalCubedConfig.TitleScreenMode.DEFAULT
            ? pc$vanillaRotatingCube : customCubeMapRenderer;
        customCubeMapRenderer.p1 = PortalCubedConfig.titleScreenMode == PortalCubedConfig.TitleScreenMode.P1;
    }
}
