package com.fusionflux.portalcubed.packet;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.network.FriendlyByteBuf;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

//Because of some Mixin+JVM weird implementation detail bs -
//gotta have the `ClientPlayNetworking` call in a separate class
//so that Mixin can resolve all the classes used when patching
public class NetworkingSafetyWrapper {
    public static void sendFromClient(String name, FriendlyByteBuf buf) {
        ClientPlayNetworking.send(PortalCubed.id(name), buf);
    }
}
