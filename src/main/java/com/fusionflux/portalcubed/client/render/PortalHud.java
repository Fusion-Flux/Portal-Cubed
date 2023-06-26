package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.mojang.blaze3d.platform.Window;
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

    public static void renderPortals(@SuppressWarnings("unused") PoseStack matrices, @SuppressWarnings("unused") float tickDelta) {
        renderPortalSide(false, -9, -9, 16);
        renderPortalSide(true, 0, -5, 0);
    }

    private static void renderPortalSide(boolean rightSide, int xOffset, int yOffset, int uOffset) {
        final Minecraft minecraft = Minecraft.getInstance();
        //noinspection DataFlowIssue
        if (
            !minecraft.options.getCameraType().isFirstPerson() ||
                minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR
        ) return;
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        if (PortalCubedConfig.enableRoundPortals) {
            RenderSystem.setShaderTexture(0, ROUND_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, SQUARE_TEXTURE);
        }
        assert minecraft.player != null;

        if (minecraft.player.isHolding(s -> s.getItem() instanceof PortalGun)) {
            final CorePhysicsEntity heldEntity = PortalCubedComponents.HOLDER_COMPONENT.get(minecraft.player).entityBeingHeld();
            if (heldEntity != null && !heldEntity.getType().is(PortalCubedEntities.P1_ENTITY)) return;

            ItemStack stack = minecraft.player.getItemBySlot(EquipmentSlot.MAINHAND);

            if (!(stack.getItem() instanceof PortalGun)) {
                stack = minecraft.player.getItemBySlot(EquipmentSlot.OFFHAND);
            }
            final PortalGun portalGun = (PortalGun)stack.getItem();

            int color = heldEntity == null
                ? portalGun.getColorForHudHalf(stack, rightSide)
                : 0xe4c9b1;

            float r = ((color & 0xFF0000) >>> 16) / 255f;
            float g = ((color & 0xFF00) >>> 8) / 255f;
            float b = (color & 0xFF) / 255f;
            assert minecraft.level != null;
            boolean portalActive = heldEntity == null && portalGun.isSideActive(minecraft.level, stack, rightSide);

            final Window window = minecraft.getWindow();
            if (!portalActive) {
                texture(window.getGuiScaledWidth() / 2 + xOffset, window.getGuiScaledHeight() / 2 + yOffset, -100, 8, 16, uOffset / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            } else {
                texture(window.getGuiScaledWidth() / 2 + xOffset, window.getGuiScaledHeight() / 2 + yOffset, -100, 8, 16, (uOffset + 8) / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }
        }
    }

    private static void texture(int x, int y, int z, int width, int height, float u, float v, float uw, float vh, float r, float g, float b, float a) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(x, y + height, z).uv(u, v + vh).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x + width, y + height, z).uv(u + uw, v + vh).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x + width, y, z).uv(u + uw, v).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x, y, z).uv(u, v).color(r, g, b, a).endVertex();
        tessellator.end();
    }
}
