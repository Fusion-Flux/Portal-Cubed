package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @WrapWithCondition(
            method = "renderScreenEffect",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderTex(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;)V"
            )
    )
    @SuppressWarnings("unused")
    private static boolean renderOverlays(TextureAtlasSprite sprite, PoseStack matrices) {
        VoxelShape portalBox = CalledValues.getPortalCutout(Minecraft.getInstance().player);
        return portalBox == Shapes.empty();
    }

}
