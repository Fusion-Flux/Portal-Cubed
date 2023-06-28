package com.fusionflux.portalcubed.client.render.block.entity;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class QuadrantModel extends ForwardingBakedModel {
    public static final Bounds[] BOUNDS = {
        new Bounds(0, 2, 0, 2),
        new Bounds(-2, 0, 0, 2),
        new Bounds(-2, 0, -2, 0),
        new Bounds(0, 2, -2, 0)
    };

    public final int quadrant;

    public final Bounds bounds;

    public QuadrantModel(BakedModel wrapped, int quadrant) {
        this.wrapped = wrapped;
        this.quadrant = quadrant;
        this.bounds = BOUNDS[quadrant - 1];
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.pushTransform(this::cropQuad);
        context.bakedModelConsumer().accept(wrapped);
        context.popTransform();
    }

    private boolean cropQuad(MutableQuadView quad) {
        for (int i = 0; i < 4; i++) {
            float x = quad.posByIndex(i, 0);
            if (!bounds.containsX(x))
                return false;
            float z = quad.posByIndex(i, 2);
            if (!bounds.containsZ(z))
                return false;
            float y = quad.posByIndex(i, 1);
        }
        return true;
    }

    public record Bounds(float xMin, float xMax, float zMin, float zMax) {
        public boolean containsX(float x) {
            return x > xMin && x < xMax;
        }

        public boolean containsZ(float z) {
            return z > zMin && z < zMax;
        }
    }
}
