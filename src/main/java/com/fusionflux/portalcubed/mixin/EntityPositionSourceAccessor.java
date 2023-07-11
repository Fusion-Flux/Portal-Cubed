package com.fusionflux.portalcubed.mixin;

import java.util.UUID;

import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.EntityPositionSource;

@Mixin(EntityPositionSource.class)
public interface EntityPositionSourceAccessor {
	@Accessor
	Either<Entity, Either<UUID, Integer>> getEntityOrUuidOrId();
}
