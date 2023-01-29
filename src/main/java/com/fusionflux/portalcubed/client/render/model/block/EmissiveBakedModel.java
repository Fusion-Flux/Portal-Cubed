package com.fusionflux.portalcubed.client.render.model.block;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockRenderView;

import java.util.function.Supplier;

public final class EmissiveBakedModel extends ForwardingBakedModel {

    private static final RenderMaterial[] EMISSIVE_MATERIALS = new RenderMaterial[BlendMode.values().length]; static {
        //noinspection DataFlowIssue
        MaterialFinder materialFinder = RendererAccess.INSTANCE.getRenderer().materialFinder();
        for (BlendMode blendMode : BlendMode.values()) {
            EMISSIVE_MATERIALS[blendMode.ordinal()] = materialFinder
                .emissive(0, true)
                .disableDiffuse(0, true)
                .disableAo(0, true)
                .blendMode(0, blendMode)
                .find();
        }
    }

    private final Identifier emissiveSpriteId;
    private boolean didEmit = false;

    public EmissiveBakedModel(BakedModel wrapped, Identifier emissiveSpriteId) {
        this.wrapped = wrapped;
        this.emissiveSpriteId = emissiveSpriteId;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<RandomGenerator> randomSupplier, RenderContext context) {
        //noinspection DataFlowIssue
        final MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        final QuadEmitter emitter = meshBuilder.getEmitter();

        context.pushTransform(quad -> {
            SpriteFinder spriteFinder = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().getAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));

            Sprite sprite = spriteFinder.find(quad, 0);
            if (emissiveSpriteId.equals(sprite.getId())) {
                BlendMode blendMode = BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state));
                final BlendMode finalBlendMode = blendMode == BlendMode.SOLID ? BlendMode.CUTOUT_MIPPED : blendMode;

                quad.copyTo(emitter);
                emitter.material(EMISSIVE_MATERIALS[finalBlendMode.ordinal()]);
                emitter.emit();
                didEmit = true;

                return false;
            }

            return true;
        });
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();

        if (didEmit) context.meshConsumer().accept(meshBuilder.build());
        didEmit = false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void emitItemQuads(ItemStack stack, Supplier<RandomGenerator> randomSupplier, RenderContext context) {
        //noinspection DataFlowIssue
        final MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        final QuadEmitter emitter = meshBuilder.getEmitter();

        context.pushTransform(quad -> {
            SpriteFinder spriteFinder = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().getAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));

            Sprite sprite = spriteFinder.find(quad, 0);
            if (emissiveSpriteId.equals(sprite.getId())) {
                quad.copyTo(emitter);
                emitter.material(EMISSIVE_MATERIALS[BlendMode.DEFAULT.ordinal()]);
                emitter.emit();
                didEmit = true;

                return false;
            }

            return true;
        });
        super.emitItemQuads(stack, randomSupplier, context);
        context.popTransform();

        if (didEmit) context.meshConsumer().accept(meshBuilder.build());
        didEmit = false;
    }

}
