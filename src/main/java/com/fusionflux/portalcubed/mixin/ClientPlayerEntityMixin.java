package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow @Final protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "wouldCollideAt", at = @At("RETURN"),locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void wouldCollideAt(BlockPos pos, CallbackInfoReturnable<Boolean> cir, Box box, Box box2) {
        Box directions = CalledValues.getPortalCutout(this);

        if(!Objects.equals(directions, new Box(0, 0, 0, 0, 0, 0))){
            cir.setReturnValue(((CustomCollisionView) this.world).canPortalCollide(this, box2, directions));
        }
    }


    //@Inject(method = "tick", at = @At("TAIL"))
    //public void tick(CallbackInfo ci) {
//
    //    //if(!((Entity) (Object) this).world.isClient) {
    //    List<ExperimentalPortal> list = ((Entity) (Object) this).world.getNonSpectatingEntities(ExperimentalPortal.class, this.client.player.getBoundingBox());
    //    VoxelShape ommitedDirections = VoxelShapes.empty();
    //    for (ExperimentalPortal entity1 : list) {
//
    //        ommitedDirections = VoxelShapes.union(ommitedDirections, VoxelShapes.cuboid(entity1.calculateCuttoutBox()));
    //    }
    //    CalledValues.setPortalCutout(((Entity) (Object) this), ommitedDirections);
//
    //}

}