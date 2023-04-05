package com.fusionflux.portalcubed.compat.rayon;

import com.fusionflux.portalcubed.compat.rayon.absent.RayonIntegrationAbsent;
import com.fusionflux.portalcubed.compat.rayon.present.RayonIntegrationPresent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public interface RayonIntegration {
    RayonIntegration INSTANCE = QuiltLoader.isModLoaded("rayon") ? RayonPresentHolder.create() : RayonIntegrationAbsent.INSTANCE;

    void init();

    boolean isPresent();

    void setVelocity(Entity entity, Vec3d velocity);

    void simpleMove(Entity entity, MovementType movementType, Vec3d movement);

    void setNoGravity(Entity entity, boolean noGravity);

    float getYaw(Entity entity);

    void rotateYaw(Entity entity, float amount);

    void setAngularVelocityYaw(Entity entity, Vec3f angle);

    @ClientOnly
    void multiplyMatrices(MatrixStack matrices, Entity entity, float tickDelta);

    class RayonPresentHolder {
        private static RayonIntegration create() {
            return new RayonIntegrationPresent();
        }
    }
}
