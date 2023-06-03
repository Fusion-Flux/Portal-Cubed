package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.listeners.CustomCollisionView;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelReader.class)
public interface WorldViewMixin extends CustomCollisionView {
}
