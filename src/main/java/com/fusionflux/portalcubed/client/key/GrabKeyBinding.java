package com.fusionflux.portalcubed.client.key;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBind;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

@ClientOnly
public class GrabKeyBinding {
    public static void register() {
        KeyBind key = new KeyBind(
                "key." + PortalCubed.MOD_ID + ".grab",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key." + PortalCubed.MOD_ID + ".category"
        );

        KeyBindingHelper.registerKeyBinding(key);
        ClientTickEvents.END.register(client -> {
            if (client.player != null && key.wasPressed()) {
                CorePhysicsEntity playerCube = (CorePhysicsEntity) ((Accessors)client.player.world).getEntity(CalledValues.getCubeUUID(client.player));
                if (playerCube != null) {
                    playerCube.dropCube();
                }
                ClientPlayNetworking.send(PortalCubedServerPackets.GRAB_KEY_PRESSED, PacketByteBufs.create());
            }
        });

        KeyBind portalRemoveKey = new KeyBind(
                "key." + PortalCubed.MOD_ID + ".removeportals",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key." + PortalCubed.MOD_ID + ".category"
        );

        KeyBindingHelper.registerKeyBinding(portalRemoveKey);
        ClientTickEvents.END.register(client -> {
            if (client.player != null && portalRemoveKey.wasPressed()) {
                ClientPlayNetworking.send(PortalCubedServerPackets.REMOVE_PORTALS, PacketByteBufs.create());
            }
        });
    }
}
