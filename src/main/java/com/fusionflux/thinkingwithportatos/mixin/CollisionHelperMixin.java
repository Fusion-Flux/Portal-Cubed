package com.fusionflux.thinkingwithportatos.mixin;

import com.fusionflux.thinkingwithportatos.accessor.VelocityTransfer;
import com.qouteall.immersive_portals.portal.Portal;
import com.qouteall.immersive_portals.teleportation.CollisionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CollisionHelper.class)
public abstract class CollisionHelperMixin {

    /**
     * @author
     * @reason sick n tired of the startup error so I added a reason lol
     */
    @Overwrite(remap = false)
    public static Box getStretchedBoundingBox(Entity entity) {
        Vec3d expand;

             expand = entity.getVelocity().multiply(1.2);

            Vec3d expand2 = entity.getVelocity();
//if(Math.abs(entity.getVelocity().y)>1)
            // expand = entity.getVelocity().multiply(4 + Math.abs(entity.getVelocity().x), 4+Math.abs(entity.getVelocity().y), 4 + Math.abs(entity.getVelocity().z));
//entity.getVelocity().x*1.3, entity.getVelocity().y+(entity.getDimensions(entity.getPose()).height-entity.getEyeHeight(entity.getPose())), entity.getVelocity().z*1.3
   /* System.out.println(entity.getVelocity().y);
    System.out.println("expand");
System.out.println(expand);*/
            //.offset(0,-(entity.getDimensions(entity.getPose()).height-entity.getEyeHeight(entity.getPose())),0)
            return entity.getBoundingBox().stretch(expand);

    }
}
