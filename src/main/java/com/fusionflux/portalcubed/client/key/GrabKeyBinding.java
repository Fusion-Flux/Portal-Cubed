package com.fusionflux.portalcubed.client.key;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.option.KeyBind;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class GrabKeyBinding {
    public static void register() {
        KeyBind key = new KeyBind(
                "key." + PortalCubed.MODID + ".grab",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key." + PortalCubed.MODID + ".category"
        );

        KeyBindingHelper.registerKeyBinding(key);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && key.wasPressed()) {
                ClientPlayNetworking.send(PortalCubedServerPackets.GRAB_KEY_PRESSED, PacketByteBufs.create());
            }
        });
    }
}
