package com.fusionflux.portalcubed.items;

import com.fusionflux.portalcubed.entity.EnergyPelletEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EnergyPelletItem extends Item {
    private final boolean isSuper;

    public EnergyPelletItem(Settings settings, boolean isSuper) {
        super(settings);
        this.isSuper = isSuper;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final ItemStack item = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(item);
        if (!user.getAbilities().creativeMode) {
            item.decrement(1);
        }
        final EnergyPelletEntity pellet = PortalCubedEntities.ENERGY_PELLET.create(world);
        if (pellet == null) return TypedActionResult.pass(item);
        pellet.setPosition(user.getCameraPosVec(0).add(user.getRotationVector()));
        Vec3d userVelocity = user.getVelocity();
        if (user.isOnGround()) {
            userVelocity = userVelocity.withAxis(Direction.Axis.Y, 0);
        }
        pellet.setVelocity(userVelocity.add(user.getRotationVector().multiply(0.25)));
        if (isSuper) {
            pellet.resetLife(-1);
        }
        world.spawnEntity(pellet);
        return TypedActionResult.consume(item);
    }

    public DispenserBehavior createDispenserBehavior() {
        return new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                final EnergyPelletEntity pellet = PortalCubedEntities.ENERGY_PELLET.create(pointer.getWorld());
                if (pellet == null) return stack;
                final Position pos = DispenserBlock.getOutputLocation(pointer);
                pellet.setPosition(pos.getX(), pos.getY(), pos.getZ());
                pellet.setVelocity(Vec3d.of(pointer.getBlockState().get(DispenserBlock.FACING).getVector()));
                if (isSuper) {
                    pellet.resetLife(-1);
                }
                pointer.getWorld().spawnEntity(pellet);
                stack.decrement(1);
                return stack;
            }

            @Override
            protected void playSound(BlockPointer pointer) {
            }

            @Override
            protected void spawnParticles(BlockPointer pointer, Direction side) {
            }
        };
    }
}
