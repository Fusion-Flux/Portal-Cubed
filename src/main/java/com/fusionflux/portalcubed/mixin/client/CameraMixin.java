package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CameraExt;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExt {
    @Shadow
    private BlockView area;
    @Shadow
    @Final
    private BlockPos.Mutable blockPos;

    @Shadow private Entity focusedEntity;

    @Shadow private boolean ready;

    @Override
    public FluidState portalcubed$getSubmergedFluidState() {
        return this.area.getFluidState(blockPos);
    }

    @Override
    public void updateSimple(BlockView area, Entity focusedEntity) {
        this.area = area;
        this.focusedEntity = focusedEntity;
        ready = true;
    }
}
