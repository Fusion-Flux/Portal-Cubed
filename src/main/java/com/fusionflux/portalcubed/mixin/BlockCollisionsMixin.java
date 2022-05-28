package com.fusionflux.portalcubed.mixin;

import net.minecraft.util.math.Box;
import net.minecraft.world.BlockCollisions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockCollisions.class)
public class BlockCollisionsMixin {
    //Todo: Noticed this is unused seemingly, removal soon?
    @Redirect(
            at = @At(target = "Lnet/minecraft/util/math/Box;intersects(DDDDDD)Z", value = "INVOKE"),
            method = "computeNext()Lnet/minecraft/util/shape/VoxelShape;"
    )
    private boolean redirectIntersectionCheck(Box ins, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return false;
    }
}