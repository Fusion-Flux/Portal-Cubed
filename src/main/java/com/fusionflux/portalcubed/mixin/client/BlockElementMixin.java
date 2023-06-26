package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.BlockElementExt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.block.model.BlockElement;

@Mixin(BlockElement.class)
public class BlockElementMixin implements BlockElementExt {
    @Unique
    private String portalcubed$renderType;

    @Override
    @Nullable
    public String portalcubed$getRenderType() {
        return portalcubed$renderType;
    }

    @Override
    public void portalcubed$setRenderType(String type) {
        this.portalcubed$renderType = type;
    }
}
