package com.fusionflux.portalcubed.client.render.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.fusionflux.portalcubed.accessor.BakedQuadExt;
import com.fusionflux.portalcubed.mixin.client.SimpleBakedModelAccessor;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class MultiRenderTypeSimpleBakedModel extends ForwardingBakedModel {
	public static final Map<String, Supplier<RenderMaterial>> SUPPORTED_TYPES = Map.of(
			"default", () -> RenderMaterials.DEFAULT_MATERIAL,
			"solid", () -> RenderMaterials.SOLID_MATERIAL,
			"cutout", () -> RenderMaterials.CUTOUT_MATERIAL,
			"translucent", () -> RenderMaterials.TRANSLUCENT_MATERIAL
	);
	public static final String SUPPORTED_TYPE_LIST = String.join(", ", SUPPORTED_TYPES.keySet());

	protected List<Triple<BakedQuad, RenderMaterial, Direction>> quads = new ArrayList<>();

	public MultiRenderTypeSimpleBakedModel(SimpleBakedModel model) {
		this.wrapped = model;

		List<BakedQuad> unculled = ((SimpleBakedModelAccessor) model).getUnculledFaces();
		unculled.forEach(quad -> addQuad(quad, null));

		// side -> list of quads *not* culled
		Map<Direction, List<BakedQuad>> culled = ((SimpleBakedModelAccessor) model).getCulledFaces();
		culled.forEach((cullFace, quads) -> quads.forEach(quad -> addQuad(quad, cullFace)));
	}

	private void addQuad(BakedQuad quad, @Nullable Direction cullFace) {
		String renderType = ((BakedQuadExt) quad).portalcubed$getRenderType();
		if (renderType == null)
			renderType = "default";
		RenderMaterial material = parseType(renderType);
		this.quads.add(Triple.of(quad, material, cullFace));
	}

	public void forEachQuad(TriConsumer<BakedQuad, RenderMaterial, Direction> consumer) {
		quads.forEach(triple -> consumer.accept(triple.getLeft(), triple.getMiddle(), triple.getRight()));
	}

	private TriConsumer<BakedQuad, RenderMaterial, Direction> emitTo(QuadEmitter emitter) {
		return (quad, material, cullFace) -> {
			emitter.fromVanilla(quad, material, cullFace);
			emitter.emit();
		};
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		forEachQuad(emitTo(context.getEmitter()));
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		forEachQuad(emitTo(context.getEmitter()));
	}

	@Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction face, RandomSource rand) {
		throw new UnsupportedOperationException("isVanillaAdapter is false! call emitBlockQuads/emitItemQuads!");
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	public static RenderMaterial parseType(String name) throws JsonParseException {
		Supplier<RenderMaterial> type = SUPPORTED_TYPES.get(name);
		if (type != null)
			return type.get();
		throw new JsonParseException(name + " is not a supported RenderType. must be one of: " + SUPPORTED_TYPE_LIST);
	}
}
