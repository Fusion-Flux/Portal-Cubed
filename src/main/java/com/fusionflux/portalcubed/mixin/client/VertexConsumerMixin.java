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
		method = "putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;vertex(FFFFFFFFFIIFFF)V"
		),
		index = 6
	)
	default float modifyAlpha(float original) {
		return EnergyPelletRenderer.pelletAlpha == null ? original : EnergyPelletRenderer.pelletAlpha;
	}
}
