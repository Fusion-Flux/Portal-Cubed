package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.EntityLikeBlockEntity;
import com.fusionflux.portalcubed.util.BlockEntityWrapperEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public abstract class EntityLikeBlockEntityModel<T extends EntityLikeBlockEntity> extends SinglePartEntityModel<BlockEntityWrapperEntity<T>> {
    protected EntityLikeBlockEntityModel(Function<Identifier, RenderLayer> renderLayer) {
        super(renderLayer);
    }

    public abstract Identifier getTexture(T entity);

    public Identifier getEmissiveTexture(T entity) {
        return null;
    }
}
