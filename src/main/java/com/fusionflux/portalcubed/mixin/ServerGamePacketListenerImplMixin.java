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

    @WrapOperation(
        method = "handleUseItemOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck1(Vec3 instance, Vec3 to, Operation<Double> original) {
        final double distance = CrossPortalInteraction.interactionDistance(player, ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE, to);
        return distance == Double.NEGATIVE_INFINITY ? original.call(instance, to) : distance;
    }

    @WrapOperation(
        method = "handleUseItemOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;distanceToSqr(DDD)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck2(ServerPlayer instance, double x, double y, double z, Operation<Double> original) {
        final double distance = CrossPortalInteraction.interactionDistance(player, 64, new Vec3(x, y, z));
        return distance == Double.NEGATIVE_INFINITY ? original.call(instance, x, y, z) : distance;
    }

    @WrapOperation(
        method = "handleInteract",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D",
            ordinal = 0
        )
    )
    private double portalCubed$replaceWithCrossPortalInteractionDistanceCheck3(AABB instance, Vec3 eyePos, Operation<Double> original) {
        final double distance = CrossPortalInteraction.interactionDistance(player, ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE, instance.getCenter());
        return distance == Double.NEGATIVE_INFINITY ? original.call(instance, eyePos) : distance;
    }

}
