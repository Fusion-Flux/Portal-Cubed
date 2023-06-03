package com.fusionflux.portalcubed.compat.pehkui.absent;

import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleData;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleType;
import net.minecraft.world.entity.Entity;

enum PehkuiScaleTypeAbsent implements PehkuiScaleType {
    INSTANCE;

    @Override
    public PehkuiScaleData getScaleData(Entity entity) {
        return PehkuiScaleDataAbsent.INSTANCE;
    }
}
