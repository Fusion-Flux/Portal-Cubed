package com.fusionflux.portalcubed.mixin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySubPredicate.Types.class)
@SuppressWarnings("checkstyle:TypeName")
public class EntitySubPredicate_TypesMixin {
    @Shadow @Final @Mutable
    @SuppressWarnings("checkstyle:StaticVariableName")
    public static BiMap<String, EntitySubPredicate.Type> TYPES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void makeMapMutable(CallbackInfo ci) {
        TYPES = HashBiMap.create(TYPES);
    }
}
