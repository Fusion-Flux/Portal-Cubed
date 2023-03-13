package com.fusionflux.portalcubed.compat.rayon;

import com.fusionflux.portalcubed.compat.rayon.absent.RayonIntegrationAbsent;
import com.fusionflux.portalcubed.compat.rayon.present.RayonIntegrationPresent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.loader.api.QuiltLoader;

public interface RayonIntegration {
    RayonIntegration INSTANCE = QuiltLoader.isModLoaded("rayon") ? RayonPresentHolder.create() : RayonIntegrationAbsent.INSTANCE;

    void setVelocity(Entity entity, Vec3d velocity);

    Quaternion getVisualRotation(Entity entity, float tickDelta);

    class RayonPresentHolder {
        private static RayonIntegration create() {
            return new RayonIntegrationPresent();
        }
    }
}
