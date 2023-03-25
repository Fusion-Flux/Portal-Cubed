package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.blocks.LaserEmitterBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyReturnValue(method = "getLeftText", at = @At("RETURN"))
    private List<String> addEmitterDirection(List<String> original) {
        original.add("");
        final var direction = LaserEmitterBlock.EmitterDirection.getClosest(client.cameraEntity);
        original.add("Emitter direction: " + direction.asString());
        original.add("Opposite: " + direction.getOpposite().asString());
        return original;
    }
}
