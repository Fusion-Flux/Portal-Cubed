package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CameraExt;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExt {
    @Shadow
    private BlockView area;
    @Shadow
    @Final
    private BlockPos.Mutable blockPos;

    @Shadow public abstract Vec3d getPos();

    @Shadow protected abstract void setPos(double x, double y, double z);

    @Override
    public FluidState portalcubed$getSubmergedFluidState() {
        return this.area.getFluidState(blockPos);
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void lowDeath(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        final MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        if (PortalCubedClient.isPortalHudMode() && client.currentScreen instanceof DeathScreen) {
            setPos(getPos().x, getPos().y - 1, getPos().z);
        }
    }
}
