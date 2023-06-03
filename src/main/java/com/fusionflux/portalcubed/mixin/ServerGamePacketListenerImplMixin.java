package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.listeners.CustomCollisionView;
import com.fusionflux.portalcubed.mechanics.CrossPortalInteraction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;

    @Shadow public abstract ServerPlayer getPlayer();

    @ModifyVariable(method = "isPlayerCollidingWithAnythingNew", at = @At("STORE"), ordinal = 0)
    private Iterable<VoxelShape> isPlayerNotCollidingWithBlocks(Iterable<VoxelShape> shapes) {
        VoxelShape portalBox = CalledValues.getPortalCutout(this.getPlayer());
        if (portalBox != Shapes.empty()) {
            // Would take in the world value from the code, but I guess I cant
            return (((CustomCollisionView) this.player.getLevel()).getPortalCollisions(this.getPlayer(), this.getPlayer().getBoundingBox().deflate(1.0E-5F), portalBox));
        }

        return shapes;
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
