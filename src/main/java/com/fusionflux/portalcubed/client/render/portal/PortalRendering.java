package com.fusionflux.portalcubed.client.render.portal;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.mixin.client.LevelRendererAccessor;
import com.fusionflux.portalcubed.mixin.client.MinecraftAccessor;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;

public final class PortalRendering {
	private PortalRendering() { }

	private static ShaderInstance blitShader;
	private static RenderTarget PORTAL_TARGET = null;
	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f().identity();

	private static int layer = 0;

	private static RenderTarget createPortalTarget(int width, int height) {
		if (PORTAL_TARGET == null) PORTAL_TARGET = new PortalRenderTarget(width, height);
		if (PORTAL_TARGET.width != width || PORTAL_TARGET.height != height) PORTAL_TARGET.resize(width, height, Minecraft.ON_OSX);
		return PORTAL_TARGET;
	}

	public static boolean isRendering() {
		return layer != 0;
	}

	public static RenderTarget portalTarget() {
		return PORTAL_TARGET;
	}

	public static void renderScreenTriangle(int r, int g, int b) {
		final var shader = GameRenderer.getPositionColorShader();
		shader.MODEL_VIEW_MATRIX.set(IDENTITY_MATRIX);
		shader.PROJECTION_MATRIX.set(IDENTITY_MATRIX);
		shader.apply();

		final var tessellator = RenderSystem.renderThreadTesselator();
		final var bufferBuilder = tessellator.getBuilder();

		bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

		bufferBuilder.vertex(-1, -1, 0).color(r, g, b, 255).endVertex();
		bufferBuilder.vertex(-1, 3, 0).color(r, g, b, 255).endVertex();
		bufferBuilder.vertex(3, -1, 0).color(r, g, b, 255).endVertex();

		BufferUploader.draw(bufferBuilder.end());
		shader.clear();
	}

	private static void renderPortal(WorldRenderContext ctx, Portal portal) {
		final var mainTarget = Minecraft.getInstance().getMainRenderTarget();
		final var portalTarget = createPortalTarget(mainTarget.width, mainTarget.height);
		mainTarget.unbindWrite();
		portalTarget.bindWrite(false);
		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT, Minecraft.ON_OSX);
		{
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
			GL11.glStencilMask(0xFF);
			RenderSystem.colorMask(false, false, false, false);
			RenderSystem.depthMask(false);

			RenderSystem.enableDepthTest();
			RenderSystem.depthFunc(GL11.GL_LEQUAL);

			PortalStencil.render(portal, ctx.matrixStack(), ctx.projectionMatrix(), GameRenderer.getPositionShader());

			GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
			GL11.glStencilMask(0x00);
			RenderSystem.colorMask(true, true, true, true);
		}
		RenderSystem.enableCull();
		((MinecraftAccessor) Minecraft.getInstance()).setMainRenderTarget(portalTarget);

		final var oldFrustum = ((LevelRendererAccessor) ctx.worldRenderer()).portalcubed$getCullingFrustum();
		// TODO: Fix this
		((LevelRendererAccessor) ctx.worldRenderer()).portalcubed$setCullingFrustum(new Frustum(IDENTITY_MATRIX, IDENTITY_MATRIX));
		final var oldRenderChunksInFrustum = ((LevelRendererAccessor) ctx.worldRenderer()).portalcubed$getRenderChunksInFrustum();
		((LevelRendererAccessor) ctx.worldRenderer()).portalcubed$setCapturedFrustum(null);

		ctx.gameRenderer().setRenderHand(false);

		VirtualPortalCamera.setup(portal, ctx.tickDelta());
		{
			final var oldProjectionMatrix = RenderSystem.getProjectionMatrix();
			layer++;
			RenderSystem.disableDepthTest();
			((LevelRendererAccessor) ctx.worldRenderer()).portalcubed$setRenderChunksInFrustum(new ObjectArrayList<>());
			// TODO: Pose stack is leaking?????
			ctx.gameRenderer().renderLevel(ctx.tickDelta(), Util.getNanos(), new PoseStack());
			RenderSystem.enableDepthTest();
			layer--;
			ctx.gameRenderer().resetProjectionMatrix(oldProjectionMatrix);
		}
		VirtualPortalCamera.reset();

		{
			GL11.glDisable(GL11.GL_STENCIL_TEST);
			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
			GL11.glStencilMask(0xFF);
		}

		((LevelRendererAccessor) ctx.worldRenderer()).portalcubed$setCullingFrustum(oldFrustum);
		((LevelRendererAccessor) ctx.worldRenderer()).portalcubed$setRenderChunksInFrustum(oldRenderChunksInFrustum);

		portalTarget.unbindWrite();
		mainTarget.bindWrite(false);
		((MinecraftAccessor) Minecraft.getInstance()).setMainRenderTarget(mainTarget);
		GL11.glDisable(GL32.GL_DEPTH_CLAMP);
		RenderSystem.disableCull();

		blitShader.SCREEN_SIZE.set((float) portalTarget.width, (float) portalTarget.height);
		blitShader.setSampler("Sampler0", portalTarget.getColorTextureId());
		PortalStencil.render(portal, ctx.matrixStack(), ctx.projectionMatrix(), blitShader);
	}

	public static void init() {
		// TODO: DO NOT FORGET TO REMOVE
		System.loadLibrary("renderdoc");
		CoreShaderRegistrationCallback.EVENT.register(ctx -> ctx.register(PortalCubed.id("portal_blit"), DefaultVertexFormat.POSITION, shader -> blitShader = shader));
		WorldRenderEvents.END.register(ctx -> {
			if (PortalCubedConfig.portalRenderingLayers < 1) return;
			if (layer >= PortalCubedConfig.portalRenderingLayers) return;

			final var poseStack = ctx.matrixStack();

			poseStack.pushPose();
			poseStack.translate(-ctx.camera().getPosition().x, -ctx.camera().getPosition().y, -ctx.camera().getPosition().z);
			// TODO: Occlusion Culling?
			for (Entity entity : ctx.world().entitiesForRendering()) {
				if (entity instanceof Portal portal && portal.getOtherRotation().isPresent() && ctx.frustum().isVisible(portal.getBoundingBoxForCulling())) {
					poseStack.pushPose();
					poseStack.translate(portal.getPosition(ctx.tickDelta()).x, portal.getPosition(ctx.tickDelta()).y, portal.getPosition(ctx.tickDelta()).z);
					poseStack.mulPose(portal.getRotation().get(ctx.tickDelta()));
					poseStack.mulPose(Axis.YP.rotationDegrees(180));
					renderPortal(ctx, portal);
					poseStack.popPose();
				}
			}
			poseStack.popPose();
		});
	}
}
