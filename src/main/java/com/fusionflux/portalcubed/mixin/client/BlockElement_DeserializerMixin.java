package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.BlockElementExt;
import com.google.gson.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.model.BlockElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Type;

@Mixin(targets = "net.minecraft.client.renderer.block.model.BlockElement$Deserializer")
public class BlockElement_DeserializerMixin {
	@ModifyReturnValue(
			method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockElement;",
			at = @At("RETURN")
	)
	private BlockElement addName(BlockElement element,
								 JsonElement json, Type type, JsonDeserializationContext context) {
		JsonObject obj = json.getAsJsonObject();
		JsonElement nameElement = obj.get("name");
		if (nameElement instanceof JsonPrimitive primitive && primitive.isString()) {
			String name = primitive.getAsString();
			((BlockElementExt) element).portalcubed$setName(name);
		}
		return element;
	}
}
