package com.fusionflux.fluxtech.mixin;

import com.qouteall.immersive_portals.teleportation.CollisionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CollisionHelper.class)
public class CollisionHelperMixin {

/**
 * @author
 */
@Overwrite(remap = false)
    public static Box getStretchedBoundingBox(Entity entity) {
    Vec3d expand = entity.getVelocity().multiply(1.3D);
    return entity.getBoundingBox().stretch(expand).stretch(-expand.x,-expand.y, -expand.z);
    }

}
