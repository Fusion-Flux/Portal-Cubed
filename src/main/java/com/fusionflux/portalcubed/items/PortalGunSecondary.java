package com.fusionflux.portalcubed.items;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class PortalGunSecondary extends PortalGun {
    public PortalGunSecondary(Properties settings) {
        super(settings);
    }

    @Override
    protected void shoot(Level world, Player user, InteractionHand hand, boolean leftClick) {
        super.shoot(world, user, hand, false);
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
    public boolean isSideActive(ClientLevel level, ItemStack stack, boolean rightSide) {
        return super.isSideActive(level, stack, true);
    }

    @Override
    protected boolean allowLinkingToOther() {
        return true;
    }
}
