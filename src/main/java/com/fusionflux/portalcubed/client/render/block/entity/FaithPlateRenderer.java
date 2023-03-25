package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class FaithPlateRenderer extends EntityLikeBlockEntityRenderer<FaithPlateBlockEntity, FaithPlateModel> {
    public static final EntityModelLayer FAITH_PLATE_LAYER = new EntityModelLayer(id("faith_plate"), "main");

    public FaithPlateRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx, FaithPlateModel::new);
    }

    @Override
    protected EntityModelLayer getModelLayer() {
        return FAITH_PLATE_LAYER;
    }
}
