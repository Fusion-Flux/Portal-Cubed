package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.entity.EnergyPelletEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EnergyPelletItem extends Item {
    private final boolean isSuper;

    public EnergyPelletItem(Properties settings, boolean isSuper) {
        super(settings);
        this.isSuper = isSuper;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        final ItemStack item = user.getItemInHand(hand);
        if (world.isClientSide) return InteractionResultHolder.pass(item);
        if (!user.getAbilities().instabuild) {
            item.shrink(1);
        }
        final EnergyPelletEntity pellet = PortalCubedEntities.ENERGY_PELLET.create(world);
        if (pellet == null) return InteractionResultHolder.pass(item);
        pellet.setPos(user.getEyePosition(0).add(user.getLookAngle()));
        Vec3 userVelocity = user.getDeltaMovement();
        if (user.onGround()) {
            userVelocity = userVelocity.with(Direction.Axis.Y, 0);
        }
        pellet.setDeltaMovement(userVelocity.add(user.getLookAngle().scale(0.25)));
        if (isSuper) {
            pellet.resetLife(-1);
        }
        world.addFreshEntity(pellet);
        return InteractionResultHolder.consume(item);
    }

    public DispenseItemBehavior createDispenserBehavior() {
        return new DefaultDispenseItemBehavior() {
            @NotNull
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                final EnergyPelletEntity pellet = PortalCubedEntities.ENERGY_PELLET.create(pointer.getLevel());
                if (pellet == null) return stack;
                final Position pos = DispenserBlock.getDispensePosition(pointer);
                pellet.setPos(pos.x(), pos.y(), pos.z());
                pellet.setDeltaMovement(Vec3.atLowerCornerOf(pointer.getBlockState().getValue(DispenserBlock.FACING).getNormal()));
                if (isSuper) {
                    pellet.resetLife(-1);
                }
                pointer.getLevel().addFreshEntity(pellet);
                stack.shrink(1);
                return stack;
            }

            @Override
            protected void playSound(BlockSource pointer) {
            }

            @Override
            protected void playAnimation(BlockSource pointer, Direction side) {
            }
        };
    }
}
