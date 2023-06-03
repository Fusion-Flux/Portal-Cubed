package com.fusionflux.portalcubed.client;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

@ClientOnly
public class PortalCubedKeyBindings {
    public static final KeyMapping GRAB = KeyBindingHelper.registerKeyBinding(new KeyMapping(
        "key." + PortalCubed.MOD_ID + ".grab",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        "key." + PortalCubed.MOD_ID + ".category"
    ));
    public static final KeyMapping REMOVE_PORTALS = KeyBindingHelper.registerKeyBinding(new KeyMapping(
        "key." + PortalCubed.MOD_ID + ".remove_portals",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key." + PortalCubed.MOD_ID + ".category"
    ));

    public static void register() {
        ClientTickEvents.END.register(client -> {
            if (client.player != null && GRAB.consumeClick()) {
                PortalCubedComponents.HOLDER_COMPONENT.get(client.player).stopHolding();
                ClientPlayNetworking.send(PortalCubedServerPackets.GRAB_KEY_PRESSED, PacketByteBufs.create());
            }
            if (client.player != null && REMOVE_PORTALS.consumeClick() && !PortalCubedClient.isPortalHudMode()) {
                ClientPlayNetworking.send(PortalCubedServerPackets.REMOVE_PORTALS, PacketByteBufs.create());
            }
        });

        if (!QuiltLoader.isModLoaded("visiblebarriers")) {
            final KeyMapping toggleHiddenBlocksKey = new KeyMapping(
                "key." + PortalCubed.MOD_ID + ".toggle_hidden_blocks",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "key." + PortalCubed.MOD_ID + ".category"
            );
            KeyBindingHelper.registerKeyBinding(toggleHiddenBlocksKey);
            ClientTickEvents.END.register(client -> {
                if (toggleHiddenBlocksKey.consumeClick()) {
                    PortalCubedClient.toggleHiddenBlocksVisible();
                    client.levelRenderer.allChanged();
                }
            });
        }
    }
}
