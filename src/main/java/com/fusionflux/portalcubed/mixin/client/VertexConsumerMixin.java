package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.client.render.entity.EnergyPelletRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin {
    @ModifyArg(
        method = "complexBakedQuad",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;vertex(FFFFFFFFFIIFFF)V"
        ),
        index = 3
    )
    default float modifyR(float original) {
        return original * EnergyPelletRenderer.pelletR;
    }

    @ModifyArg(
        method = "complexBakedQuad",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;vertex(FFFFFFFFFIIFFF)V"
        ),
        index = 4
    )
    default float modifyG(float original) {
        return original * EnergyPelletRenderer.pelletG;
    }

    @ModifyArg(
        method = "complexBakedQuad",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;vertex(FFFFFFFFFIIFFF)V"
        ),
        index = 5
    )
    default float modifyB(float original) {
        return original * EnergyPelletRenderer.pelletB;
    }

    @ModifyArg(
        method = "complexBakedQuad",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;vertex(FFFFFFFFFIIFFF)V"
        ),
        index = 6
    )
    default float modifyAlpha(float original) {
        return Objects.requireNonNullElse(EnergyPelletRenderer.pelletAlpha, original);
    }
}
