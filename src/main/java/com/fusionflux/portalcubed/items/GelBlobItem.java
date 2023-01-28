package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.entity.GelBlobEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GelBlobItem extends BlockItem {
    private final EntityType<? extends GelBlobEntity> blobEntity;

    public GelBlobItem(Block block, EntityType<? extends GelBlobEntity> blobEntity, Settings settings) {
        super(block, settings);
        this.blobEntity = blobEntity;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final ItemStack item = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(item);
        final GelBlobEntity entity = blobEntity.create(world);
        if (entity == null) return TypedActionResult.pass(item);
        entity.setPosition(user.getX(), user.getEyeY(), user.getZ());
        entity.setProperties(user, user.getPitch(), user.getYaw(), 0f, 3f, 1f);
        entity.setOwner(user);
        world.spawnEntity(entity);
        if (!user.getAbilities().creativeMode) {
            item.decrement(1);
        }
        return TypedActionResult.consume(item);
    }
}
