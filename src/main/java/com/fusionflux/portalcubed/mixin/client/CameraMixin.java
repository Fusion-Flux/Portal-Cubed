package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CameraExt;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExt {
    @Shadow
    private BlockGetter level;
    @Shadow
    @Final
    private BlockPos.MutableBlockPos blockPosition;

    @Shadow private Entity entity;

    @Shadow private boolean initialized;

    @Override
    public FluidState portalcubed$getSubmergedFluidState() {
        return this.level.getFluidState(blockPosition);
    }

    @Override
    public void updateSimple(BlockGetter area, Entity focusedEntity) {
        this.level = area;
        this.entity = focusedEntity;
        initialized = true;
    }
}
