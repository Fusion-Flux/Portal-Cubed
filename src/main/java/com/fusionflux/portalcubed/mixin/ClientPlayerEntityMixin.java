package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow @Final protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld clientWorld, GameProfile gameProfile, @Nullable PlayerPublicKey playerPublicKey) {
        super(clientWorld, gameProfile, playerPublicKey);
    }


    @Inject(method = "wouldCollideAt", at = @At("HEAD"), cancellable = true)
    public void portalCubed$changeCollision(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((ClientPlayerEntity)(Object)this));
        if(portalBox != VoxelShapes.empty()){
            cir.setReturnValue(false);
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