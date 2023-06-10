package com.fusionflux.portalcubed.compat.rayon.absent;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public enum RayonIntegrationAbsent implements RayonIntegration {
    INSTANCE;

    @Override
    public void init() {
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public void setVelocity(Entity entity, Vec3 velocity) {
        entity.setDeltaMovement(velocity);
    }

    @Override
    public void simpleMove(Entity entity, MoverType movementType, Vec3 movement) {
        entity.move(movementType, movement);
    }

    @Override
    public void setNoGravity(Entity entity, boolean noGravity) {
        entity.setNoGravity(noGravity);
    }

    @Override
    public float getYaw(Entity entity) {
        return entity.getYRot();
    }

    @Override
    public void rotateYaw(Entity entity, float amount) {
        entity.setYRot(entity.getYRot() + amount);
    }

    @Override
    public void setAngularVelocityYaw(Entity entity, Vector3f angle) {
    }

    @Override
    @ClientOnly
    public void multiplyMatrices(PoseStack matrices, Entity entity, float tickDelta) {
        matrices.mulPose(Axis.YP.rotationDegrees(180f - entity.getViewYRot(tickDelta)));
        matrices.mulPose(Axis.XP.rotationDegrees(entity.getViewXRot(tickDelta)));
    }

}
