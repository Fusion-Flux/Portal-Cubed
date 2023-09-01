package com.fusionflux.portalcubed.blocks.fizzler;

import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PhysicsRepulsionField extends AbstractFizzlerBlock {
	public PhysicsRepulsionField(Properties settings) {
		super(settings);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return context instanceof EntityCollisionContext entityCtx && entityCtx.getEntity() instanceof CorePhysicsEntity
			? getShape(state, world, pos, context)
			: super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public void applyEffectsTo(Entity entity) {
	}
}
