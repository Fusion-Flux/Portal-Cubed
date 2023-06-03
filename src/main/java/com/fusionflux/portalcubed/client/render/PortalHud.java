package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.items.PortalGun;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalHud {
    private static final ResourceLocation ROUND_TEXTURE = id("textures/gui/active_portal_indicator.png");
    private static final ResourceLocation SQUARE_TEXTURE = id("textures/gui/active_portal_indicator_square.png");

    public static void renderPortalLeft(@SuppressWarnings("unused") PoseStack matrices, @SuppressWarnings("unused") float tickDelta) {
        //noinspection DataFlowIssue
        if (
            !Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
                Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR
        ) return;
        RenderSystem.enableBlend();
        if (PortalCubedConfig.enableRoundPortals) {
            RenderSystem.setShaderTexture(0, ROUND_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, SQUARE_TEXTURE);
        }
        assert Minecraft.getInstance().player != null;

        if (Minecraft.getInstance().player.isHolding(s -> s.getItem() instanceof PortalGun)) {
            ItemStack stack = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.MAINHAND);

            if (!(stack.getItem() instanceof PortalGun)) {
                stack = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.OFFHAND);
            }

            int color = ((PortalGun) stack.getItem()).getColorForHudHalf(stack, false);

            float r = ((color & 0xFF0000) >>> 16) / 255f;
            float g = ((color & 0xFF00) >>> 8) / 255f;
            float b = (color & 0xFF) / 255f;
            assert Minecraft.getInstance().level != null;
            boolean portalActive = ((PortalGun) stack.getItem()).isSideActive(
                Minecraft.getInstance().level, stack, false
            );

            if (!portalActive) {
                texture(Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 9, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 9, -100, 8, 16, 16 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            } else {
                texture(Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 9, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 9, -100, 8, 16, 24 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }
        }
    }

    public static void renderPortalRight(@SuppressWarnings("unused") PoseStack matrices, @SuppressWarnings("unused") float tickDelta) {
        //noinspection DataFlowIssue
        if (
            !Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
                Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR
        ) return;
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (PortalCubedConfig.enableRoundPortals) {
            RenderSystem.setShaderTexture(0, ROUND_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, SQUARE_TEXTURE);
        }
        assert Minecraft.getInstance().player != null;

        if (Minecraft.getInstance().player.isHolding(s -> s.getItem() instanceof PortalGun)) {
            ItemStack stack = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!(stack.getItem() instanceof PortalGun)) {
                stack = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.OFFHAND);
            }

            int color = ((PortalGun) stack.getItem()).getColorForHudHalf(stack, true);

            float r = ((color & 0xFF0000) >>> 16) / 255f;
            float g = ((color & 0xFF00) >>> 8) / 255f;
            float b = (color & 0xFF) / 255f;
            assert Minecraft.getInstance().level != null;
            boolean portalActive = ((PortalGun) stack.getItem()).isSideActive(
                Minecraft.getInstance().level, stack, true
            );

            if (!portalActive) {
                texture(Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 5, -100, 8, 16, 0 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            } else {
                texture(Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 5, -100, 8, 16, 8 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }
        }
    }

    public static void texture(int x, int y, int z, int width, int height, float u, float v, float uw, float vh, float r, float g, float b, float a) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        RenderSystem.enableTexture();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(x, y + height, z).uv(u, v + vh).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x + width, y + height, z).uv(u + uw, v + vh).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x + width, y, z).uv(u + uw, v).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x, y, z).uv(u, v).color(r, g, b, a).endVertex();
        tessellator.end();
    }
}
