package com.fusionflux.portalcubed.compat.pehkui;

import com.fusionflux.portalcubed.compat.pehkui.absent.PehkuiApiAbsent;
import com.fusionflux.portalcubed.compat.pehkui.present.PehkuiApiPresent;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.QuiltLoader;

public interface PehkuiApi {
    PehkuiApi INSTANCE = QuiltLoader.isModLoaded("pehkui") ? PehkuiPresentHolder.getPresent() : PehkuiApiAbsent.INSTANCE;

    PehkuiScaleType getScaleType(Identifier id);

    PehkuiScaleModifier getScaleModifier(Identifier id);

    PehkuiScaleType registerScaleType(Identifier id, PehkuiScaleModifier valueModifier);

    float getFallingScale(Entity entity);

    static Identifier id(String path) {
        return new Identifier("pehkui", path);
    }

    class PehkuiPresentHolder {
        private static PehkuiApi getPresent() {
            return new PehkuiApiPresent();
        }
    }
}
