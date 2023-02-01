package com.fusionflux.portalcubed.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class PortalGunSecondary extends PortalGun {
    public PortalGunSecondary(Settings settings) {
        super(settings);
    }

    @Override
    public void useLeft(World world, PlayerEntity user, Hand hand) {
    }
}
