package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.PortalCubedKeyBindings;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.platform.InputUtil;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @WrapWithCondition(
        method = "onKey",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/KeyBind;onKeyPressed(Lcom/mojang/blaze3d/platform/InputUtil$Key;)V"
        )
    )
    private boolean dontSpamGrab(InputUtil.Key instance, long window, int key, int scancode, int action, int modifiers) {
        if (action != GLFW_REPEAT) {
            return true;
        }
        final KeyBind keybind = KeyBindAccessor.getKeyBindsByKey().get(instance);
        return keybind != PortalCubedKeyBindings.GRAB && (!PortalCubedClient.isPortalHudMode() || keybind != client.options.inventoryKey);
    }
}
