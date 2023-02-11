package com.fusionflux.portalcubed.items;


import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import com.unascribed.lib39.recoil.api.DirectClickItem;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;


public class PaintGun extends Item implements DirectClickItem, DyeableItem {

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

    private FireableGelType lastFiredGelType = null;

    @Override
    public ActionResult onDirectAttack(PlayerEntity user, Hand hand) {
        fireGel(user.world, user, FireableGelType.REPULSION);
        return ActionResult.CONSUME;
    }

    @Override
    public ActionResult onDirectUse(PlayerEntity user, Hand hand) {
        fireGel(user.world, user, FireableGelType.PROPULSION);
        return ActionResult.CONSUME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    private void fireGel(World world, PlayerEntity user, FireableGelType gelType) {
        if (lastFiredGelType != null && lastFiredGelType != gelType) {
            lastFiredGelType = null;
            return;
        }
        lastFiredGelType = gelType;

        if (world.isClient && !user.isSpectator() && !CalledValues.getCanFireGel(user)) {
            var byteBuf = PacketByteBufs.create();
            byteBuf.writeDouble(user.getVelocity().x);
            byteBuf.writeDouble(user.getVelocity().y);
            byteBuf.writeDouble(user.getVelocity().z);
            byteBuf.writeBoolean(true);
            NetworkingSafetyWrapper.sendFromClient("requestvelocityforgel", byteBuf);
        }
        if (!world.isClient && !user.isSpectator() && CalledValues.getCanFireGel(user)) {
            final GelBlobEntity entity = gelType.blobEntityType.create(world);
            if (entity == null) return;
            entity.setPosition(user.getX(), user.getEyeY()-.5, user.getZ());
            entity.setProperties(user, user.getPitch(), user.getYaw(), 0f, 2f, 1f);
            entity.setOwner(user);
            entity.setSize(1);
            world.spawnEntity(entity);
            CalledValues.setCanFireGel(user,false);
            entity.addVelocity(CalledValues.getServerVelForGel(user).x,CalledValues.getServerVelForGel(user).y,CalledValues.getServerVelForGel(user).z);
        }
    }

    private enum FireableGelType {
        REPULSION(PortalCubedEntities.REPULSION_GEL_BLOB),
        PROPULSION(PortalCubedEntities.PROPULSION_GEL_BLOB);

        public final EntityType<? extends GelBlobEntity> blobEntityType;

        private FireableGelType(EntityType<? extends GelBlobEntity> blobEntityType) {
            this.blobEntityType = blobEntityType;
        }
    }

}