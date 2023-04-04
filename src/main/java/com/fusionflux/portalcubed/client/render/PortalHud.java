package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.items.PortalGun;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalHud {
    private static final Identifier ROUND_TEXTURE = id("textures/gui/active_portal_indicator.png");
    private static final Identifier SQUARE_TEXTURE = id("textures/gui/active_portal_indicator_square.png");

    public static void renderPortalLeft(@SuppressWarnings("unused") MatrixStack matrices, @SuppressWarnings("unused") float tickDelta) {
        //noinspection DataFlowIssue
        if (
            !MinecraftClient.getInstance().options.getPerspective().isFirstPerson() ||
                MinecraftClient.getInstance().interactionManager.getCurrentGameMode() == GameMode.SPECTATOR
        ) return;
        RenderSystem.enableBlend();
        if (PortalCubedConfig.enableRoundPortals) {
            RenderSystem.setShaderTexture(0, ROUND_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, SQUARE_TEXTURE);
        }
        assert MinecraftClient.getInstance().player != null;

        if (MinecraftClient.getInstance().player.isHolding(s -> s.getItem() instanceof PortalGun)) {
            ItemStack stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.MAINHAND);

            if (!(stack.getItem() instanceof PortalGun)) {
                stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.OFFHAND);
            }

            int color = ((PortalGun) stack.getItem()).getColorForHudHalf(stack, false);

            float r = ((color & 0xFF0000) >>> 16) / 255f;
            float g = ((color & 0xFF00) >>> 8) / 255f;
            float b = (color & 0xFF) / 255f;
            assert MinecraftClient.getInstance().world != null;
            boolean portalActive = ((PortalGun) stack.getItem()).isSideActive(
                MinecraftClient.getInstance().world, stack, false
            );

            if (!portalActive) {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 9, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 9, -100, 8, 16, 16 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            } else {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 9, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 9, -100, 8, 16, 24 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }
        }
    }

    public static void renderPortalRight(@SuppressWarnings("unused") MatrixStack matrices, @SuppressWarnings("unused") float tickDelta) {
        //noinspection DataFlowIssue
        if (
            !MinecraftClient.getInstance().options.getPerspective().isFirstPerson() ||
                MinecraftClient.getInstance().interactionManager.getCurrentGameMode() == GameMode.SPECTATOR
        ) return;
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (PortalCubedConfig.enableRoundPortals) {
            RenderSystem.setShaderTexture(0, ROUND_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, SQUARE_TEXTURE);
        }
        assert MinecraftClient.getInstance().player != null;

        if (MinecraftClient.getInstance().player.isHolding(s -> s.getItem() instanceof PortalGun)) {
            ItemStack stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!(stack.getItem() instanceof PortalGun)) {
                stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.OFFHAND);
            }

            int color = ((PortalGun) stack.getItem()).getColorForHudHalf(stack, true);

            float r = ((color & 0xFF0000) >>> 16) / 255f;
            float g = ((color & 0xFF00) >>> 8) / 255f;
            float b = (color & 0xFF) / 255f;
            assert MinecraftClient.getInstance().world != null;
            boolean portalActive = ((PortalGun) stack.getItem()).isSideActive(
                MinecraftClient.getInstance().world, stack, true
            );

            if (!portalActive) {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 5, -100, 8, 16, 0 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            } else {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 5, -100, 8, 16, 8 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }
        }
    }

    public static void texture(int x, int y, int z, int width, int height, float u, float v, float uw, float vh, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        RenderSystem.enableTexture();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(x, y + height, z).uv(u, v + vh).color(r, g, b, a).next();
        bufferBuilder.vertex(x + width, y + height, z).uv(u + uw, v + vh).color(r, g, b, a).next();
        bufferBuilder.vertex(x + width, y, z).uv(u + uw, v).color(r, g, b, a).next();
        bufferBuilder.vertex(x, y, z).uv(u, v).color(r, g, b, a).next();
        tessellator.draw();
    }
}
