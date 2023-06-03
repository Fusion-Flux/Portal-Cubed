package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.HasMovementInputAccessor;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayer implements HasMovementInputAccessor {

    @Shadow @Final protected Minecraft minecraft;

    @Shadow public Input input;

    public ClientPlayerEntityMixin(ClientLevel clientWorld, GameProfile gameProfile, @Nullable ProfilePublicKey playerPublicKey) {
        super(clientWorld, gameProfile, playerPublicKey);
    }


    @Inject(method = "suffocatesAt", at = @At("HEAD"), cancellable = true)
    public void portalCubed$changeCollision(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        VoxelShape portalBox = CalledValues.getPortalCutout(((LocalPlayer)(Object)this));
        if (portalBox != Shapes.empty()) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean hasMovementInputPublic() {
        Vec2 vec2f = this.input.getMoveVector();
        return vec2f.x != 0.0F || vec2f.y != 0.0F;
    }

    @WrapOperation(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"
        )
    )
    private boolean noSprinting(LocalPlayer instance, MobEffect effect, Operation<Boolean> original) {
        if (PortalCubedClient.isPortalHudMode()) {
            return true;
        }
        return original.call(instance, effect);
    }

}
