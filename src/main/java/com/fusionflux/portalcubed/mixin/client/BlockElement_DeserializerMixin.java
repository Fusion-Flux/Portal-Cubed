package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.BlockElementExt;
import com.google.gson.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.model.BlockElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Type;

@SuppressWarnings("checkstyle:TypeName")
@Mixin(targets = "net.minecraft.client.renderer.block.model.BlockElement$Deserializer")
public class BlockElement_DeserializerMixin {
	@ModifyReturnValue(method = "deserialize", at = @At("RETURN"))
	private BlockElement portalcubed$addRenderType(BlockElement element,
												   JsonElement json, Type type, JsonDeserializationContext context) {
		JsonObject obj = json.getAsJsonObject();
		JsonElement renderTypeElement = obj.get("portalcubed:render_type");
		if (renderTypeElement != null) {
			if (!(renderTypeElement instanceof JsonPrimitive primitive) || !primitive.isString()) {
				throw new JsonParseException("portalcubed:render_type must be a string");
			}
			String renderType = primitive.getAsString();
			((BlockElementExt) element).portalcubed$setRenderType(renderType);
		}
		return element;
	}
}
