package com.fusionflux.portalcubed.compat.pehkui.present;

import com.fusionflux.portalcubed.compat.pehkui.PehkuiApi;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleModifier;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;

public class PehkuiApiPresent implements PehkuiApi {
    @Override
    public PehkuiScaleType getScaleType(ResourceLocation id) {
        final ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id);
        return scaleType == null ? null : new PehkuiScaleTypePresent(scaleType);
    }

    @Override
    public PehkuiScaleModifier getScaleModifier(ResourceLocation id) {
        final ScaleModifier scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_MODIFIERS, id);
        return scaleType == null ? null : new PehkuiScaleModifierPresent(scaleType);
    }

    @Override
    public PehkuiScaleType registerScaleType(ResourceLocation id, PehkuiScaleModifier valueModifier) {
        return new PehkuiScaleTypePresent(ScaleRegistries.register(
            ScaleRegistries.SCALE_TYPES, id,
            ScaleType.Builder.create()
                .addBaseValueModifier(((PehkuiScaleModifierPresent)valueModifier).inner())
                .build()
        ));
    }

    @Override
    public float getFallingScale(Entity entity) {
        return ScaleTypes.FALLING.getScaleData(entity).getScale();
    }
}
