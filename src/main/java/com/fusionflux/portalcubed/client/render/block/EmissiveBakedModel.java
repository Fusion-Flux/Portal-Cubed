package com.fusionflux.portalcubed.client.render.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class EmissiveBakedModel extends ForwardingBakedModel {

    private static final Map<ResourceLocation, Function<BakedModel, EmissiveBakedModel>> WRAPPERS = new Object2ObjectOpenHashMap<>();

    public static void register(ResourceLocation modelId) {
        WRAPPERS.put(modelId, EmissiveBakedModel::new);
    }

    public static Optional<BakedModel> wrap(ResourceLocation modelId, BakedModel model) {
        final Function<BakedModel, EmissiveBakedModel> wrapper = WRAPPERS.get(new ResourceLocation(modelId.getNamespace(), modelId.getPath()));
        if (wrapper != null) return Optional.of(wrapper.apply(model));
        return Optional.empty();
    }

    @SuppressWarnings("DataFlowIssue")
    private static final RenderMaterial DEFAULT_MATERIAL = RendererAccess.INSTANCE.getRenderer().materialById(RenderMaterial.MATERIAL_STANDARD);
    private static final RenderMaterial[] EMISSIVE_MATERIALS;
    static {
        BlendMode[] blendModes = BlendMode.values();
        EMISSIVE_MATERIALS = new RenderMaterial[blendModes.length];
        final var materialFinder = RendererAccess.INSTANCE.getRenderer().materialFinder();
        for (BlendMode blendMode : blendModes) {
            EMISSIVE_MATERIALS[blendMode.ordinal()] = materialFinder
                .emissive(true)
                .disableDiffuse(true)
                .ambientOcclusion(TriState.FALSE)
                .blendMode(blendMode)
                .find();
        }
    }

    private Pair<BlockState, Mesh> cachedMesh = Pair.of(null, null);

    EmissiveBakedModel(BakedModel model) {
        this.wrapped = model;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        final ModelObjects objects = ModelObjects.get();
        objects.cullingCache.prepare(pos, state);
        buildMesh(objects, state, randomSupplier);
        context.pushTransform(quad -> !objects.cullingCache.shouldCull(quad, blockView));
        context.meshConsumer().accept(cachedMesh.getValue());
        context.popTransform();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        final ModelObjects objects = ModelObjects.get();
        buildMesh(objects, null, randomSupplier);
        context.meshConsumer().accept(cachedMesh.getValue());
    }


    private void buildMesh(ModelObjects objects, @Nullable BlockState state, Supplier<RandomSource> randomSupplier) {
        boolean shouldBuild = true;
        if (state != null) shouldBuild = cachedMesh.getKey() != state;
        if (!shouldBuild) return;

        final QuadEmitter emitter = objects.meshBuilder.getEmitter();

        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            final Direction cullFace = ModelHelper.faceFromIndex(i);
            final List<BakedQuad> quads = wrapped.getQuads(state, cullFace, randomSupplier.get());

            for (BakedQuad quad : quads) {
                boolean isQuadEmissive = EmissiveSpriteRegistry.isEmissive(quad.getSprite().contents().name());

                BlendMode blendMode = BlendMode.DEFAULT;
                if (state != null) {
                    blendMode = BlendMode.fromRenderLayer(ItemBlockRenderTypes.getChunkRenderType(state));
                    if (blendMode == BlendMode.SOLID) blendMode = BlendMode.CUTOUT_MIPPED;
                }

                emitter.fromVanilla(quad, isQuadEmissive ? EMISSIVE_MATERIALS[blendMode.ordinal()] : DEFAULT_MATERIAL, cullFace);
                emitter.cullFace(cullFace);
                emitter.emit();
            }
        }

        cachedMesh = Pair.of(state, objects.meshBuilder.build());
    }


    private static class ModelObjects {

        private static final ThreadLocal<ModelObjects> INSTANCE = ThreadLocal.withInitial(ModelObjects::new);

        private final CullingCache cullingCache = new CullingCache();

        @SuppressWarnings("DataFlowIssue")
        private final MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();

        private static ModelObjects get() {
            return INSTANCE.get();
        }

    }

}
