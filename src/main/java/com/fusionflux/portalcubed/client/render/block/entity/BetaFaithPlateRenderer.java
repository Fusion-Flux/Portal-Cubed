package com.fusionflux.portalcubed.client.render.block.entity;

import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class BetaFaithPlateRenderer extends EntityLikeBlockEntityRenderer<FaithPlateBlockEntity, FaithPlateModel> {
    public static final EntityModelLayer BETA_FAITH_PLATE_LAYER = new EntityModelLayer(id("beta_faith_plate"), "main");

    public BetaFaithPlateRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx, BetaFaithPlateModel::new);
    }

    @Override
    protected EntityModelLayer getModelLayer() {
        return BETA_FAITH_PLATE_LAYER;
    }
}
