package com.fusionflux.portalcubed.compat.rayon.mixin;

import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CorePhysicsEntity.class, remap = false)
@SuppressWarnings("UnresolvedMixinReference")
public abstract class CorePhysicsEntityMixin implements EntityPhysicsElement {
    private final EntityRigidBody rigidBody = new EntityRigidBody(this);

    @Shadow public abstract float getRayonMass();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setupRigidBody(EntityType<? extends PathAwareEntity> type, World world, CallbackInfo ci) {
        rigidBody.setMass(getRayonMass());
    }

    @Override
    public EntityRigidBody getRigidBody() {
        return rigidBody;
    }
}
