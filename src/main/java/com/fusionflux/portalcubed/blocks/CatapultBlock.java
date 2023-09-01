package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.blocks.blockentities.CatapultBlockEntity;
import com.fusionflux.portalcubed.entity.EnergyPellet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CatapultBlock extends SpecialHiddenBlockWithEntity implements BlockCollisionTrigger {
	private static final VoxelShape SHAPE = box(3, 3, 3, 13, 13, 13);

	public CatapultBlock(Properties settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CatapultBlockEntity(pos, state);
	}

	@Override
	protected VoxelShape getVisibleOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getTriggerShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public void onEntityEnter(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (entity instanceof EntityExt ext && !(entity instanceof EnergyPellet)) {
			world.getBlockEntity(pos, PortalCubedBlocks.CATAPULT_BLOCK_ENTITY).ifPresent(ext::collidedWithCatapult);
		}
	}

	@NotNull
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!player.isCreative()) return InteractionResult.PASS;
		if (!world.isClientSide) {
			final MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);

			if (screenHandlerFactory != null) {
				player.openMenu(screenHandlerFactory);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
