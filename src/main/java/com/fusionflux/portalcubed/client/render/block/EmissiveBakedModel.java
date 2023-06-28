package com.fusionflux.portalcubed.client.render.block;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.fabricmc.fabric.api.util.TriState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class EmissiveBakedModel extends ForwardingBakedModel {

    private static final Map<ResourceLocation, Function<BakedModel, EmissiveBakedModel>> WRAPPERS = new Object2ObjectOpenHashMap<>();

    public static void register(ResourceLocation modelId) {
        WRAPPERS.put(modelId, EmissiveBakedModel::new);
    }

    public static Optional<BakedModel> wrap(ResourceLocation modelId, BakedModel model) {
        if (!RenderMaterials.ARE_SUPPORTED)
            return Optional.empty();
        final Function<BakedModel, EmissiveBakedModel> wrapper = WRAPPERS.get(new ResourceLocation(modelId.getNamespace(), modelId.getPath()));
        if (wrapper != null) return Optional.of(wrapper.apply(model));
        return Optional.empty();
    }

    private static final Map<RenderMaterial, RenderMaterial> toEmissive = new ConcurrentHashMap<>();

    private final QuadTransform emissiveTransform;

    EmissiveBakedModel(BakedModel model) {
        this.wrapped = model;
        this.emissiveTransform = quad -> {
            TextureAtlasSprite sprite = getSpriteFinder().find(quad);
            if (EmissiveSpriteRegistry.isEmissive(sprite.contents().name())) {
                RenderMaterial material = quad.material();
                quad.material(getEmissiveMaterial(material));
            }
            return true;
        };
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.pushTransform(emissiveTransform);
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.pushTransform(emissiveTransform);
        super.emitItemQuads(stack, randomSupplier, context);
        context.popTransform();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState blockState, Direction face, RandomSource rand) {
        throw new UnsupportedOperationException("isVanillaAdapter is false! call emitBlockQuads/emitItemQuads!");
    }

    public static RenderMaterial getEmissiveMaterial(RenderMaterial base) {
        return toEmissive.computeIfAbsent(base, EmissiveBakedModel::makeEmissiveMaterial);
    }

    private static RenderMaterial makeEmissiveMaterial(RenderMaterial base) {
        return RenderMaterials.FINDER.copyFrom(base)
                .emissive(true)
                .disableDiffuse(true)
                .ambientOcclusion(TriState.FALSE)
                .find();
    }

    private static SpriteFinder getSpriteFinder() {
        TextureAtlas blockAtlas = Minecraft.getInstance()
                .getModelManager()
                .getAtlas(InventoryMenu.BLOCK_ATLAS);
        return SpriteFinder.get(blockAtlas);
    }
}
