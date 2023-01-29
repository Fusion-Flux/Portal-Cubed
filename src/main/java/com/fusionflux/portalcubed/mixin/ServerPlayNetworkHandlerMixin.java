package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract ServerPlayerEntity getPlayer();

    @ModifyVariable(method = "isPlayerNotCollidingWithBlocks(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/Box;)Z", at = @At("STORE"), ordinal = 0)
    private Iterable<VoxelShape> isPlayerNotCollidingWithBlocks(Iterable<VoxelShape> shapes) {
        VoxelShape portalBox = CalledValues.getPortalCutout(this.getPlayer());
        if(portalBox != VoxelShapes.empty()) {
            // Would take in the world value from the code but I guess I cant
            return shapes = (((CustomCollisionView) this.player.getWorld()).getPortalCollisions(this.getPlayer(), this.getPlayer().getBoundingBox().contract(1.0E-5F), portalBox));
        }

        return shapes;
    }

}