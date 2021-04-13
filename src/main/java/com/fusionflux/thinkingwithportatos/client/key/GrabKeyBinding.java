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
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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
                if (!ThinkingWithPortatos.getBodyGrabbingManager(true).isPlayerGrabbing(client.player)) {
                    Vec3d vec3d = client.player.getCameraPosVec(1.0f);
                    Vec3d vec3d2 = client.player.getRotationVec(1.0f);
                    double d = client.interactionManager.getReachDistance();
                    Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
                    Box box = client.player.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);

                    EntityHitResult result = ProjectileUtil.raycast(client.player, vec3d, vec3d3, box, entity -> true, d);

                    if (result != null && result.getEntity() != null) {
                        Entity toSend;

                        if (result.getEntity().hasVehicle()) {
                            toSend = result.getEntity().getVehicle();
                        } else {
                            toSend = result.getEntity();
                        }

                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(toSend.getEntityId());
                        ClientPlayNetworking.send(ThinkingWithPortatosServerPackets.GRAB_KEY_PRESSED, buf);
                    }
                } else {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(-1);
                    ClientPlayNetworking.send(ThinkingWithPortatosServerPackets.GRAB_KEY_PRESSED, buf);
                }
            }
        });
    }
}
