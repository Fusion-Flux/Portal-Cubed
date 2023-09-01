package com.fusionflux.portalcubed.mixin.client;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.HasMovementInputAccessor;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer implements HasMovementInputAccessor {

	@Shadow @Final protected Minecraft minecraft;

	@Shadow public Input input;

	public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
		super(clientLevel, gameProfile);
	}


	@Override
	public boolean hasMovementInputPublic() {
		Vec2 vec2f = this.input.getMoveVector();
		return vec2f.x != 0.0F || vec2f.y != 0.0F;
	}

	@Inject(
		method = "canStartSprinting",
		at = @At("HEAD"),
		cancellable = true
	)
	private void noSprintingInPortalHud(CallbackInfoReturnable<Boolean> cir) {
		if (PortalCubedClient.isPortalHudMode()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(
		method = "suffocatesAt",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;level()Lnet/minecraft/world/level/Level;"
		),
		cancellable = true
	)
	private void noSuffocate(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) AABB aabb) {
		final VoxelShape cutout = CalledValues.getPortalCutout(this);
		if (cutout.isEmpty()) return;
		if (Shapes.joinIsNotEmpty(Shapes.create(aabb), cutout, BooleanOp.AND)) {
			cir.setReturnValue(false);
		}
	}

}
