package com.fusionflux.portalcubed.mixin.client;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("checkstyle:TypeName")
@Mixin(ModelVariant.Deserializer.class)
public class ModelVariant_DeserializerMixin {
    @WrapOperation(
        method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/ModelVariant;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/json/ModelVariant$Deserializer;deserializeRotation(Lcom/google/gson/JsonObject;)Lnet/minecraft/client/render/model/ModelRotation;"
        )
    )
    private ModelRotation dontDeserialize45(ModelVariant.Deserializer instance, JsonObject object, Operation<ModelRotation> original) {
        if (object.has("y")) {
            final int y = object.get("y").getAsInt();
            if (y / 90 * 90 != y) {
                return null;
            }
        }
        if (object.has("x")) {
            final int x = object.get("x").getAsInt();
            if (x / 90 * 90 != x) {
                return null;
            }
        }
        return original.call(instance, object);
    }

    @WrapOperation(
        method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/ModelVariant;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelRotation;getRotation()Lnet/minecraft/util/math/AffineTransformation;"
        )
    )
    private AffineTransformation customRotate45(ModelRotation instance, Operation<AffineTransformation> original, @Local JsonObject object) {
        if (instance != null) {
            return original.call(instance);
        }
        final int x = JsonHelper.getInt(object, "x", 0);
        final int y = JsonHelper.getInt(object, "y", 0);
        final Quaternion quaternion = Vec3f.POSITIVE_Y.getDegreesQuaternion(-y);
        quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(-x));
        return new AffineTransformation(null, quaternion, null, null);
    }
}
