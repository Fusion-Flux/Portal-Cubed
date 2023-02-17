package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.entity.EnergyPelletEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class EnergyPelletRenderer extends EntityRenderer<EnergyPelletEntity> {
    private static final ItemStack ITEM = new ItemStack(PortalCubedItems.ENERGY_PELLET);

    public EnergyPelletRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(EnergyPelletEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, entity.getBoundingBox().getYLength() / 2 - 1 / 8f, 0);

        final MinecraftClient mc = MinecraftClient.getInstance();
        final Vec3d p1 = mc.gameRenderer.getCamera().getPos();
        final Vec3d diff = entity.getBoundingBox().getCenter().subtract(p1);

        //noinspection SuspiciousNameCombination
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(
            (float)Math.toDegrees(MathHelper.atan2(diff.x, diff.z)) + 180
        ));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(
            (float)Math.toDegrees(MathHelper.atan2(diff.y, MathHelper.sqrt((float) (diff.x * diff.x + diff.z * diff.z))))
        ));

        mc.getItemRenderer()
            .renderItem(ITEM, ModelTransformation.Mode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(EnergyPelletEntity entity) {
        return null;
    }
}
