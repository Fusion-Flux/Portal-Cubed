package com.fusionflux.thinkingwithportatos.client.render;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class PortalHud {
    private static final Identifier BASE_TEXTURE = new Identifier(ThinkingWithPortatos.MODID,"textures/gui/activeportalindicator.png");

    public static void renderPortalLeft(MatrixStack matrices, float tickDelta) {
        RenderSystem.enableBlend();
        MinecraftClient.getInstance().getTextureManager().bindTexture(BASE_TEXTURE);
        assert MinecraftClient.getInstance().player != null;

        if(MinecraftClient.getInstance().player.isHolding(ThinkingWithPortatosItems.PORTAL_GUN)||MinecraftClient.getInstance().player.isHolding(ThinkingWithPortatosItems.PORTAL_GUN_MODEL2)) {
            ItemStack stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.MAINHAND);
            if(!stack.getItem().equals(ThinkingWithPortatosItems.PORTAL_GUN)){
                if(!stack.getItem().equals(ThinkingWithPortatosItems.PORTAL_GUN_MODEL2)){
                    stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.OFFHAND);
                }
            }

            CompoundTag tag = stack.getOrCreateTag();
            CompoundTag portalsTag = tag.getCompound(MinecraftClient.getInstance().player.world.getRegistryKey().toString());
            PortalGun gun = (PortalGun)stack.getItem();
            int color = Math.abs(gun.getColor(stack));

            if(Math.abs(color)==14842149){
                color=-color;
            }

            if(color==-16383998){
                color=1908001;
            }

            if(color==16383998){
                color=-1908001;
            }

            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            boolean portalActive = false;
            assert MinecraftClient.getInstance().world != null;

            for (Entity globalportal : MinecraftClient.getInstance().world.getEntities()) {
                if(portalsTag!=null) {
                    if (portalsTag.contains("RightBackground")) {
                        if (globalportal.getUuid().equals(portalsTag.getUuid("RightBackground"))) {
                            portalActive = true;
                        }
                    }
                }
            }

            if (!portalActive) {
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 5, -100, 8, 16, 0 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }else{
                texture(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 5, -100, 8, 16, 8 / 256f, 0 / 256f, 8 / 256f, 16 / 256f, r, g, b, 1);
            }
        }
    }

    public static void renderPortalRight(MatrixStack matrices, float tickDelta) {
        RenderSystem.enableBlend();
        MinecraftClient.getInstance().getTextureManager().bindTexture(BASE_TEXTURE);
        assert MinecraftClient.getInstance().player != null;

        if (MinecraftClient.getInstance().player.isHolding(ThinkingWithPortatosItems.PORTAL_GUN)||MinecraftClient.getInstance().player.isHolding(ThinkingWithPortatosItems.PORTAL_GUN_MODEL2)) {
            ItemStack stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.MAINHAND);

            if(!stack.getItem().equals(ThinkingWithPortatosItems.PORTAL_GUN)){
                if(!stack.getItem().equals(ThinkingWithPortatosItems.PORTAL_GUN_MODEL2)){
                    stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.OFFHAND);
                }
            }

            CompoundTag tag = stack.getOrCreateTag();
            CompoundTag portalsTag = tag.getCompound(MinecraftClient.getInstance().player.world.getRegistryKey().toString());
            PortalGun gun = (PortalGun) stack.getItem();
            int color = Math.abs(gun.getColor(stack)) * -1;

            if (Math.abs(color) == 14842149) {
                color = -color;
            }

            if(color==-16383998){
                color=1908001;
            }

            if(color==16383998){
                color=-1908001;
            }

            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            boolean portalActive = false;
            assert MinecraftClient.getInstance().world != null;

            for (Entity globalportal : MinecraftClient.getInstance().world.getEntities()) {
                if(portalsTag!=null) {
                    if (portalsTag.contains("LeftBackground")) {
                        if (globalportal.getUuid().equals(portalsTag.getUuid("LeftBackground"))) {
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
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.enableTexture();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(x, y + height, z).texture(u, v + vh).color(r, g, b, a).next();
        bufferBuilder.vertex(x + width, y + height, z).texture(u + uw, v + vh).color(r, g, b, a).next();
        bufferBuilder.vertex(x + width, y, z).texture(u + uw, v).color(r, g, b, a).next();
        bufferBuilder.vertex(x, y, z).texture(u, v).color(r, g, b, a).next();
        tessellator.draw();
    }
}
