package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.BlockElementExt;
import com.fusionflux.portalcubed.client.render.block.MultiRenderTypeSimpleBakedModel;
import com.fusionflux.portalcubed.client.render.block.RenderMaterials;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("checkstyle:TypeName")
@Mixin(BlockModel.Deserializer.class)
public class BlockModel_DeserializerMixin {
	// failing in parsing is much better than in baking.
	@ModifyReturnValue(method = "deserialize", at = @At("RETURN"))
	private BlockModel portalcubed$validateMultiRenderTypeModel(BlockModel original) {
		if (!RenderMaterials.ARE_SUPPORTED)
			return original;

		for (BlockElement element : original.getElements()) {
			String renderType = ((BlockElementExt) element).portalcubed$getRenderType();
			if (renderType != null) {
				MultiRenderTypeSimpleBakedModel.parseType(renderType);
			}
		}
		return original;
	}
}
