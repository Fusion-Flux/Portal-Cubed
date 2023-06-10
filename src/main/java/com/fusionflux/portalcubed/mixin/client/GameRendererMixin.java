package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.AdvancedRaycastResultHolder;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @WrapOperation(
        method = "pick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
        )
    )
    private EntityHitResult portalCubed$portalCompatibleEntityRaycast(Entity entity, Vec3 min, Vec3 max, AABB box, Predicate<Entity> predicate, double d, Operation<EntityHitResult> original) {
        if (minecraft.hitResult instanceof AdvancedRaycastResultHolder resultHolder && resultHolder.getResult().isPresent()) {
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
