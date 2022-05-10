package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.CollisionView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CollisionView.class)
public interface CollisionViewMixin {

}
