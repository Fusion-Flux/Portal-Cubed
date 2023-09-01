package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.EntityLikeBlockEntity;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class EntityLikeBlockEntityModel<T extends EntityLikeBlockEntity> extends HierarchicalModel<BlockEntityWrapperEntity<T>> {
	protected EntityLikeBlockEntityModel(Function<ResourceLocation, RenderType> renderLayer) {
		super(renderLayer);
	}

	public abstract ResourceLocation getTexture(T entity);

	public ResourceLocation getEmissiveTexture(T entity) {
		return null;
	}
}
