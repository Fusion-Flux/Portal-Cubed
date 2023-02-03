package com.fusionflux.portalcubed.items;


import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
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
import org.quiltmc.qsl.networking.api.PacketByteBufs;

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
        if(world.isClient && !user.isSpectator() && !CalledValues.getCanFireGel(user)){
            var byteBuf = PacketByteBufs.create();
            byteBuf.writeDouble(user.getVelocity().x);
            byteBuf.writeDouble(user.getVelocity().y);
            byteBuf.writeDouble(user.getVelocity().z);
            byteBuf.writeBoolean(true);
            NetworkingSafetyWrapper.sendFromClient("requestvelocityforgel", byteBuf);
        }
        ItemStack stack = user.getStackInHand(hand);
        if(!world.isClient && !user.isSpectator() && CalledValues.getCanFireGel(user)) {
            final GelBlobEntity entity = PortalCubedEntities.REPULSION_GEL_BLOB.create(world);
            if (entity == null) TypedActionResult.pass(stack);
            entity.setPosition(user.getX(), user.getEyeY()-.5, user.getZ());
            entity.setProperties(user, user.getPitch(), user.getYaw(), 0f, 2f, 1f);
            entity.setOwner(user);
            entity.setSize(1);
            world.spawnEntity(entity);
            CalledValues.setCanFireGel(user,false);
            entity.addVelocity(CalledValues.getServerVelForGel(user).x,CalledValues.getServerVelForGel(user).y,CalledValues.getServerVelForGel(user).z);
        }
        TypedActionResult.pass(stack);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient && !user.isSpectator() && !CalledValues.getCanFireGel(user)){
            var byteBuf = PacketByteBufs.create();
            byteBuf.writeDouble(user.getVelocity().x);
            byteBuf.writeDouble(user.getVelocity().y);
            byteBuf.writeDouble(user.getVelocity().z);
            byteBuf.writeBoolean(true);
            NetworkingSafetyWrapper.sendFromClient("requestvelocityforgel", byteBuf);
        }
        ItemStack stack = user.getStackInHand(hand);
        if(!world.isClient && !user.isSpectator() && CalledValues.getCanFireGel(user)) {
            final GelBlobEntity entity = PortalCubedEntities.PROPULSION_GEL_BLOB.create(world);
            if (entity == null) return TypedActionResult.pass(stack);
            entity.setPosition(user.getX(), user.getEyeY()-.5, user.getZ());
            entity.setProperties(user, user.getPitch(), user.getYaw(), 0f, 2f, 1f);
            entity.setOwner(user);
            world.spawnEntity(entity);
            entity.setSize(1);
            CalledValues.setCanFireGel(user,false);
            entity.addVelocity(CalledValues.getServerVelForGel(user).x,CalledValues.getServerVelForGel(user).y,CalledValues.getServerVelForGel(user).z);
        }
        return TypedActionResult.pass(stack);
    }








}