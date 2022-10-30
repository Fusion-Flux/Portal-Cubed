package com.fusionflux.portalcubed.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.fusionflux.portalcubed.accessor.CameraExt;

import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExt {
    @Shadow
    private BlockView area;
    @Shadow
    @Final
    private BlockPos.Mutable blockPos;

    @Override
    public FluidState portalcubed$getSubmergedFluidState() {
        return this.area.getFluidState(blockPos);
    }
}
