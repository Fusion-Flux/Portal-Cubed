package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.CollisionView;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Overwrite
    private boolean isPlayerNotCollidingWithBlocks(WorldView world, Box box) {
        Iterable<VoxelShape> iterable = world.getCollisions(this.player, this.player.getBoundingBox().contract(1.0E-5F));


        List<ExperimentalPortal> list = player.world.getEntitiesByClass(ExperimentalPortal.class, player.getBoundingBox().expand(2), e -> true);
        for (ExperimentalPortal entity1 : list) {
            iterable =(((CustomCollisionView) player.world).getPortalCollisions(player, player.getBoundingBox(), entity1.getFacingDirection()));
        }

        VoxelShape voxelShape = VoxelShapes.cuboid(box.contract(1.0E-5F));

        for(VoxelShape voxelShape2 : iterable) {
            if (!VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND)) {
                return true;
            }
        }

        return false;
    }
}