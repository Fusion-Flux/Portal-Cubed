package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.PortalCubedKeyBindings;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	@Shadow @Final private Minecraft minecraft;

	@WrapWithCondition(
		method = "keyPress",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/KeyMapping;click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"
		)
	)
	private boolean dontSpamGrab(InputConstants.Key instance, long window, int key, int scancode, int action, int modifiers) {
		if (action != GLFW_REPEAT) {
			return true;
		}
		final KeyMapping keybind = KeyMappingAccessor.getMAP().get(instance);
		return keybind != PortalCubedKeyBindings.GRAB && (!PortalCubedClient.isPortalHudMode() || keybind != minecraft.options.keyInventory);
	}
}
