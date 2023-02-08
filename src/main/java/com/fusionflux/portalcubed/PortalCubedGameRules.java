package com.fusionflux.portalcubed;

import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedGameRules {
    public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(
        id(PortalCubed.MOD_ID), Text.translatable("gamerule.category.portalcubed")
    );

    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_CROUCH_FLY_GLITCH = GameRuleRegistry.register(
        "allowCrouchFlyGlitch", CATEGORY, GameRuleFactory.createBooleanRule(true, (server, rule) -> {
            final PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(rule.get());
            server.getPlayerManager().sendToAll(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_CFG, buf));
        })
    );

    public static void register() {
    }
}
