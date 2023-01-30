package com.fusionflux.portalcubed.items;


import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPQuaternion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;


public class PaintGun extends Item implements DyeableItem {

    public PaintGun(Settings settings) {
        super(settings);
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound compoundTag = stack.getOrCreateNbt();
        boolean complementary = compoundTag.getBoolean("complementary");
        compoundTag = stack.getSubNbt("display");
        return compoundTag != null && compoundTag.contains("color", 99) ? complementary ? compoundTag.getInt("color") * -1 : compoundTag.getInt("color") : (complementary ? 14842149 : -14842149);
    }

    public void useLeft(World world, PlayerEntity user, Hand hand) {
        if(!user.isSpectator()) {
            ItemStack stack = user.getStackInHand(hand);
            final GelBlobEntity entity = PortalCubedEntities.REPULSION_GEL_BLOB.create(world);
            if (entity == null) TypedActionResult.pass(stack);
            entity.setPosition(user.getX(), user.getEyeY(), user.getZ());
            entity.setProperties(user, user.getPitch(), user.getYaw(), 0f, 3f, 1f);
            entity.setOwner(user);
            world.spawnEntity(entity);
            entity.addVelocity(user.getVelocity().x,user.getVelocity().y,user.getVelocity().z);
            TypedActionResult.pass(stack);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        final GelBlobEntity entity = PortalCubedEntities.PROPULSION_GEL_BLOB.create(world);
        if (entity == null) return TypedActionResult.pass(stack);
        entity.setPosition(user.getX(), user.getEyeY(), user.getZ());
        entity.setProperties(user, user.getPitch(), user.getYaw(), 0f, 3f, 1f);
        entity.setOwner(user);
        world.spawnEntity(entity);
        entity.addVelocity(user.getVelocity().x,user.getVelocity().y,user.getVelocity().z);
        return TypedActionResult.pass(stack);
    }







}