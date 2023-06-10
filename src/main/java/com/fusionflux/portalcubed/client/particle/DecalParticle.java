package com.fusionflux.portalcubed.client.particle;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.particle.DecalParticleEffect;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ClientOnly
public class DecalParticle extends Particle {
    public static final ParticleRenderType PARTICLE_SHEET_MULTIPLY = new ParticleRenderType() {
        @Override
        @SuppressWarnings("deprecation")
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.SRC_COLOR);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_MULTIPLY";
        }
    };

    private final TextureAtlasSprite sprite;
    private final Direction direction;
    private final boolean multiply;

    public DecalParticle(
        ClientLevel world,
        double x, double y, double z,
        TextureAtlasSprite sprite, Direction direction,
        boolean multiply
    ) {
        super(world, x, y, z);
        this.sprite = sprite;
        this.direction = direction;
        this.multiply = multiply;
        lifetime = 1400;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        final Vec3 cameraPos = camera.getPosition();
        final float x = (float)(Mth.lerp(tickDelta, xo, this.x) - cameraPos.x());
        final float y = (float)(Mth.lerp(tickDelta, yo, this.y) - cameraPos.y());
        final float z = (float)(Mth.lerp(tickDelta, zo, this.z) - cameraPos.z());
        final Quaternionf rotation = direction.getRotation();

        final Vector3f[] vertices = {
            new Vector3f(-0.5f, 0f, -0.5f),
            new Vector3f(-0.5f, 0f, 0.5f),
            new Vector3f(0.5f, 0f, 0.5f),
            new Vector3f(0.5f, 0f, -0.5f)
        };

        for (final Vector3f vertex : vertices) {
            vertex.rotate(rotation);
            vertex.add(x, y, z);
        }

        alpha = 1f;
        if (age + 100 >= lifetime) {
            final float past100 = (age + tickDelta) - lifetime + 100;
            alpha = 1f - Mth.clamp(past100 / 100f, 0f, 1f);
        }

        final float minU = sprite.getU0();
        final float maxU = sprite.getU1();
        final float minV = sprite.getV0();
        final float maxV = sprite.getV1();
        final int brightness = getLightColor(tickDelta);
        vertexConsumer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
            .uv(maxU, maxV)
            .color(rCol, gCol, bCol, alpha)
            .uv2(brightness)
            .endVertex();
        vertexConsumer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
            .uv(maxU, minV)
            .color(rCol, gCol, bCol, alpha)
            .uv2(brightness)
            .endVertex();
        vertexConsumer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
            .uv(minU, minV)
            .color(rCol, gCol, bCol, alpha)
            .uv2(brightness)
            .endVertex();
        vertexConsumer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
            .uv(minU, maxV)
            .color(rCol, gCol, bCol, alpha)
            .uv2(brightness)
            .endVertex();
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return multiply ? PARTICLE_SHEET_MULTIPLY : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @ClientOnly
    public static class Factory implements ParticleProvider<DecalParticleEffect> {
        private final FabricSpriteProvider spriteProvider;

        private List<TextureAtlasSprite> cacheKey;
        private final Map<ResourceLocation, TextureAtlasSprite> spriteCache = new HashMap<>();

        public Factory(FabricSpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DecalParticleEffect parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            final TextureAtlasSprite sprite = getSpriteCache().get(parameters.getTexture());
            if (sprite == null) {
                PortalCubed.LOGGER.warn("Unknown decal particle texture {}", parameters.getTexture());
                return null;
            }
            return new DecalParticle(world, x, y, z, sprite, parameters.getDirection(), parameters.isMultiply());
        }

        private Map<ResourceLocation, TextureAtlasSprite> getSpriteCache() {
            final List<TextureAtlasSprite> sprites = spriteProvider.getSprites();
            if (sprites != cacheKey) {
                cacheKey = sprites;
                spriteCache.clear();
                for (final TextureAtlasSprite sprite : sprites) {
                    spriteCache.put(sprite.contents().name(), sprite);
                }
            }
            return spriteCache;
        }
    }
}
