package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.BakedQuadExt;
import com.fusionflux.portalcubed.accessor.BlockElementExt;
import com.fusionflux.portalcubed.client.render.block.MultiRenderTypeSimpleBakedModel;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BlockModel.class)
public abstract class BlockModelMixin {
	@Shadow
	public abstract List<BlockElement> getElements();

	@ModifyReturnValue(
			method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;",
			at = @At("RETURN")
	)
	private BakedModel portalcubed$useMultiRenderTypeModel(BakedModel model) {
		if (model instanceof SimpleBakedModel simple) {
			for (BlockElement element : getElements()) {
				String renderType = ((BlockElementExt) element).portalcubed$getRenderType();
				if (renderType != null) {
					return new MultiRenderTypeSimpleBakedModel(simple);
				}
			}
		}
		return model;
	}

	@ModifyReturnValue(method = "bakeFace", at = @At("RETURN"))
	private static BakedQuad portalcubed$giveQuadRenderType(BakedQuad quad,
															BlockElement part, BlockElementFace partFace, TextureAtlasSprite sprite,
															Direction direction, ModelState transform, ResourceLocation location) {
		String renderType = ((BlockElementExt) part).portalcubed$getRenderType();
		if (renderType != null) {
			((BakedQuadExt) quad).portalcubed$setRenderType(renderType);
		}
		return quad;
	}
}
