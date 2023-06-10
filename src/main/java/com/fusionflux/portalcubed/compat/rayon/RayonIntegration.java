package com.fusionflux.portalcubed.compat.rayon;

import com.fusionflux.portalcubed.compat.rayon.absent.RayonIntegrationAbsent;
import com.fusionflux.portalcubed.compat.rayon.present.RayonIntegrationPresent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public interface RayonIntegration {
    RayonIntegration INSTANCE = QuiltLoader.isModLoaded("rayon") ? RayonPresentHolder.create() : RayonIntegrationAbsent.INSTANCE;

    void init();

    boolean isPresent();

    void setVelocity(Entity entity, Vec3 velocity);

    void simpleMove(Entity entity, MoverType movementType, Vec3 movement);

    void setNoGravity(Entity entity, boolean noGravity);

    float getYaw(Entity entity);

    void rotateYaw(Entity entity, float amount);

    void setAngularVelocityYaw(Entity entity, Vector3f angle);

    @ClientOnly
    void multiplyMatrices(PoseStack matrices, Entity entity, float tickDelta);

    class RayonPresentHolder {
        private static RayonIntegration create() {
            return new RayonIntegrationPresent();
        }
    }
}
