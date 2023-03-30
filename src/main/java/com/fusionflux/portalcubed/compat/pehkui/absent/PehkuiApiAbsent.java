package com.fusionflux.portalcubed.compat.pehkui.absent;

import com.fusionflux.portalcubed.compat.pehkui.PehkuiApi;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleModifier;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleType;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public enum PehkuiApiAbsent implements PehkuiApi {
    INSTANCE;

    @Override
    public PehkuiScaleType getScaleType(Identifier id) {
        return PehkuiScaleTypeAbsent.INSTANCE;
    }

    @Override
    public PehkuiScaleModifier getScaleModifier(Identifier id) {
        return PehkuiScaleModifierAbsent.INSTANCE;
    }

    @Override
    public PehkuiScaleType registerScaleType(Identifier id, PehkuiScaleModifier valueModifier) {
        return PehkuiScaleTypeAbsent.INSTANCE;
    }

    @Override
    public float getFallingScale(Entity entity) {
        return 1f;
    }
}
