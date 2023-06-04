package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.BlockCollisionsExt;
import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;

    @Shadow public abstract ServerPlayer getPlayer();

    @WrapOperation(
        method = "isPlayerCollidingWithAnythingNew",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelReader;getCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/lang/Iterable;"
        )
    )
    private Iterable<VoxelShape> supportCutout(LevelReader instance, Entity entity, AABB collisionBox, Operation<Iterable<VoxelShape>> original) {
        return BlockCollisionsExt.wrapBlockCollisions(original.call(instance, entity, collisionBox), entity);
    }

    @Redirect(
        method = "handleUseItemOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck1(Vec3 from, Vec3 to) {
        return CrossPortalInteraction.interactionDistance(player, ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE, to);
    }

    @Redirect(
        method = "handleUseItemOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;distanceToSqr(DDD)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck2(ServerPlayer self, double x, double y, double z) {
        return CrossPortalInteraction.interactionDistance(player, 64, new Vec3(x, y, z));
    }

    @Redirect(
        method = "handleInteract",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck3(Entity self, Vec3 to) {
        return CrossPortalInteraction.interactionDistance(player, ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE, to);
    }

}
