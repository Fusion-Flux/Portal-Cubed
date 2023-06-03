package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class FaithPlateRenderer extends EntityLikeBlockEntityRenderer<FaithPlateBlockEntity, FaithPlateModel> {
    public static final ModelLayerLocation FAITH_PLATE_LAYER = new ModelLayerLocation(id("faith_plate"), "main");

    public FaithPlateRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, FaithPlateModel::new);
    }

    @Override
    protected ModelLayerLocation getModelLayer() {
        return FAITH_PLATE_LAYER;
    }
}
