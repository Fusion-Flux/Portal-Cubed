package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.client.PortalCubedClient;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow @Final public Player player;

    @Inject(method = "swapPaint", at = @At("HEAD"), cancellable = true)
    private void zoomInPortalMode(double scrollAmount, CallbackInfo ci) {
        if (player.level.isClientSide && performZoom((int)Math.signum(scrollAmount))) {
            ci.cancel();
        }
    }

    @ClientOnly
    private boolean performZoom(int delta) {
        if (!PortalCubedClient.isPortalHudMode()) {
            return false;
        }
        if (delta > 0) {
            if (PortalCubedClient.zoomDir == 0) {
                PortalCubedClient.zoomTimer = 0;
            } else if (PortalCubedClient.zoomDir < 0) {
                PortalCubedClient.zoomTimer = PortalCubedClient.ZOOM_TIME - PortalCubedClient.zoomTimer;
            }
            PortalCubedClient.zoomDir = 1;
        } else if (delta < 0 && PortalCubedClient.zoomDir > 0) {
            PortalCubedClient.zoomDir = -1;
            PortalCubedClient.zoomTimer = Math.max(PortalCubedClient.ZOOM_TIME - PortalCubedClient.zoomTimer, 0);
        }
        return true;
    }
}
