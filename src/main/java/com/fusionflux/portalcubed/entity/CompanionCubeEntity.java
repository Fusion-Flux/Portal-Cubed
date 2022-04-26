package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class CompanionCubeEntity extends StorageCubeEntity  {
    public CompanionCubeEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    private int t = 1500;

    @Override
    public void tick() {
        if (!this.world.isClient) {
            if (t == 1500) {
                world.playSoundFromEntity(null,this, PortalCubedSounds.COMPANION_CUBE_AMBIANCE_EVENT,this.getSoundCategory(),1f,1f);
            }
            t--;
            if (t == 0) {
                t = 1500;
            }

        }
        super.tick();
    }

}
