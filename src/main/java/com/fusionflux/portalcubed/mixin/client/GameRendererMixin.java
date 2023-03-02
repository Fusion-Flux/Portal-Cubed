package com.fusionflux.portalcubed.mixin.client;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.fusionflux.portalcubed.accessor.AdvancedRaycastResultHolder;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @WrapOperation(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    private EntityHitResult portalCubed$portalCompatibleEntityRaycast(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double d, Operation<EntityHitResult> original) {
        if (client.crosshairTarget instanceof AdvancedRaycastResultHolder resultHolder && resultHolder.getResult().isPresent()) {
            return resultHolder.getResult().get().entityRaycast(entity, predicate);
        } else {
            return original.call(entity, min, max, box, predicate, d);
        }
    }

    @WrapOperation(
        method = {"getFov", "bobViewWhenHurt"},
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(FF)F"
        )
    )
    private float noDeathEffects(float a, float b, Operation<Float> original) {
        if (PortalCubedClient.isPortalHudMode()) {
            return 0;
        }
        return original.call(a, b);
    }
}
