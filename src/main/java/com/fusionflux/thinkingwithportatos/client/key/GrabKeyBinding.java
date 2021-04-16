package com.fusionflux.thinkingwithportatos.client.key;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.packet.ThinkingWithPortatosServerPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class GrabKeyBinding {
    public static void register() {
        KeyBinding key = new KeyBinding(
                "key." + ThinkingWithPortatos.MODID + ".grab",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key." + ThinkingWithPortatos.MODID + ".category"
        );

        KeyBindingHelper.registerKeyBinding(key);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && key.wasPressed()) {
                ClientPlayNetworking.send(ThinkingWithPortatosServerPackets.GRAB_KEY_PRESSED, PacketByteBufs.create());
            }
        });
    }
}
