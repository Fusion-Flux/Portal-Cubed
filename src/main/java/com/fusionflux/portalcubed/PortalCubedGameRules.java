package com.fusionflux.portalcubed;

import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedGameRules {
    public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(
        id(PortalCubed.MOD_ID),
        Text.translatable("gamerule.category.portalcubed")
            .formatted(Formatting.BOLD, Formatting.YELLOW)
    );

    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_CROUCH_FLY_GLITCH = GameRuleRegistry.register(
        "allowCrouchFlyGlitch", CATEGORY, GameRuleFactory.createBooleanRule(true, (server, rule) -> {
            final PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(rule.get());
            server.getPlayerManager().sendToAll(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_CFG, buf));
        })
    );
    public static final GameRules.Key<GameRules.IntRule> PORTAL_ALIGNMENT = GameRuleRegistry.register(
        "portalAlignment", CATEGORY, GameRuleFactory.createIntRule(16, 0)
    );
    public static final GameRules.Key<GameRules.BooleanRule> USE_PORTAL_HUD = GameRuleRegistry.register(
        "usePortalHud", CATEGORY, GameRuleFactory.createBooleanRule(false, (server, rule) -> {
            final PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(rule.get());
            server.getPlayerManager().sendToAll(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_PORTAL_HUD, buf));
        })
    );
    public static final GameRules.Key<GameRules.BooleanRule> USE_PORTAL_FOG = GameRuleRegistry.register(
        "usePortalFog", CATEGORY, GameRuleFactory.createBooleanRule(false, (server, rule) -> {
            final PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(rule.get());
            server.getPlayerManager().sendToAll(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_PORTAL_FOG, buf));
        })
    );

    public static void register() {
    }
}
