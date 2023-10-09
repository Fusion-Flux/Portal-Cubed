package com.fusionflux.portalcubed.mixin.client;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.fusionflux.portalcubed.accessor.BlockElementExt;
import com.fusionflux.portalcubed.client.render.models.RenderMaterials;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockModel.Deserializer.class)
public class BlockModel_DeserializerMixin {
	@ModifyReturnValue(
			method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;",
			at = @At("RETURN")
	)
	private BlockModel addRenderTypeInfo(BlockModel original,
										 JsonElement blockElement, Type type, JsonDeserializationContext ctx) {
		if (!RenderMaterials.ARE_SUPPORTED)
			return original;

		JsonObject json = blockElement.getAsJsonObject();
		if (json.has("portalcubed:render_types")) {
			Map<String, RenderMaterial> elementMaterials = new HashMap<>();

			// read the material map
			// typeName -> elementName[]
			JsonObject typeMap = json.getAsJsonObject("portalcubed:render_types");
			typeMap.asMap().forEach((typeName, element) -> {
				RenderMaterial material = RenderMaterials.parse(typeName);
				JsonArray array = element.getAsJsonArray();
				array.forEach(nameElement -> {
					String elementName = nameElement.getAsString();
					elementMaterials.put(elementName, material);
				});
			});

			// apply read materials to elements
			for (BlockElement element : original.getElements()) {
				BlockElementExt ext = (BlockElementExt) element;
				String name = ext.portalcubed$getName();
				if (name != null && elementMaterials.containsKey(name)) {
					RenderMaterial material = elementMaterials.get(name);
					ext.portalcubed$setRenderMaterial(material);
				}
			}
		}

		return original;
	}
}
