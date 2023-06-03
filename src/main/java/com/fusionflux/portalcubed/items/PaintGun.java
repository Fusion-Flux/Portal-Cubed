package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.entity.GelBlobEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import com.unascribed.lib39.recoil.api.DirectClickItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

public class PaintGun extends Item implements DirectClickItem, DyeableLeatherItem {

    public PaintGun(Properties settings) {
        super(settings);
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        boolean complementary = compoundTag.getBoolean("complementary");
        compoundTag = stack.getTagElement("display");
        return compoundTag != null && compoundTag.contains("color", 99) ? complementary ? compoundTag.getInt("color") * -1 : compoundTag.getInt("color") : (complementary ? 14842149 : -14842149);
    }

    private FireableGelType lastFiredGelType = null;

    @Override
    public InteractionResult onDirectAttack(Player user, InteractionHand hand) {
        fireGel(user.level, user, FireableGelType.REPULSION);
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult onDirectUse(Player user, InteractionHand hand) {
        fireGel(user.level, user, FireableGelType.PROPULSION);
        return InteractionResult.CONSUME;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return false;
    }

    private void fireGel(Level world, Player user, FireableGelType gelType) {
        if (lastFiredGelType != null && lastFiredGelType != gelType) {
            lastFiredGelType = null;
            return;
        }
        lastFiredGelType = gelType;

        if (world.isClientSide && !user.isSpectator() && !CalledValues.getCanFireGel(user)) {
            var byteBuf = PacketByteBufs.create();
            byteBuf.writeDouble(user.getDeltaMovement().x);
            byteBuf.writeDouble(user.getDeltaMovement().y);
            byteBuf.writeDouble(user.getDeltaMovement().z);
            byteBuf.writeBoolean(true);
            NetworkingSafetyWrapper.sendFromClient("request_velocity_for_gel", byteBuf);
        }
        if (!world.isClientSide && !user.isSpectator() && CalledValues.getCanFireGel(user)) {
            final GelBlobEntity entity = gelType.blobEntityType.create(world);
            if (entity == null) return;
            entity.setPos(user.getX(), user.getEyeY() - .5, user.getZ());
            entity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0f, 2f, 1f);
            entity.setOwner(user);
            entity.setSize(1);
            world.addFreshEntity(entity);
            CalledValues.setCanFireGel(user, false);
            entity.push(CalledValues.getServerVelForGel(user).x, CalledValues.getServerVelForGel(user).y, CalledValues.getServerVelForGel(user).z);
        }
    }

    private enum FireableGelType {
        REPULSION(PortalCubedEntities.REPULSION_GEL_BLOB),
        PROPULSION(PortalCubedEntities.PROPULSION_GEL_BLOB);

        public final EntityType<? extends GelBlobEntity> blobEntityType;

        FireableGelType(EntityType<? extends GelBlobEntity> blobEntityType) {
            this.blobEntityType = blobEntityType;
        }
    }

}
