package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.render.entity.ExperimentalPortalRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {

    @ModifyVariable(method = "depthFunc", at = @At("HEAD"), argsOnly = true, remap = false)
    private static int portalCubed$fixPortalTracerDepthFunc(int originalDepthFunc) {
        return !ExperimentalPortalRenderer.renderingTracers ? originalDepthFunc : GL11.GL_GEQUAL;
    }

}
