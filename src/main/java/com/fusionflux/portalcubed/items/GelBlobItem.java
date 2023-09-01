package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.entity.GelBlobEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class GelBlobItem extends BlockItem {
	private final EntityType<? extends GelBlobEntity> blobEntity;

	public GelBlobItem(Block block, EntityType<? extends GelBlobEntity> blobEntity, Properties settings) {
		super(block, settings);
		this.blobEntity = blobEntity;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		final ItemStack item = user.getItemInHand(hand);
		if (world.isClientSide) return InteractionResultHolder.pass(item);
		final GelBlobEntity entity = blobEntity.create(world);
		if (entity == null) return InteractionResultHolder.pass(item);
		entity.setPos(user.getX(), user.getEyeY(), user.getZ());
		entity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0f, 3f, 1f);
		entity.setOwner(user);
		world.addFreshEntity(entity);
		if (!user.getAbilities().instabuild) {
			item.shrink(1);
		}
		return InteractionResultHolder.consume(item);
	}
}
