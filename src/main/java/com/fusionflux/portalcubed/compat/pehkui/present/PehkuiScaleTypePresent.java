package com.fusionflux.portalcubed.compat.pehkui.present;

import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleData;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleType;
import net.minecraft.world.entity.Entity;
import virtuoel.pehkui.api.ScaleType;

record PehkuiScaleTypePresent(ScaleType inner) implements PehkuiScaleType {
    @Override
    public PehkuiScaleData getScaleData(Entity entity) {
        return new PehkuiScaleDataPresent(inner.getScaleData(entity));
    }
}
