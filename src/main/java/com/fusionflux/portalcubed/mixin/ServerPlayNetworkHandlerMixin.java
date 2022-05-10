package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CollisionView;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract ServerPlayerEntity getPlayer();

    /**
     * @author
     */
    @Overwrite
    private boolean isPlayerNotCollidingWithBlocks(WorldView world, Box box) {
        Iterable<VoxelShape> iterable = world.getCollisions( this.getPlayer(), this.getPlayer().getBoundingBox().contract(1.0E-5F));
        VoxelShape voxelShape = VoxelShapes.cuboid(box.contract(1.0E-5F));

        Box directions = CalledValues.getPortalCutout(this.getPlayer());
        if(!Objects.equals(directions, new Box(0, 0, 0, 0, 0, 0))){
            iterable =(((CustomCollisionView) world).getPortalCollisions(this.getPlayer(), this.getPlayer().getBoundingBox().contract(1.0E-5F), directions));
            //System.out.println("ServerPlayNetworkHandler" + iterable);
        }

        for(VoxelShape voxelShape2 : iterable) {
            if (!VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND)) {
                return true;
            }
        }

        return false;
    }

    //@Inject(method = "tick", at = @At("TAIL"))
    //public void tick(CallbackInfo ci) {
//
    //    //if(!((Entity) (Object) this).world.isClient) {
    //    List<ExperimentalPortal> list = this.getPlayer().world.getNonSpectatingEntities(ExperimentalPortal.class, this.getPlayer().getBoundingBox());
    //    VoxelShape ommitedDirections = VoxelShapes.empty();
    //    for (ExperimentalPortal entity1 : list) {
//
    //        ommitedDirections = VoxelShapes.union(ommitedDirections, VoxelShapes.cuboid(entity1.calculateCuttoutBox()));
    //    }
    //    CalledValues.setPortalCutout(this.getPlayer(), ommitedDirections);
//
    //}

}