package com.fusionflux.portalcubed.mixin.client;

import com.mojang.blaze3d.platform.InputUtil;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBind.class)
public interface KeyBindAccessor {
    @Accessor("KEY_BINDS_BY_KEY")
    static Map<InputUtil.Key, KeyBind> getKeyBindsByKey() {
        throw new AssertionError();
    }
}
