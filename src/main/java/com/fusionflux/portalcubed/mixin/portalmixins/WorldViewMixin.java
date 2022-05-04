package com.fusionflux.portalcubed.mixin.portalmixins;

import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldView.class)
public interface WorldViewMixin extends CustomCollisionView {
}
