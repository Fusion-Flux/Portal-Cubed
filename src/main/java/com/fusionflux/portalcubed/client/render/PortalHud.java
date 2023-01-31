package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.items.PortalGun;
import com.fusionflux.portalcubed.items.PortalGunPrimary;
import com.fusionflux.portalcubed.items.PortalGunSecondary;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class PortalHud {
    private static final Identifier ROUND_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/gui/activeportalindicator.png");
    private static final Identifier SQUARE_TEXTURE = new Identifier(PortalCubed.MOD_ID, "textures/gui/activeportalindicator_square.png");

    public static void renderPortalLeft(@SuppressWarnings("unused") MatrixStack matrices, @SuppressWarnings("unused") float tickDelta) {
        //noinspection DataFlowIssue
        if (
            !MinecraftClient.getInstance().options.getPerspective().isFirstPerson() ||
                MinecraftClient.getInstance().interactionManager.getCurrentGameMode() == GameMode.SPECTATOR
        ) return;
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(PortalCubedConfig.enableRoundPortals) {
            RenderSystem.setShaderTexture(0, ROUND_TEXTURE);
        }else{
            RenderSystem.setShaderTexture(0, SQUARE_TEXTURE);
        }
        assert MinecraftClient.getInstance().player != null;

        if (MinecraftClient.getInstance().player.isHolding(PortalCubedItems.PORTAL_GUN) || MinecraftClient.getInstance().player.isHolding(PortalCubedItems.PORTAL_GUN_SECONDARY)) {
            ItemStack stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!stack.getItem().equals(PortalCubedItems.PORTAL_GUN) && !stack.getItem().equals(PortalCubedItems.PORTAL_GUN_SECONDARY)) {
                stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.OFFHAND);
            }

            NbtCompound tag = stack.getOrCreateNbt();
            NbtCompound portalsTag = tag.getCompound(MinecraftClient.getInstance().player.world.getRegistryKey().toString());

            int color = 0;
            if(stack.getItem().equals(PortalCubedItems.PORTAL_GUN)){
                // PortalGun gun = ((PortalGun) stack.getItem());
                color = Math.abs(((PortalGun) stack.getItem()).getColor(stack));
            }
            if(stack.getItem().equals(PortalCubedItems.PORTAL_GUN_SECONDARY)){
                //PortalGunSecondary gun = ((PortalGunSecondary) stack.getItem());
                color = Math.abs(((PortalGunSecondary) stack.getItem()).getColor(stack));
            }

            if (Math.abs(color) == 14842149) {
                color = -color;
            }

            if (color == -16383998) {
                color = 1908001;
            }

            if (color == 16383998) {
                color = -1908001;
            }

            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            boolean portalActive = false;
            assert MinecraftClient.getInstance().world != null;

            for (Entity globalPortal : MinecraftClient.getInstance().world.getEntities()) {
                if (portalsTag != null) {
                    if (portalsTag.contains("RightBackground")) {
                        if (globalPortal.getUuid().equals(portalsTag.getUuid("RightBackground"))) {
                            portalActive = true;
                        }
                    }
                }
            }

            if (!portalActive) {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 5, -100, 8, 16, 0 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            } else {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 5, -100, 8, 16, 8 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }
        }
    }

    public static void renderPortalRight(@SuppressWarnings("unused") MatrixStack matrices, @SuppressWarnings("unused") float tickDelta) {
        RenderSystem.enableBlend();
        if(PortalCubedConfig.enableRoundPortals) {
            RenderSystem.setShaderTexture(0, ROUND_TEXTURE);
        }else{
            RenderSystem.setShaderTexture(0, SQUARE_TEXTURE);
        }
        assert MinecraftClient.getInstance().player != null;

        if (MinecraftClient.getInstance().player.isHolding(PortalCubedItems.PORTAL_GUN) || MinecraftClient.getInstance().player.isHolding(PortalCubedItems.PORTAL_GUN_PRIMARY)) {
            ItemStack stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.MAINHAND);

            if (!stack.getItem().equals(PortalCubedItems.PORTAL_GUN) && !stack.getItem().equals(PortalCubedItems.PORTAL_GUN_PRIMARY)) {
                stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.OFFHAND);
            }

            NbtCompound tag = stack.getOrCreateNbt();
            NbtCompound portalsTag = tag.getCompound(MinecraftClient.getInstance().player.world.getRegistryKey().toString());

            int color = 0;
            if(stack.getItem().equals(PortalCubedItems.PORTAL_GUN)){
               // PortalGun gun = ((PortalGun) stack.getItem());
                color = Math.abs(((PortalGun) stack.getItem()).getColor(stack)) * -1;
            }
            if(stack.getItem().equals(PortalCubedItems.PORTAL_GUN_PRIMARY)){
                //PortalGunSecondary gun = ((PortalGunSecondary) stack.getItem());
                color = Math.abs(((PortalGunPrimary) stack.getItem()).getColor(stack)) * -1;
            }

            if (Math.abs(color) == 14842149) {
                color = -color;
            }

            if (color == -16383998) {
                color = 1908001;
            }

            if (color == 16383998) {
                color = -1908001;
            }

            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            boolean portalActive = false;
            assert MinecraftClient.getInstance().world != null;

            for (Entity globalPortal : MinecraftClient.getInstance().world.getEntities()) {
                if (portalsTag != null) {
                    if (portalsTag.contains("LeftBackground")) {
                        if (globalPortal.getUuid().equals(portalsTag.getUuid("LeftBackground"))) {
                            portalActive = true;
                        }
                    }
                }
            }

            if (!portalActive) {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 9, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 9, -100, 8, 16, 16 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            } else {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 9, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 9, -100, 8, 16, 24 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }
        }
    }

    public static void texture(int x, int y, int z, int width, int height, float u, float v, float uw, float vh, float r, float g, float b, float a) {
        //noinspection DataFlowIssue
        if (
            !MinecraftClient.getInstance().options.getPerspective().isFirstPerson() ||
                MinecraftClient.getInstance().interactionManager.getCurrentGameMode() == GameMode.SPECTATOR
        ) return;
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
