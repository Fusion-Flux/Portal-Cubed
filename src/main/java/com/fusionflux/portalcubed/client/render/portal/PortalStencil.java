package com.fusionflux.portalcubed.client.render.portal;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import com.fusionflux.portalcubed.entity.Portal;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

public final class PortalStencil {
	private PortalStencil() { }

	public static void render(Portal portal, PoseStack matrices, Matrix4f projectionMatrix, ShaderInstance shader) {
		shader.MODEL_VIEW_MATRIX.set(matrices.last().pose());
		shader.PROJECTION_MATRIX.set(projectionMatrix);
		shader.apply();

		if (portal.getBoundingBox().intersects(Minecraft.getInstance().player.getBoundingBox())) GL11.glEnable(GL32.GL_DEPTH_CLAMP);

		// Build mesh
		final var tessellator = RenderSystem.renderThreadTesselator();
		final var bufferBuilder = tessellator.getBuilder();

		bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);

		bufferBuilder.vertex(.32, -.82, 0).endVertex();
		bufferBuilder.vertex(.32, .82, 0).endVertex();
		bufferBuilder.vertex(-.32, .82, 0).endVertex();

		bufferBuilder.vertex(-.32, .82, 0).endVertex();
		bufferBuilder.vertex(-.32, -.82, 0).endVertex();
		bufferBuilder.vertex(.32, -.82, 0).endVertex();

		BufferUploader.draw(bufferBuilder.end());
		shader.clear();
	}
}
