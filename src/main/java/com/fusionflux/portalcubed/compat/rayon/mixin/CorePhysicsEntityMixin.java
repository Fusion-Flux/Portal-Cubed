package com.fusionflux.portalcubed.compat.rayon.mixin;

import com.fusionflux.portalcubed.compat.rayon.RayonUtil;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CorePhysicsEntity.class, remap = false)
@SuppressWarnings("UnresolvedMixinReference")
public abstract class CorePhysicsEntityMixin extends PathfinderMob implements EntityPhysicsElement {
    private final EntityRigidBody rigidBody = new EntityRigidBody(this);

    protected CorePhysicsEntityMixin(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setupRigidBody(EntityType<? extends PathfinderMob> type, Level world, CallbackInfo ci) {
        rigidBody.setDragCoefficient(0.001f);
        rigidBody.setMass(1.5f);
        rigidBody.setProtectGravity(true);
        rigidBody.setGravity(new Vector3f(0, -16.8f, 0));
        rigidBody.setDragType(ElementRigidBody.DragType.NONE);
    }

    @Inject(method = {"tick", "method_5773"}, at = @At("TAIL"))
    private void checkBlockCollisions(CallbackInfo ci) {
        // Rayon normally prevents this call, as it's called in move() and Rayon yeets move()
        tryCheckInsideBlocks();
    }

    @Override
    public EntityRigidBody getRigidBody() {
        return rigidBody;
    }

    @Override
    public MinecraftShape.Convex createShape() {
        return new MinecraftShape.Convex(RayonUtil.getShiftedMeshOf(
            Convert.toBullet(getBoundingBox().contract(0, 0.1, 0))
        ));
    }

    @Override
    protected AABB makeBoundingBox() {
        return super.makeBoundingBox().expandTowards(0, 0.1, 0);
    }
}
