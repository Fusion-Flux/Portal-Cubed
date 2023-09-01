package com.fusionflux.portalcubed.blocks;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class RepulsionGel extends BaseGel {

	public RepulsionGel(Properties settings) {
		super(settings);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		this.addCollisionEffects(world, entity, pos);
	}

	public Vec3 getDirections(BlockState state) {
		Vec3 result = Vec3.ZERO;

		if (state.getValue(BlockStateProperties.NORTH)) {
			result = result.subtract(0, 0, 1);
		}
		if (state.getValue(BlockStateProperties.SOUTH)) {
			result = result.add(0, 0, 1);
		}

		if (state.getValue(BlockStateProperties.SOUTH) && state.getValue(BlockStateProperties.NORTH)) {
			result = result.add(0, 0, 2);
		}

		if (state.getValue(BlockStateProperties.EAST)) {
			result = result.add(1, 0, 0);
		}
		if (state.getValue(BlockStateProperties.WEST)) {
			result = result.subtract(1, 0, 0);
		}

		if (state.getValue(BlockStateProperties.EAST) && state.getValue(BlockStateProperties.WEST)) {
			result = result.add(2, 0, 0);
		}

		if (state.getValue(BlockStateProperties.UP)) {
			result = result.add(0, 1, 0);
		}
		if (state.getValue(BlockStateProperties.DOWN)) {
			result = result.subtract(0, 1, 0);
		}

		if (state.getValue(BlockStateProperties.UP) && state.getValue(BlockStateProperties.DOWN)) {
			result = result.add(0, 2, 0);
		}

		return result;
	}

	private void addCollisionEffects(Level world, Entity entity, BlockPos pos) {
		Vec3 vec3dLast = ((EntityExt) entity).getLastVel();
		Vec3 vec3d = new Vec3(Math.max(entity.getDeltaMovement().x(), vec3dLast.x()), Math.max(entity.getDeltaMovement().y(), vec3dLast.y()), Math.max(entity.getDeltaMovement().z(), vec3dLast.z()));
		BlockState state = world.getBlockState(pos);

		Vec3 direction = getDirections(state);
		Vec3 rotatedPos = entity.position();
		direction = RotationUtil.vecWorldToPlayer(direction, GravityChangerAPI.getGravityDirection(entity));
		rotatedPos = RotationUtil.vecWorldToPlayer(rotatedPos, GravityChangerAPI.getGravityDirection(entity));

		if (!entity.isSuppressingBounce()) {
			final boolean jumping = entity instanceof LivingEntityAccessor living && living.isJumping();
			if (entity.verticalCollision || jumping) {
				final boolean speedGel = Math.abs(vec3d.x()) + Math.abs(vec3d.z()) > 0.6;
				if ((direction.y == -1 || Math.abs(direction.y) == 2)  && (vec3dLast.y() < 0 || speedGel || jumping)) {
					double fall = ((EntityExt) entity).getMaxFallHeight();
					if (fall != rotatedPos.y || speedGel) {

						fall = fall - rotatedPos.y;
						if (fall < 5) {
							fall = 5;
						}
						double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
						entity.setOnGround(false);
						entity.setDeltaMovement(vec3d.x, velocity, vec3d.z);
						((EntityExt) entity).setMaxFallHeight(rotatedPos.y);
						PortalCubed.playBounceSound(entity);
						if (entity instanceof Player && world.isClientSide && (jumping || speedGel)) {
							PortalCubed.playBounceSoundRemotely();
						}
					}
				}
				if (direction.y == 1 || Math.abs(direction.y) == 2 && vec3dLast.y() > 0) {
					entity.setDeltaMovement(vec3d.x, -vec3dLast.y, vec3d.z);
					PortalCubed.playBounceSound(entity);
				}
			}

			double defaultVelocity = Math.sqrt(2 * .08 * .25);
			if (entity.horizontalCollision) {
				if (direction.z == -1 || Math.abs(direction.z) == 2 && vec3dLast.z() < 0) {
					if (Math.abs(vec3dLast.z) < defaultVelocity) {
						entity.setDeltaMovement(vec3d.x, vec3d.y, defaultVelocity);
					} else {
						entity.setDeltaMovement(vec3d.x, vec3d.y, -vec3dLast.z);
					}
					if (Math.abs(vec3dLast.z) > .1) {
						if (vec3dLast.y() != 0) {
							double fall = ((EntityExt)entity).getMaxFallHeight();
							if (fall != rotatedPos.y) {

								fall = fall - rotatedPos.y;
								if (fall < 1.5) {
									fall = 1.5;
								}

								double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
								entity.setDeltaMovement(vec3d.x, velocity, vec3d.z);
								((EntityExt)entity).setMaxFallHeight(rotatedPos.y);
							}
						}
					}

					PortalCubed.playBounceSound(entity);
				}
				if (direction.z == 1 || Math.abs(direction.z) == 2 && vec3dLast.z() > 0) {
					if (Math.abs(vec3dLast.z) < defaultVelocity) {
						entity.setDeltaMovement(vec3d.x, vec3d.y, -defaultVelocity);
					} else {
						entity.setDeltaMovement(vec3d.x, vec3d.y, -vec3dLast.z);
					}
					if (Math.abs(vec3dLast.z) > .1 && vec3dLast.y() != 0) {
						double fall = ((EntityExt)entity).getMaxFallHeight();
						if (fall != rotatedPos.y) {

							fall = fall - rotatedPos.y;
							if (fall < 1.5) {
								fall = 1.5;
							}
							double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
							entity.setDeltaMovement(vec3d.x, velocity, vec3d.z);
							((EntityExt)entity).setMaxFallHeight(rotatedPos.y);
						}
					}
					PortalCubed.playBounceSound(entity);
				}
				if (direction.x == 1 || Math.abs(direction.x) == 2 && vec3dLast.x() > 0) {

					if (Math.abs(vec3dLast.x) < defaultVelocity) {
						entity.setDeltaMovement(-defaultVelocity, vec3d.y, vec3d.z);
					} else {
						entity.setDeltaMovement(-vec3dLast.x, vec3d.y, vec3d.z);
					}
					if (Math.abs(vec3dLast.x) > .1 && vec3dLast.y() != 0) {
						double fall = ((EntityExt)entity).getMaxFallHeight();
						if (fall != rotatedPos.y) {

							fall = fall - rotatedPos.y;
							if (fall < 1.5) {
								fall = 1.5;
							}
							double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
							entity.setDeltaMovement(vec3d.x, velocity, vec3d.z);
							((EntityExt)entity).setMaxFallHeight(rotatedPos.y);
						}
					}
					PortalCubed.playBounceSound(entity);
				}
				if (direction.x == -1 || Math.abs(direction.x) == 2 && vec3dLast.x() < 0) {
					if (Math.abs(vec3dLast.x) < defaultVelocity) {
						entity.setDeltaMovement(defaultVelocity, vec3d.y, vec3d.z);
					} else {
						entity.setDeltaMovement(-vec3dLast.x, vec3d.y, vec3d.z);
					}
					if (Math.abs(vec3dLast.x) > .1 && vec3dLast.y() != 0) {
						double fall = ((EntityExt)entity).getMaxFallHeight();
						if (fall != rotatedPos.y) {

							fall = fall - rotatedPos.y;
							if (fall < 1.5) {
								fall = 1.5;
							}
							double velocity = Math.sqrt(2 * .08 * (fall)) + .0402;
							entity.setDeltaMovement(vec3d.x, velocity, vec3d.z);
							((EntityExt)entity).setMaxFallHeight(rotatedPos.y);
						}
					}
					PortalCubed.playBounceSound(entity);
				}
			}
		}
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (entity.isSuppressingBounce()) {
			super.fallOn(level, state, pos, entity, fallDistance);
		} else {
			entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
		}
	}
}
