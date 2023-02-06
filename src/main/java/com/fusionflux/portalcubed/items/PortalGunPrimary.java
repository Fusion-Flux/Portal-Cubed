package com.fusionflux.portalcubed.items;


import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;


public class PortalGunPrimary extends PortalGun {
    public PortalGunPrimary(Settings settings) {
        super(settings);
    }

    @Override
    protected void shoot(World world, PlayerEntity user, Hand hand, boolean leftClick) {
        super.shoot(world, user, hand, true);
    }

    @Override
    public boolean isComplementary(ItemStack stack) {
        return false;
    }

    @Override
    public int getColorForHudHalf(ItemStack stack, boolean rightHalf) {
        return super.getColorForHudHalf(stack, false);
    }

    @Override
    @ClientOnly
    public boolean isSideActive(ClientWorld world, ItemStack stack, boolean rightSide) {
        return super.isSideActive(world, stack, false);
    }

    @Override
    protected boolean allowLinkingToOther() {
        return true;
    }
}
