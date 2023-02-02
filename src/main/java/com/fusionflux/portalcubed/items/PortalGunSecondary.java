package com.fusionflux.portalcubed.items;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class PortalGunSecondary extends PortalGun {
    public PortalGunSecondary(Settings settings) {
        super(settings);
    }

    @Override
    public void useLeft(World world, PlayerEntity user, Hand hand) {
        useImpl(world, user, hand, false);
    }

    @Override
    public boolean isComplementary(ItemStack stack) {
        return true;
    }

    @Override
    public int getColorForHudHalf(ItemStack stack, boolean rightHalf) {
        return super.getColorForHudHalf(stack, true);
    }

    @Override
    @ClientOnly
    public boolean isSideActive(ClientWorld world, ItemStack stack, boolean rightSide) {
        return super.isSideActive(world, stack, true);
    }
}
