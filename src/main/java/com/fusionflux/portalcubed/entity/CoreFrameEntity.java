package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class CoreFrameEntity extends CorePhysicsEntity {

    public CoreFrameEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

}
