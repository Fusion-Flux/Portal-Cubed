package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockCollisionSpliterator.class)
public class BlockCollisionSpliteratorMixin {

    @Redirect(
            at = @At(target = "Lnet/minecraft/util/math/Box;intersects(DDDDDD)Z", value = "INVOKE"),
            method = "computeNext()Lnet/minecraft/util/shape/VoxelShape;"
    )
    private boolean redirectIntersectionCheck(Box ins, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return false;
    }
}