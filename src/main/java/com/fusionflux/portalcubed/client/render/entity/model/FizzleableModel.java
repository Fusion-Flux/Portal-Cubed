package com.fusionflux.portalcubed.client.render.entity.model;

import com.fusionflux.portalcubed.entity.Fizzleable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

public abstract class FizzleableModel<T extends Entity & Fizzleable> extends EntityModel<T> {
    private float fizzleProgress;

    public FizzleableModel() {
        super();
    }

    public FizzleableModel(Function<ResourceLocation, RenderType> function) {
        super(function);
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        fizzleProgress = 1f - Math.min(entity.getFizzleProgress(), 1f);
    }

    @Override
    public final void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        renderFizzled(matrices, vertices, light, overlay, red * fizzleProgress, green * fizzleProgress, blue * fizzleProgress, alpha);
    }

    public abstract void renderFizzled(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);
}
