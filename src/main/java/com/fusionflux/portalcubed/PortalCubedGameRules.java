package com.fusionflux.portalcubed;

import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedGameRules {
    public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(
        id(PortalCubed.MOD_ID),
        Component.translatable("gamerule.category.portalcubed")
            .withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW)
    );

    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_CROUCH_FLY_GLITCH = GameRuleRegistry.register(
        "allowCrouchFlyGlitch", CATEGORY, GameRuleFactory.createBooleanRule(true, (server, rule) -> {
            final FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(rule.get());
            server.getPlayerList().broadcastAll(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_CFG, buf));
        })
    );
    public static final GameRules.Key<GameRules.IntegerValue> PORTAL_ALIGNMENT = GameRuleRegistry.register(
        "portalAlignment", CATEGORY, GameRuleFactory.createIntRule(16, 0)
    );
    public static final GameRules.Key<GameRules.BooleanValue> USE_PORTAL_HUD = GameRuleRegistry.register(
        "usePortalHud", CATEGORY, GameRuleFactory.createBooleanRule(false, (server, rule) -> {
            final FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(rule.get());
            server.getPlayerList().broadcastAll(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_PORTAL_HUD, buf));
        })
    );

    public static void register() {
    }
}
