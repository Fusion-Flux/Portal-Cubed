package com.fusionflux.portalcubed.client.key;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBind;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

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
        ClientTickEvents.END.register(client -> {
            if (client.player != null && key.wasPressed()) {
                StorageCubeEntity playercube = (StorageCubeEntity) ((Accessors)client.player.world).getEntity(CalledValues.getCubeUUID(client.player));
                if (playercube != null) {
                    playercube.dropCube();
                }
                ClientPlayNetworking.send(PortalCubedServerPackets.GRAB_KEY_PRESSED, PacketByteBufs.create());
            }
        });
    }
}
