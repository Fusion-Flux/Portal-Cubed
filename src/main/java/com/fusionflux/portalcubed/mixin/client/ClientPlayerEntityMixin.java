package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.HasMovementInputAccessor;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements HasMovementInputAccessor {

    @Shadow @Final protected MinecraftClient client;

    @Shadow public Input input;

    public ClientPlayerEntityMixin(ClientWorld clientWorld, GameProfile gameProfile, @Nullable PlayerPublicKey playerPublicKey) {
        super(clientWorld, gameProfile, playerPublicKey);
    }


    @Inject(method = "wouldCollideAt", at = @At("HEAD"), cancellable = true)
    public void portalCubed$changeCollision(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((ClientPlayerEntity)(Object)this));
        if (portalBox != VoxelShapes.empty()) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean hasMovementInputPublic() {
        Vec2f vec2f = this.input.getMovementInput();
        return vec2f.x != 0.0F || vec2f.y != 0.0F;
    }

    @WrapOperation(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"
        )
    )
    private boolean noSprinting(ClientPlayerEntity instance, StatusEffect effect, Operation<Boolean> original) {
        if (PortalCubedClient.isPortalHudMode()) {
            return true;
        }
        return original.call(instance, effect);
    }

}
