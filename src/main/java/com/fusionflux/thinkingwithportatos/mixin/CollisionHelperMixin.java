package com.fusionflux.thinkingwithportatos.mixin;

import com.qouteall.immersive_portals.McHelper;
import com.qouteall.immersive_portals.ducks.IEEntity;
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

    @Shadow
    public static boolean canCollideWithPortal(Entity entity, Portal portal, float tickDelta) {
        return false;
    }



    /**
 * @author
 */
@Overwrite(remap = false)
    public static Box getStretchedBoundingBox(Entity entity) {
    Vec3d expand = entity.getVelocity().multiply(4 + Math.abs(entity.getVelocity().x), 4+Math.abs(entity.getVelocity().y), 4 + Math.abs(entity.getVelocity().z));
if(Math.abs(entity.getVelocity().y)>1)
    expand = entity.getVelocity().multiply(4 + Math.abs(entity.getVelocity().x), 4+Math.abs(entity.getVelocity().y), 4 + Math.abs(entity.getVelocity().z));

   /* System.out.println(entity.getVelocity().y);
    System.out.println("expand");
System.out.println(expand);*/
    return entity.getBoundingBox().stretch(entity.getVelocity().x, entity.getVelocity().y+(entity.getDimensions(entity.getPose()).height-entity.getEyeHeight(entity.getPose())), entity.getVelocity().z).offset(0,-(entity.getDimensions(entity.getPose()).height-entity.getEyeHeight(entity.getPose())),0);
    }


}
