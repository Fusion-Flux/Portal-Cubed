package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "wouldCollideAt", at = @At("RETURN"),locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void tick(BlockPos pos, CallbackInfoReturnable<Boolean> cir, Box box, Box box2) {
        Vec3d directions = CalledValues.getOmmitedDirections(((ClientPlayerEntity)(Object)this));
        if(directions != Vec3d.ZERO){
            cir.setReturnValue(((CustomCollisionView) ((ClientPlayerEntity)(Object)this).world).canPortalCollide(((ClientPlayerEntity)(Object)this), box2, directions));
        }
        //List<ExperimentalPortal> list = ((Entity)(Object)this).world.getEntitiesByClass(ExperimentalPortal.class, box2, e -> true);
        //for (ExperimentalPortal entity1 : list) {
        //    cir.setReturnValue(((CustomCollisionView) ((Entity)(Object)this).world).canPortalCollide(((Entity)(Object)this), box2, entity1.getFacingDirection()));
        //}
    }
}