package com.fusionflux.portalcubed.blocks;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.client.AdhesionGravityVerifier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;

public class AdhesionGel extends BaseGel {
	public AdhesionGel(Properties settings) {
		super(settings);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		AABB block = new AABB(pos);
		AABB player = getPlayerBox(entity.getBoundingBox(), GravityChangerAPI.getGravityDirection(entity), -(entity.getBbHeight() - 1));
		if (block.intersects(player)) {
			this.addCollisionEffects(world, entity, pos, state);
		}
	}

	private void addCollisionEffects(Level world, Entity entity, BlockPos pos, BlockState state) {
		FriendlyByteBuf info = AdhesionGravityVerifier.packInfo(pos);
		if ((entity.onGround() && entity.horizontalCollision) || (!entity.onGround() && entity.horizontalCollision) || (!entity.onGround() && !entity.horizontalCollision)) {
			if (((EntityExt) entity).getGelTimer() == 0) {
				Direction current = GravityChangerAPI.getGravityDirection(entity);

				double delta = -.9;
				ArrayList<Gravity> gravList = GravityChangerAPI.getGravityList(entity);
				for (Gravity grav : gravList) {
					if (grav.source().equals("portalcubed:adhesion_gel")) {
						delta = -.1;
						break;
					}
				}
				for (Direction direc : availableFaces(state)) {
					if (direc != current) {
						AABB gravbox = getGravityEffectBox(pos, direc, delta);
						if (gravbox.intersects(entity.getBoundingBox())) {
							if (world.isClientSide && entity instanceof Player) {
								GravityChangerAPI.addGravityClient((LocalPlayer) entity, AdhesionGravityVerifier.newFieldGravity(direc), AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, info);
								((EntityExt) entity).setGelTimer(10);
								break;
							} else {
								if (!(entity instanceof Player) && !world.isClientSide) {
									GravityChangerAPI.addGravity(entity, new Gravity(direc, 10, 2, "adhesion_gel"));
									((EntityExt) entity).setGelTimer(10);
									break;
								}
							}
						}
					}
				}
			}
		}
		if (world.isClientSide && entity instanceof Player) {
			GravityChangerAPI.addGravityClient((LocalPlayer) entity, AdhesionGravityVerifier.newFieldGravity(GravityChangerAPI.getGravityDirection(entity)), AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, info);
		} else {
			if (!(entity instanceof Player) && !world.isClientSide)
				GravityChangerAPI.addGravity(entity, new Gravity(GravityChangerAPI.getGravityDirection(entity), 10, 2, "adhesion_gel"));
		}
	}

	public AABB getGravityEffectBox(BlockPos blockPos, Direction direction, double delta) {
		double minX = blockPos.getX();
		double minY = blockPos.getY();
		double minZ = blockPos.getZ();
		double maxX = blockPos.getX() + 1;
		double maxY = blockPos.getY() + 1;
		double maxZ = blockPos.getZ() + 1;
		switch (direction) {
			case DOWN -> maxY += delta;
			case UP -> minY -= delta;
			case NORTH -> maxZ += delta;
			case SOUTH -> minZ -= delta;
			case WEST -> maxX += delta;
			case EAST -> minX -= delta;
		}
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public AABB getPlayerBox(AABB playerBox, Direction direction, double delta) {
		double minX = playerBox.minX;
		double minY = playerBox.minY;
		double minZ = playerBox.minZ;
		double maxX = playerBox.maxX;
		double maxY = playerBox.maxY;
		double maxZ = playerBox.maxZ;
		switch (direction) {
			case DOWN -> maxY += delta;
			case UP -> minY -= delta;
			case NORTH -> maxZ += delta;
			case SOUTH -> minZ -= delta;
			case WEST -> maxX += delta;
			case EAST -> minX -= delta;
		}
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
