package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class BetaFaithPlateRenderer extends EntityLikeBlockEntityRenderer<FaithPlateBlockEntity, FaithPlateModel> {
    public static final ModelLayerLocation BETA_FAITH_PLATE_LAYER = new ModelLayerLocation(id("beta_faith_plate"), "main");

    public BetaFaithPlateRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, BetaFaithPlateModel::new);
    }

    @Override
    protected ModelLayerLocation getModelLayer() {
        return BETA_FAITH_PLATE_LAYER;
    }
}
