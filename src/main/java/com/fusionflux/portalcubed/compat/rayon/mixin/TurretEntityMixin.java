package com.fusionflux.portalcubed.compat.rayon.mixin;

import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.TurretEntity;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TurretEntity.class)
public abstract class TurretEntityMixin extends CorePhysicsEntity implements EntityPhysicsElement {
	public TurretEntityMixin(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
	}

	// TODO: Custom shape
//	@Override
//	public MinecraftShape.Convex createShape() {
//		return new MinecraftShape.Convex(Stream.of(
//			RayonUtil.getShiftedMeshOf(Convert.toBullet(new Box(0, 0, 0, 1, 1, 0.75f)))
//		).flatMap(List::stream).toList());
//	}
}
