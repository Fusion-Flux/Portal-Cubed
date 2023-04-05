package com.fusionflux.portalcubed.compat.rayon.absent;

import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
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
    public void setVelocity(Entity entity, Vec3d velocity) {
        entity.setVelocity(velocity);
    }

    @Override
    public void simpleMove(Entity entity, MovementType movementType, Vec3d movement) {
        entity.move(movementType, movement);
    }

    @Override
    public void setNoGravity(Entity entity, boolean noGravity) {
        entity.setNoGravity(noGravity);
    }

    @Override
    public float getYaw(Entity entity) {
        return entity.getYaw();
    }

    @Override
    public void rotateYaw(Entity entity, float amount) {
        entity.setYaw(entity.getYaw() + amount);
    }

    @Override
    public void setAngularVelocityYaw(Entity entity, Vec3f angle) {
    }

    @Override
    @ClientOnly
    public void multiplyMatrices(MatrixStack matrices, Entity entity, float tickDelta) {
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f - entity.getYaw(tickDelta)));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(entity.getPitch(tickDelta)));
    }
}
